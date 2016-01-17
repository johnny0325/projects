/**
 * TaskDNS.java 2009-7-23 下午12:16:23
 */
package pro.vdes.task;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import common.util.ConfigureUtil;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskDNS extends TaskCommon {
	public TaskDNS(String table, int delete, int exists) {
		this.table = table;
		this.delete = delete;
		this.exists = exists;
	}

	private static Logger log = Logger.getLogger(TaskDNS.class);

	private String yesterday;
	private String today;
	private String maxStatDate;

	public void generateDNS() {
		log.info("TaskDNS start...");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {

			initRecentlyDay();

			con = DBUtil.getConnection("proxool.vdes");

			con.setAutoCommit(false);

			// 删除数据
			doDelete(con);

			// 获取数据库中最大的日期
			maxStatDate = getMaxStatDate(con);
			log.info("maxStatDate: " + maxStatDate);

			String sql = "insert into bpw_wap_dns(statdate,ip,status,delay) values(?,?,?,?)";
			pstmt = con.prepareStatement(sql);

			BufferedReader br = getSourceReader();
			String line = "";
			while ((line = br.readLine()) != null) {
				if (!isRecentlyTwoDay(line)) {
					// 这一行的日期不是昨天, 也不是今天
					// 那么这一行忽略
					continue;
				}
				if (!isNewData(line)) {
					// 如果这一行的日期不比数据库中数据的最大日期大
					// 在执行这一个采集时, 已经将两天内的数据删除了, 如果这个时候这一行的日期还是比数据库中最大的日期小,
					// 那么这一行忽略
					continue;
				}
				//log.info(line);

				String regex = "(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})\\s(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\snull\\snull\\s(\\d+)\\s(.*)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					pstmt.setString(1, matcher.group(1));
					pstmt.setString(2, matcher.group(2));
					pstmt.setString(3, matcher.group(3));
					pstmt.setString(4, matcher.group(4));

					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();

			con.commit();
		} catch (FileNotFoundException e) {
			log.error(e);
			rollback(con);
		} catch (IOException e) {
			log.error(e);
			rollback(con);
		} catch (SQLException e) {
			log.error(e);
			rollback(con);
		} catch (Exception e) {
			log.error(e);
			rollback(con);
		}finally {
			DBUtil.release(pstmt, con);
		}
		log.info("TaskDNS end...");

	}

	/**
	 *
	 */
	private void initRecentlyDay() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		long current = System.currentTimeMillis();

		yesterday = format.format(current - 24 * 60 * 60 * 1000);
		today = format.format(current);
		log.info(yesterday + "|" + today);
	}

	/**
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private BufferedReader getSourceReader() throws MalformedURLException,
			IOException {
		String destUrl = new ConfigureUtil().getValue("dnsFile");
		URL url = new URL(destUrl);
		HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
		// 连接指定的网络资源
		httpUrl.connect();
		// 获取网络输入流
		InputStreamReader reader = new InputStreamReader(httpUrl
				.getInputStream());

		return new BufferedReader(reader);
	}

	/**
	 * @param con
	 */
	private void rollback(Connection con) {
		if (con == null) {
			return;
		}

		try {
			con.rollback();
		} catch (SQLException e) {
			log.error(e);
		}
	}

	/**
	 * @param str
	 * @return
	 */
	private boolean isRecentlyTwoDay(String str) {
		if (str.length() == 0) {
			return false;
		}
		if (str.startsWith(yesterday)) {
			return true;
		}
		return str.startsWith(today);
	}

	/**
	 * @param line
	 * @return
	 */
	private boolean isNewData(String line) {
		if (maxStatDate == null) {
			return true;
		}

		return line.substring(0, maxStatDate.length()).compareTo(maxStatDate) > 0;
	}

	/**
	 * @param con
	 * @throws SQLException
	 */
	private void doDelete(Connection con) throws SQLException {
		log.info("deleteData");
		PreparedStatement stmt = null;
		try {
			String sql = "delete from  " + table
					+ " where datediff(d,statdate,getdate())<" + delete;
			stmt = con.prepareStatement(sql);
			int i = stmt.executeUpdate();
			log.info("deleteDate  | 删除 " + i + " 条");
		} catch (SQLException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.releaseStatement(stmt);
		}
	}

	/**
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	private String getMaxStatDate(Connection con) throws SQLException {
		String sql = "SELECT MAX(statDate) FROM bpw_wap_dns";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				String maxDate = rs.getString(1);
				if (maxDate == null) {
				} else if (maxDate.length() > 19) {
					return maxDate.substring(0, 19);
				} else if (maxDate.length() > 10) {
					return maxDate.substring(0, 10);
				}
			}
		} catch (SQLException e) {
			log.error(e);
		}catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.release(rs, pstmt, null);
		}
		return null;
	}

}
