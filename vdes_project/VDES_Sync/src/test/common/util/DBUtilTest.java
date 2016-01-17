/**
 * DBUtilTest.java 2009-4-24 上午10:32:45
 */
package test.common.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class DBUtilTest extends TestCase {
	private static Logger log = Logger.getLogger(DBUtilTest.class);

	public void testA() {
		Connection con = DBUtil.getConnection("proxool.mmsc");
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			stmt = con.createStatement();
			//rs = stmt.executeQuery("select * from ic2 limit 1, 10");
			rs = stmt.executeQuery("select * from ic2 i where  i.msisdn in('15914268299','15918611748','13450432881') and i.city = 'gz'");
			while (rs.next()) {
				log.info(rs.getString(1));
				log.info(rs.getString("lastmmstime"));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.release(rs, stmt, con);
		}

	}

	public void testB() throws IOException, SQLException {

		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "insert into mmsperf20090417 values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		for (int count = 1; count < 2; count++) {
			try {

				con = DBUtil.getConnection("proxool.mmsc");

				pstmt = con.prepareStatement(sql);

				BufferedReader br = new BufferedReader(new FileReader(
						//"d:/jifang/mms" + count + ".txt"));
				"d:/jifang/mms.txt"));
				String line = "";
				int i = 0;
				String data[] = null;
				while ((line = br.readLine()) != null) {
					log.info(++i + ":" + line);
					data = line.split("\\|");
					for (int j = 0; j < data.length; j++) {
						pstmt.setString(j + 1, data[j]);
					}
					pstmt.addBatch();
				}
				pstmt.executeBatch();
				log.info("mms" + count + ".txt个文件已经成功导入!");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBUtil.release(pstmt, con);

			}
		}

	}
	public void testC() {
		Connection con = DBUtil.getConnection("proxool.mmsc");
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			String tempTSql = "create TEMPORARY table c (tcode varchar(11)) ";
			//String tempTSql = "select * from ic2 limit 1,1 ";
			stmt.execute(tempTSql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.release(rs, stmt, con);
		}

	}

}
