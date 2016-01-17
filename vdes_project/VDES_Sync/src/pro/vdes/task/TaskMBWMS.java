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
public class TaskMBWMS extends ITaskTXT{
	private static Logger log = Logger.getLogger(TaskMBWMS.class);
	/**
	 * 外界调用的主要方法；
	 *
	 */
	public void doSync() {
		InputFile in = new InputFile();
		OutputDB out = new OutputDB();
		out.setTable("MBW_WAP_MS");
		out.setSql("insert into MBW_WAP_MS (neCode,statDate,ms,requestNum) values(?,?,?,?)");
		setInputFile(in);
		setOutputDB(out);
		
		final String lastDay = getLastDay();//2010-05-16 00:00:00.000
		//清洗数据；
		delete(lastDay);
		//加载各个文件所对于的数据到库中；
		String ftpconf = new ConfigureUtil().getValue("ftpconf");
		File dir = new File(ftpconf);
		String[] filenames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if(name.endsWith("_MS.csv")){
					if(lastDay==null){
						return true;
					}
					String compare = lastDay.substring(5,7)+lastDay.substring(8,10);
					String date =  name.substring(6,10); //WAP22_0516_MS.csv
					if (compare.compareTo(date)<=0) {
						return true;

					}
				}
				return false;
				
			}

		});
		for (String fileName : filenames) {
			in.setFileName(fileName);
			handle();
		}

	}

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
	public static void main(String[] argv) {
		TaskMBWMS tt = new TaskMBWMS();
		tt.doSync();
	}
}
