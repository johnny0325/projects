/**
 * TaskTXT.java 2010-5-26 下午04:44:02
 */
package pro.vdes.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

import common.util.ConfigureUtil;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public abstract class ITaskTXT {
	private static Logger log = Logger.getLogger(ITaskTXT.class);
	public InputFile inputFile = null;
	public OutputDB outputDB = null;
	public String getLastDay() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String lastDay = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String sql = "select top (1) statDate from "+outputDB.getTable()+" order by statDate desc";
			rs = stmt.executeQuery(sql);
				if (rs.next() && rs.getTimestamp(1) != null&&!rs.getTimestamp(1).toString().toLowerCase().equals("null")) {
				//currentDate = DateUtil.formatDate(rs.getTimestamp(1),"MMdd");
				return rs.getString(1);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs, stmt, con);
		}
		return lastDay;

	}
	/**
	 * 数据清洗工作：
	 * 删除最后一天的数据；
	 * 删除一个月外的数据。
	 *
	 */
	public void delete(String lastDay){
		if(lastDay==null) return;
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt.execute("delete from "+outputDB.getTable()+" where datediff(d,statDate,'"+lastDay+"')=0");
			stmt.execute("delete from "+outputDB.getTable()+" where datediff(d,statDate,getdate())>31");
			con.commit();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, con);
		}
	}
	/**
	 * 外界调用的主要方法；
	 *
	 */
	public abstract void doSync();

	public void handle() {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			String line = "";
			int i = 0;
			final int LEN = 20;
			String fileName = inputFile.getFileName();
			BufferedReader br = this.getSource(fileName);
			if (br == null)
				return;// WAP22_0516_MS.csv
			String neCode = fileName.substring(0, 3) + "9200"
					+ fileName.substring(3, 5);
			String statDate = Calendar.getInstance().get(Calendar.YEAR) + "-"
					+ fileName.substring(6, 8) + "-"
					+ fileName.substring(8, 10);
			while ((line = br.readLine()) != null) {
				try {

					// 对数据库连接的优化
					if (con == null || i % LEN == 0) {
						con = DBUtil.getConnection("proxool.vdes");
						pstmt = con.prepareStatement(outputDB.getSql());
					}
					log.info(line);
					++i;
					String[] row = line.split(",");
					pstmt.setString(1, neCode);
					pstmt.setString(2, statDate);
					pstmt.setString(3, row[0]);
					pstmt.setString(4, row[1]);
					pstmt.addBatch();

					if (i % LEN == 0) {
						pstmt.executeBatch();
						DBUtil.release(pstmt, con);
					}

				} catch (Exception e) {
					log.error(e);
				}
			}

			if (pstmt != null) {
				pstmt.executeBatch();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block

			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block

			log.error(e);
		} finally {
			DBUtil.release(pstmt, con);
		}

	}

	public BufferedReader getSource(String fileName)
			throws FileNotFoundException {
		String ftpconf = new ConfigureUtil().getValue("ftpconf");
		File srcFile = new File(ftpconf, fileName);
		if (!srcFile.exists()) {
			log.info("no file");
			return null;
		}
		return new BufferedReader(new FileReader(srcFile));
	}

	/**
	 * @return the inputFile
	 */
	public InputFile getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile
	 *            the inputFile to set
	 */
	public void setInputFile(InputFile inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the outputDB
	 */
	public OutputDB getOutputDB() {
		return outputDB;
	}

	/**
	 * @param outputDB
	 *            the outputDB to set
	 */
	public void setOutputDB(OutputDB outputDB) {
		this.outputDB = outputDB;
	}
}

class InputFile {
	String fileName;

	String reg;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the reg
	 */
	public String getReg() {
		return reg;
	}

	/**
	 * @param reg
	 *            the reg to set
	 */
	public void setReg(String reg) {
		this.reg = reg;
	}

}

class OutputDB {
	String database;

	String table;

	String sql;

	String[] field;

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @param database
	 *            the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * @return the field
	 */
	public String[] getField() {
		return field;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(String[] field) {
		this.field = field;
	}

	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table
	 *            the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

}
