package pro.vdes.mms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import common.util.DBUtil;

/**
 * <ol>
 * <li>sync data from icare to local SQL Server</li>
 * <li>do stat (the total quantity and the success quantity in an hour for each
 * city)</li>
 * <li>save the result to the table</li>
 * </ol>
 * 
 * @author SHua.Qiu
 * 
 */
public class Stater {

	public class MmsPerf {

		private Date sendTime = null;

		private String city = null;

		private boolean isSuccess = false;

		public MmsPerf(Date sendTime, String city, boolean isSuccess) {
			this.sendTime = sendTime;
			this.city = city;
			this.isSuccess = isSuccess;
		}

		public Date getSendTime() {
			return sendTime;
		}

		public String getCity() {
			return city;
		}

		public boolean isSuccess() {
			return isSuccess;
		}

	}

	public class StatResult {
		private String sendTime = null;

		private String city = null;

		private long total = 0;

		private long success = 0;

		public StatResult(String sendTime, String city) {
			this.sendTime = sendTime;
			this.city = city;
		}

		public void addTotal(long amount) {
			total += amount;
		}

		public void addSuccess(long amount) {
			success += amount;
		}

		public String getSendTime() {
			return sendTime;
		}

		public String getCity() {
			return city;
		}

		public long getTotal() {
			return total;
		}

		public long getSuccess() {
			return success;
		}

	}

	private Logger log = Logger.getLogger(getClass());

	private static final int BATCH_SIZE = 2000;

	private static final String SRC_TABLE = "mmsperf";

	private SimpleDateFormat format = null;

	private String startTime = null;

	private String endTime = null;

	public Stater(Calendar startTime, Calendar endTime) {
		this(startTime.getTime(), endTime.getTime());
	}

	public Stater(Date startTime, Date endTime) {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		this.startTime = format.format(startTime);
		this.endTime = format.format(endTime);

		log.info("New stater from " + startTime + " to " + endTime);
	}

	/**
	 * 
	 */
	private Connection getMmscConnection() {
		Connection mmscConn = DBUtil.getConnection("proxool.mmsc");
		log.debug("mmsc connection init");
		return mmscConn;
	}

	private Connection getVdesConnection() {
		Connection conn = DBUtil.getConnection("proxool.vdes");
		log.debug("vdes connection init");

		return conn;
	}

	public void doSync() throws SQLException {
		log.info("Sync start");
		String startDay = startTime.substring(0, 10);
		String endDay = endTime.substring(0, 10);

		if (startDay.equals(endDay)) {
			doSync(startDay.replace("-", ""), startTime, endTime);
		} else {
			doSync(startDay.replace("-", ""), startTime, null);
			doSync(endDay.replace("-", ""), null, endTime);
		}
		log.info("Sync finished");
	}

	/**
	 * @param day
	 * @param startTime
	 * @param endTime
	 * @throws SQLException
	 */
	protected void doSync(String day, String startTime, String endTime)
			throws SQLException {
		String sql = "SELECT * FROM " + SRC_TABLE + day;
		if (startTime != null) {
			sql += " WHERE DATE_FORMAT(sendTime, '%Y-%m-%d %H:%i') >= ?";
		}
		if (endTime != null) {
			if (startTime == null) {
				sql += " WHERE DATE_FORMAT(sendTime, '%Y-%m-%d %H:%i') <= ?";
			} else {
				sql += " AND DATE_FORMAT(sendTime, '%Y-%m-%d %H:%i') <= ?";
			}
		}
		log.debug(sql);

		Connection mmscConn = getMmscConnection();
		PreparedStatement pstmt = mmscConn.prepareStatement(sql);
		int index = 1;
		if (startTime != null) {
			pstmt.setString(index++, startTime);
		}
		if (endTime != null) {
			pstmt.setString(index++, endTime);
		}
		ResultSet rs = pstmt.executeQuery();

		ResultSetMetaData metaData = rs.getMetaData();
		String insertSql = getInsertSql(metaData);
		log.debug(insertSql);

		Connection conn = getVdesConnection();
		PreparedStatement insertPstmt = conn.prepareStatement(insertSql);

		int columnCount = metaData.getColumnCount();
		int count = 0;
		while (rs.next()) {
			for (int column = 1; column <= columnCount; column++) {
				Object obj = rs.getObject(column);
				if (obj == null) {
					insertPstmt.setNull(column, Types.JAVA_OBJECT);
				} else {
					insertPstmt.setObject(column, obj);
				}
			}
			insertPstmt.addBatch();

			if ((++count) % BATCH_SIZE == 0) {
				executeBatch(insertPstmt);
				log.info("Sync " + count + " record");
			}
		}
		if (count % BATCH_SIZE != 0) {
			executeBatch(insertPstmt);
			log.info("Sync " + count + " record");
		}

		DBUtil.release(insertPstmt, conn);
		DBUtil.release(rs, pstmt, mmscConn);
	}

	/**
	 * @param metaData
	 * @return
	 * @throws SQLException
	 */
	private String getInsertSql(ResultSetMetaData metaData) throws SQLException {
		int columnCount = metaData.getColumnCount();

		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		for (int i = 1; i <= columnCount; i++) {
			if (i > 1) {
				columns.append(", ");
				values.append(", ");
			}
			columns.append(metaData.getColumnName(i));
			values.append("?");
		}

		String insertSql = "INSERT INTO MMS_PerfTmp(" + columns.toString()
				+ ")" + " VALUES(" + values.toString() + ")";
		return insertSql;
	}

	@Deprecated
	public List<MmsPerf> getDatas() throws SQLException {
		List<MmsPerf> datas = new ArrayList<MmsPerf>();

		String sql = "SELECT sendTime, city, statusCode FROM MMS_PerfTmp"
				+ " WHERE CONVERT(VARCHAR(16), sendTime, 120) >= ?"
				+ " AND CONVERT(VARCHAR(16), sendTime, 120) <= ?";
		log.debug(sql);

		Connection conn = getVdesConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, startTime);
		pstmt.setString(2, endTime);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			Date sendTime = rs.getTimestamp("sendTime");
			String city = rs.getString("city").trim();
			boolean isSuccess = rs.getInt("statusCode") <= 2000;
			datas.add(new MmsPerf(sendTime, city, isSuccess));
		}
		log.info("Get " + datas.size() + " records");

		DBUtil.release(rs, pstmt, conn);

		return datas;
	}

	/**
	 * @param datas
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public Map<String, StatResult> doStat(List<MmsPerf> datas)
			throws SQLException {
		Map<String, StatResult> results = new HashMap<String, StatResult>();
		log.info("start stat");
		for (MmsPerf perf : datas) {
			String sendTime = format.format(perf.getSendTime());
			String city = perf.getCity();

			String key = getKey(sendTime, city);
			StatResult result = results.get(key);
			if (result == null) {
				log.info("There are not result in the " + city + " at "
						+ sendTime + ", create a new one");
				result = new StatResult(sendTime, city);
				results.put(key, result);
			}
			result.addTotal(1);
			if (perf.isSuccess()) {
				result.addSuccess(1);
			}
			log.info(result.getSendTime() + "@" + result.getCity() + "="
					+ result.getTotal() + "|" + result.getSuccess());
		}

		return results;
	}

	public Map<String, StatResult> doStat() throws SQLException {
		log.info("Start stat");
		Map<String, StatResult> results = new HashMap<String, StatResult>();
		doStat(results, false);
		doStat(results, true);
		log.info("Stat end");
		return results;
	}

	private Map<String, StatResult> doStat(Map<String, StatResult> results,
			boolean isToGetSucCount) throws SQLException {

		String sql = "SELECT COUNT(1) AS cnt, city, CONVERT(VARCHAR(16), sendtime, 120) AS stime FROM MMS_PerfTmp"
				+ " WHERE CONVERT(VARCHAR(16), sendTime, 120) >= ? AND CONVERT(VARCHAR(16), sendTime, 120) <= ?";
		if (isToGetSucCount) {
			sql += " AND statusCode <= 2000";
		}
		sql += " GROUP BY city, CONVERT(VARCHAR(16), sendtime, 120)";
		log.debug(sql);

		Connection conn = getVdesConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, startTime);
		pstmt.setString(2, endTime);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String sendTime = rs.getString("stime");
			String city = rs.getString("city").trim();
			long count = rs.getLong("cnt");

			String key = getKey(sendTime, city);
			StatResult result = results.get(key);
			if (result == null) {
				log.info("There is not result in the " + city + " at "
						+ sendTime + ", create a new one");
				result = new StatResult(sendTime, city);
				results.put(key, result);
			}
			if (isToGetSucCount) {
				result.addSuccess(count);
			} else {
				result.addTotal(count);
			}

			log.info(result.getSendTime() + "@" + result.getCity() + " = "
					+ result.getTotal() + "|" + result.getSuccess());
		}
		log.info("Get " + results.size() + " records");

		DBUtil.release(rs, pstmt, conn);

		return results;
	}

	private String getKey(String sendTime, String city) {
		return sendTime + "@" + city;
	}

	public void doSave(Map<String, StatResult> results) throws SQLException {
		String updateSql = "UPDATE MMS_StatResult SET total = total + ?, success = success + ?"
				+ " WHERE sendTime = ? AND city = ?";
		log.debug(updateSql);
		String insertSql = "INSERT INTO MMS_StatResult(total, success, sendTime, city)"
				+ "VALUES(?, ?, ?, ?)";
		log.debug(insertSql);

		Connection conn = getVdesConnection();
		PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
		PreparedStatement insertPstmt = conn.prepareStatement(insertSql);

		int updateCount = 0;
		int insertCount = 0;
		Map<String, String> existResults = getExistResult();
		for (StatResult result : results.values()) {
			String key = getKey(result.getSendTime(), result.getCity());
			if (existResults.containsKey(key)) {
				setParam(updatePstmt, result);

				updatePstmt.addBatch();
				if ((++updateCount) % BATCH_SIZE == 0) {
					executeBatch(updatePstmt);
					log.info("Update " + updateCount
							+ " record in MMS_StatResult");
				}
				// updatePstmt.executeUpdate();
			} else {
				setParam(insertPstmt, result);

				insertPstmt.addBatch();
				if ((++insertCount) % BATCH_SIZE == 0) {
					executeBatch(insertPstmt);
					log.info("Insert " + insertCount
							+ " record to MMS_StatResult");
				}
				// insertPstmt.executeUpdate();
			}
		}
		if (updateCount % BATCH_SIZE != 0) {
			executeBatch(updatePstmt);
			log.info("Update " + updateCount + " record in MMS_StatResult");
		}
		if (insertCount % BATCH_SIZE != 0) {
			executeBatch(insertPstmt);
			log.info("Insert " + insertCount + " record to MMS_StatResult");
		}
		DBUtil.releaseStatement(updatePstmt);
		DBUtil.releaseStatement(insertPstmt);
		DBUtil.releaseConnection(conn);
	}

	/**
	 * @param pstmt
	 * @throws SQLException
	 */
	private void executeBatch(PreparedStatement pstmt) throws SQLException {
		pstmt.executeBatch();
		pstmt.clearBatch();
	}

	/**
	 * @param pstmt
	 * @param result
	 * @throws SQLException
	 */
	private void setParam(PreparedStatement pstmt, StatResult result)
			throws SQLException {
		int index = 1;
		pstmt.setLong(index++, result.getTotal());
		pstmt.setLong(index++, result.getSuccess());
		pstmt.setString(index++, result.getSendTime());
		pstmt.setString(index++, result.getCity());
	}

	@SuppressWarnings("unused")
	private boolean isExist(StatResult result) throws SQLException {
		String sql = "SELECT 1 FROM MMS_StatResult WHERE sendTime = ? AND city = ?";
		log.debug(sql);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getVdesConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, result.getSendTime());
			pstmt.setString(2, result.getCity());
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			log.error(e);
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs, pstmt, conn);
		}

		return false;
	}

	private Map<String, String> getExistResult() {
		Map<String, String> existResults = new HashMap<String, String>();

		String sql = "SELECT CONVERT(VARCHAR(16), sendtime, 120) AS stime, city FROM MMS_StatResult"
				+ " WHERE sendTime >= ? AND sendTime <= ?";
		log.debug(sql);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getVdesConnection();
			pstmt = conn.prepareStatement(sql);

			pstmt.setTimestamp(1, new Timestamp(addMinute(startTime, -2)
					.getTimeInMillis()));
			pstmt.setTimestamp(2, new Timestamp(addMinute(endTime, 2)
					.getTimeInMillis()));
			rs = pstmt.executeQuery();

			while (rs.next()) {
				String sendTime = rs.getString("stime");
				String city = rs.getString("city");

				existResults.put(getKey(sendTime, city), "");
			}
		} catch (Exception e) {
		
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs, pstmt, conn);
		}

		return existResults;
	}

	/**
	 * @param aTime
	 * @param amount
	 * @return
	 * @throws ParseException
	 */
	private Calendar addMinute(String aTime, int amount) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format.parse(aTime));
		calendar.add(Calendar.MINUTE, amount);

		return calendar;
	}

	public void doSaveToDay(Map<String, StatResult> results)
			throws SQLException {
		Map<String, StatResult> dayResults = convert(results);

		String updateSql = "UPDATE MMS_StatDayResult SET total = total + ?, success = success + ?"
				+ " WHERE sendTime = ? AND city = ?";
		log.debug(updateSql);
		String insertSql = "INSERT INTO MMS_StatDayResult(total, success, sendTime, city)"
				+ "VALUES(?, ?, ?, ?)";
		log.debug(insertSql);

		Connection conn = getVdesConnection();
		PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
		PreparedStatement insertPstmt = conn.prepareStatement(insertSql);

		int updateCount = 0;
		int insertCount = 0;
		for (StatResult result : dayResults.values()) {
			if (isExistDayResult(conn, result)) {
				setParam(updatePstmt, result);
				updateCount += updatePstmt.executeUpdate();
			} else {
				setParam(insertPstmt, result);
				insertCount += insertPstmt.executeUpdate();
			}
		}
		log.info("Updated " + updateCount + " records in MMS_StatDayResult");
		log.info("inserted " + insertCount + " records to MMS_StatDayResult ");
		DBUtil.releaseStatement(updatePstmt);
		DBUtil.releaseStatement(insertPstmt);
		DBUtil.releaseConnection(conn);
	}

	private Map<String, StatResult> convert(Map<String, StatResult> results) {
		Map<String, StatResult> dayResults = new HashMap<String, StatResult>();
		for (StatResult result : results.values()) {
			String sendDay = result.getSendTime().substring(0, 10);
			String city = result.getCity();
			String key = getKey(sendDay, city);

			StatResult dayResult = dayResults.get(key);
			if (dayResult == null) {
				dayResult = new StatResult(sendDay, city);
				dayResults.put(key, dayResult);
			}
			dayResult.addTotal(result.getTotal());
			dayResult.addSuccess(result.getSuccess());
		}
		return dayResults;
	}

	private boolean isExistDayResult(Connection conn, StatResult result) {
		String sql = "SELECT 1 FROM MMS_StatDayResult"
				+ " WHERE CONVERT(VARCHAR(10), sendTime, 120) = ? AND city = ?";
		log.debug(sql);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, result.getSendTime());
			pstmt.setString(2, result.getCity());
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			log.error(e);
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs, pstmt, null);
		}

		return false;
	}

	public void doClearTmp() {
		String sql = "TRUNCATE TABLE MMS_PerfTmp";
		log.debug(sql);
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getVdesConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			log.error(e);
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, conn);
		}
	}

	public void doClearStatResult(int keepDay) {
		doClearResult("MMS_StatResult", keepDay);
	}

	public void doClearStatDayResult(int keepDay) {
		doClearResult("MMS_StatDayResult", keepDay);
	}

	/**
	 * @param table
	 * @param keepDay
	 */
	private void doClearResult(String table, int keepDay) {
		String sql = "DELETE FROM " + table + " WHERE sendTime < ?";
		log.debug(sql);
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getVdesConnection();
			pstmt = conn.prepareStatement(sql);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(format.parse(startTime));
			calendar.add(Calendar.DATE, -keepDay);
			pstmt.setTimestamp(1, new Timestamp(calendar.getTimeInMillis()));

			int row = pstmt.executeUpdate();
			log.info("Deleted " + row + " records at " + table);
		} catch (Exception e) {
			
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(pstmt, conn);
		}
	}
}
