/*
 * SyncTimestampDao.java 2009-5-14 ÉÏÎç09:27:59
 *  
 */
package pro.vdes.mms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import common.util.DBUtil;

/**
 * @author QiuSH
 * @version 1.0
 * 
 */
public class SyncTimestampDao {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 
	 */
	private Date initTimestamp = null;

	/**
	 * 
	 */
	public SyncTimestampDao() {
	}

	/**
	 * @param initTimestamp
	 */
	public SyncTimestampDao(Date initTimestamp) {
		this.initTimestamp = initTimestamp;
	}

	/**
	 * @param current
	 * @param type
	 * @return
	 */
	public boolean updateTimestamp(Date timestamp, String type) {
		String sql = "UPDATE Sync_Timestamp SET lastTime = ? WHERE contentType = ?";
		log.debug(sql);
		log.info("Update last timestamp " + timestamp + "@" + type);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBUtil.getConnection("proxool.vdes");
			pstmt = conn.prepareStatement(sql);
			pstmt.setTimestamp(1, new Timestamp(timestamp.getTime()));
			pstmt.setString(2, type);

			int row = pstmt.executeUpdate();
			return row == 1;
		} catch (SQLException e) {
			log.error(e);
		} finally {
			DBUtil.releaseStatement(pstmt);
			DBUtil.releaseConnection(conn);
		}
		return false;
	}

	/**
	 * @param type
	 * @return
	 */
	public Date getTimestamp(String type) {
		Date timestamp = getInitTimestamp();

		String sql = "SELECT lastTime FROM Sync_Timestamp WHERE contentType = ?";
		log.debug(sql);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBUtil.getConnection("proxool.vdes");
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, type);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				timestamp.setTime(rs.getTimestamp(1).getTime());
				log.info("Getted last timestamp is " + timestamp + "@" + type);
				return timestamp;
			} else {
				insertTimestamp(timestamp, type);
			}

		} catch (SQLException e) {
			log.error(e);
		} finally {
			DBUtil.releaseStatement(pstmt);
			DBUtil.releaseConnection(conn);
		}

		log.debug("Getted default last timestamp is " + timestamp + "|" + type);
		return timestamp;
	}

	/**
	 * @return
	 */
	private Date getInitTimestamp() {
		if (initTimestamp != null) {
			return initTimestamp;
		}
		Calendar current = Calendar.getInstance();
		current.add(Calendar.MINUTE, -1);
		current.set(Calendar.SECOND, 0);
		current.set(Calendar.MILLISECOND, 0);
		return current.getTime();
	}

	/**
	 * @param timestamp
	 * @param type
	 * @return
	 */
	public boolean insertTimestamp(Date timestamp, String type) {
		String sql = "INSERT INTO Sync_Timestamp(lastTime, contentType) VALUES(?, ?)";
		log.debug(sql);
		log.info("INSERT new timestamp " + timestamp + "|" + type);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBUtil.getConnection("proxool.vdes");
			pstmt = conn.prepareStatement(sql);
			pstmt.setTimestamp(1, new Timestamp(timestamp.getTime()));
			pstmt.setString(2, type);

			int row = pstmt.executeUpdate();
			return row == 1;
		} catch (SQLException e) {
			log.error(e);
		} finally {
			DBUtil.releaseStatement(pstmt);
			DBUtil.releaseConnection(conn);
		}
		return false;
	}
}
