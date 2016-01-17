/**
 * TaskBill.java 2010-6-28 下午03:00:37
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
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import common.util.CfgUtil;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.DateUtil;

	/**
	 * @author aiyan
	 * @version 1.0
	 * 
	 */
	public  class TaskBill {
		private static Logger log = Logger.getLogger(TaskBill.class);
		/**
		 * 
select   TOP 1 NAME   from   sysobjects   where   type   =   'U' and  name like 'BILL_%' ORDER BY NAME DESC
		 * @return  查询WAP 话单的表 的最近一天的表名
		 */
		private String getLasTable() {
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				con = DBUtil.getConnection("proxool.vdes");
				stmt = con.createStatement();
				String sql = "select   TOP 1 NAME   from   sysobjects   where   type   =   'U' and  name like 'BILL_%' ORDER BY NAME DESC";
				rs = stmt.executeQuery(sql);
				if(rs.next()){
					return rs.getString(1);
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				DBUtil.release(rs, stmt, con);
			}
			return null;

		}
		/**
		 * 表数据清洗工作：
		 */
		private void delete(String lastTable){
			if(lastTable==null) return;
			Connection con = null;
			Statement stmt = null;
			try {
				con = DBUtil.getConnection("proxool.vdes");
				stmt = con.createStatement();
				stmt.execute("TRUNCATE TABLE "+ lastTable);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				DBUtil.release(stmt, con);
			}
		}
		/**
		 * 获得当天的WAP话单文件列表；
		 * @param day 时间形式是MMdd,如：0628
		 * @return
		 */
		private String[] getFilenames(final String day){
			String ftpconf = CfgUtil.getValue("ftpconf");
			File dir = new File(ftpconf);
			String[] filenames = dir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					if(name.startsWith("bill_imsi_simple."+day)){//bill_imsi_simple.0628.af
						return true;
					}
					return false;
					
				}

			});
			return filenames;
		}
		/**
		 * 外界调用的主要方法；
		 *
		 */
		public void doSync() {
			String today = DateUtil.format(new Date(), "MMdd");
			String lastTable = this.getLasTable();
			String currentDate = lastTable.substring(5);
			String[] filenames = null;
			String tableName = null;
			while (currentDate.compareTo(today) < 0) {
				log.info("处理"+currentDate+"的WAP原始话单");
				filenames = getFilenames(currentDate);
				if(filenames!=null&&filenames.length>0){
					tableName = "bill_"+currentDate;
					createTable(tableName);
					delete(tableName);
					
					for (String fileName : filenames) {
						handle(fileName);
					}
					setCity(tableName);
				}

				currentDate = DateUtil.nextDay(currentDate, 1,"MMdd");
			}
			
		}

		private void setCity(String tableName) {
			// TODO Auto-generated method stub
			Connection con = null;
			Statement stmt = null;
			String sql1 = "update  "+tableName+"  set city =("+
					"select city from dbo.BPW_HLR where code=substring(cast(callerphone as varchar(30)),3,7)"+
					")";  
			String sql2="update  "+tableName+" set city='其它' where city is null";
			try {
				con = DBUtil.getConnection("proxool.vdes");
				stmt = con.createStatement();
				stmt.execute(sql1);
				stmt.execute(sql2);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				DBUtil.release(stmt, con);
			}
			log.info("setCity");
			
		}
		/**
		 * @param string
		 */
		private void createTable(String tableName) {
			// TODO Auto-generated method stub
			Connection con = null;
			Statement stmt = null;
			
			
			String sql = 
				"CREATE TABLE [dbo].["+tableName+"]("+
						" [statDate] [datetime] NULL,"+
						" [callerPhone] [numeric](13, 0) NULL,"+
						" [url] [varchar](3000) COLLATE Chinese_PRC_CI_AS NULL,"+
						" [url2] [varchar](128) COLLATE Chinese_PRC_CI_AS NULL,"+
						" [feature] [varchar](50) COLLATE Chinese_PRC_CI_AS NULL,"+
						" [city] [varchar](50) COLLATE Chinese_PRC_CI_AS NULL"+
					") ON [PRIMARY]";
					
			try {
				con = DBUtil.getConnection("proxool.vdes");
				stmt = con.createStatement();
				stmt.execute(sql);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				DBUtil.release(stmt, con);
			}
			log.info(tableName);
			
		}
		public void handle(String fileName) {
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				BufferedReader br = this.getSource(fileName);
				if (br == null) return;
				String line = "";
				int i = 0;
				final int LEN = 20;
				String insertSql = "insert into "+getTableName(fileName)+"(statDate,callerPhone,url,url2,feature) values(?,?,?,?,?)";
				String url2 = "";
				while ((line = br.readLine()) != null) {
					try {

						// 对数据库连接的优化
						if (con == null || i % LEN == 0) {
							con = DBUtil.getConnection("proxool.vdes");
							pstmt = con.prepareStatement(insertSql);
						}
						++i;
						String[] row = line.split(",");
						//2010-06-20 23:55:08.967,8615818492859,http://lrc.ttpod.com/q?title=%E4%BD%95%E5%BF%85%E5%9C%A8%E4%B8%80%E8%B5%B7%20www.2651.cn&artist=%E5%BC%A0%E6%9D%B0%20www.2651.cn&raw=2&v=v3.7.0.2010042712&uid=dfgdihacaaiedce&mid=2000da64&f=f1&s=s030&imsi=460028182259225&mediatype=mp3&duration=274&bitrate=129&srate=44100,TTPodHttpQhk1.1,200
						if(row.length==5){
							if(row[0].equals("NULL")||row[1].equals("NULL")||row[2].equals("NULL")){
								//log.warn("这个话单被过滤："+line);
							}else if(row[1].startsWith("86")&&row[1].length()==13&&!row[2].startsWith("http:/?")&&!row[2].startsWith("http://&")){//以http:/? 或者http://&开头的都不考虑。
								url2 = row[2].replace("http://","");
								if(url2.indexOf("/")!=-1) url2 = url2.substring(0,url2.indexOf("/"));else url2 = "";
								pstmt.setString(1, row[0]);
								pstmt.setString(2, row[1]);
								pstmt.setString(3, row[2]);
								pstmt.setString(4, url2);
								pstmt.setString(5, getFeature(url2));
								pstmt.addBatch();
							}
						}
				
						if (i % LEN == 0) {
							pstmt.executeBatch();
							DBUtil.release(pstmt, con);
						}

						
					} catch (Exception e) {
						log.error(e);
						e.printStackTrace();
					}
				}
				log.info("文件长度-->"+i);
				if (con!=null&&!con.isClosed()) {
					pstmt.executeBatch();
				}
				log.info(fileName+"文件处理完毕!");
			} catch (IOException e) {
				// TODO Auto-generated catch block

				log.error(e);
			} catch (Exception e) {
				// TODO Auto-generated catch block

				log.error(e);
				e.printStackTrace();
			} finally {
				try {
					if (con!=null&&!con.isClosed()) {
						DBUtil.release(pstmt, con);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		/**
		 * mtk2.wapdfw.com 就得到 wapdfw.com 做为特征
		 * @param url
		 * @return
		 */
		private String getFeature(String url){
			String feature = null;
			String regEx ="(\\w+:)?(([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.)?(com.cn|" +
					"com|cn|net|org)(:\\d*)?";
			Pattern p=Pattern.compile(regEx);
			Matcher m=p.matcher(url);
			if(m.find()){
	/*			for(int i =0;i<m.groupCount();i++){
					System.out.println(i+"--"+m.group(i));
				}*/
				feature = m.group(2)+m.group(4);
			}else{
				regEx ="((\\d+)\\.(\\d+)\\.(\\d+))\\.(\\d+)";
				p=Pattern.compile(regEx);
				m=p.matcher(url);
				if(m.find()){
					feature = m.group(1);
				}else{
					feature = url;//当两种匹配都不通过时，就将原来URL直接返回做为特征域名。
				}
				
			}
			return feature;
			
		}

		
		/**
		 * @param fileName
		 * @return
		 */
		private String getTableName(String fileName) {
			// TODO Auto-generated method stub
			return "bill_"+fileName.substring(17,21);//bill_imsi_simple.0628.af  得到bill_0628表名
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
		
		public static void main(String[] argv){
			//new TaskBill().doSync();
			//String a = new TaskBill().getLasTable();
			//System.out.println("lastTable"+a);
			//String url = "http://&mtid=357423010027657460020107687735&pv=100721&sv=3.0.1.091116&ov=c-mtk-lyt6223_0828-xmt6223c_08a&pt=mtunes2a1&pk=mthot";
			
		}

}
