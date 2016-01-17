/**
 * TaskSMS.java 2010-6-28 ����03:00:37
 */
	package pro.vdes.task;

	import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import common.util.DBUtil;
import common.util.DateUtil;
import common.util.RarUtil;

	/**
	 * @author aiyan
	 * @version 1.0
	 * 
	 */
	public  class TaskSMS {
		private static Logger log = Logger.getLogger(TaskSMS.class);
		private static String ftpconf = CfgUtil.getValue("ftpconf")+"/mbw/sms/SMS_0625.txt~";
		private String getFirstTable(){//����ķ���getLasTable����û��bill_0623��ʽ�ı���Ҫ��������ļ�����firstTable
			File dir = new File(ftpconf);
			String[] filenames = dir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					if(name.startsWith("SMS_")){//SMS_0625.txt
						return true;
					}
					return false;
					
				}

			});
			String minFile = null;
			for(String fileName:filenames){//SMS_0625.txt
				if(minFile==null){
					minFile = fileName;
				}else if(minFile.compareTo(fileName)>0){
					minFile = fileName;
				}
				
			}
			String FirstTable = getTableName(minFile);//SMS_0625.txt  �õ�sms_0625����
			return FirstTable;
			
		}
		
		private String getLasTable() {
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			String lastTable = null;
			try {
				con = DBUtil.getConnection("proxool.mysql_vdes");
				stmt = con.createStatement();
				String sql = "show tables";
				rs = stmt.executeQuery(sql);
				
				while (rs.next()) {
					if(rs.getString(1).startsWith("sms_")){
						if(lastTable==null) {
							lastTable = rs.getString(1);
						}else if(lastTable.compareTo(rs.getString(1))<0){
							 lastTable = rs.getString(1);
						}
					}
				}
				if(lastTable==null){
					lastTable = getFirstTable();
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				DBUtil.release(rs, stmt, con);
			}
			return lastTable;

		}
		/**
		 * ��������ϴ������
		 */
		private void delete(String lastTable){
			if(lastTable==null) return;
			Connection con = null;
			Statement stmt = null;
			try {
				con = DBUtil.getConnection("proxool.mysql_vdes");
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
		 * ��õ�����˹�FTP�����ļ��б�
		 * @param day ʱ����ʽ��MMdd,�磺0628
		 * @return
		 */
		private String[] getFilenames(final String day){
			File dir = new File(ftpconf);
			String[] filenames = dir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					if(name.startsWith("SMS_"+day)){//SMS_0628.txt
						return true;
					}
					return false;
					
				}

			});
			return filenames;
		}
		/**
		 * �����õ���Ҫ������
		 *
		 */
		public void doSync() {
			String today = DateUtil.format(new Date(), "MMdd");
			String lastTable = this.getLasTable();
			String currentDate = lastTable.substring(4);//lastTable:SMS_0625
			String[] filenames = null;
			String tableName = null;
			while (currentDate.compareTo(today) < 0) {
				log.info("����"+currentDate+"���˹�FTP�����ļ�");
				filenames = getFilenames(currentDate);
				if(filenames!=null&&filenames.length>0){
					tableName = "sms_"+currentDate;
					createTable(tableName);
					delete(tableName);
					
					for (String fileName : filenames) {
						handle(fileName);
					}
				}

				currentDate = DateUtil.nextDay(currentDate, 1,"MMdd");
			}
			
		}

		/**
		 * @param string
		 */
		private void createTable(String tableName) {
			// TODO Auto-generated method stub
			Connection con = null;
			Statement stmt = null;
			String sql = "CREATE TABLE if not exists   `"+tableName+"` ("+
					  "`callerPhone` varchar(50) default NULL,"+
					  "`calledPhone` varchar(50) default NULL,"+
					  "`statDate` datetime default NULL,"+
					  "`key` varchar(50) default NULL,"+
					  "`city` varchar(50) default NULL,"+
					  "`brand` varchar(50) default NULL"+
					") ENGINE=InnoDB DEFAULT CHARSET=gbk;";
					
			try {
				con = DBUtil.getConnection("proxool.mysql_vdes");
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
		/**
		 * �жϸ����Ƿ�Ϊ��Ч�����ݡ�
		 * @param line //1341745,8613415155446,����,8613622594483,�㶫ʡ,��ͷ��,�ƶ�,������,proxy-150,2010-06-23 10:12:52,2010-06-23 10:13:04,��Ե����,3,imsi,��,�����澯  		
		 * @return
		 */
		private boolean isValidLine(String line,String regexp){
           Pattern pattern = Pattern.compile(regexp);
           Matcher match = pattern.matcher(line);                
           return match.matches();
		}
		public void handle(String fileName) {
			Connection con = null;
			PreparedStatement pstmt = null;
			boolean flag = false;
			try {
				BufferedReader br = this.getSource(fileName);
				if (br == null) return;
				String line = "";
				long i = 0;
				final int LEN = 20;
				String insertSql = "insert into "+getTableName(fileName)+" values(?,?,?,?,?,?)";
				String regexp = "\\d+,\\d{13},.*";	
				while ((line = br.readLine()) != null) {
					try {
						//������֤;
						if(!flag){
							flag = isValidLine(line,regexp);
							if(!flag){
								//log.info("--"+new String(line.getBytes(),"UTF-8"));
								continue;
							}
						}
						
						
						// �����ݿ����ӵ��Ż�
						if (con == null || i % LEN == 0) {
							con = DBUtil.getConnection("proxool.mysql_vdes");
							pstmt = con.prepareStatement(insertSql);
						}
						++i;
						String[] row = line.split(",");
						//int[] col = new int[]{2,4,11,14,6,8};
						//"insert into MBW_SMS(callerPhone,calledPhone,alarmDate,[key],city,brand,parentId) values(?,?,?,?,?,?,?)";
						//1341745,8613415155446,����,8613622594483,�㶫ʡ,��ͷ��,�ƶ�,������,proxy-150,2010-06-23 10:12:52,2010-06-23 10:13:04,��Ե����,3,imsi,��,�����澯                         
						//log.info(i);
						//log.info(row[1]+"----"+row[3]+"----"+row[10]+"----"+row[13]+"----"+row[5]+"----"+row[7]);
						if(i%5000==0){
							log.info("�Ѿ�����"+fileName+" "+i+"��.");
						}
						if(row.length>13){
							//log.info(i+"----"+line);
								pstmt.setString(1, row[1]);
								pstmt.setString(2, row[3]);
								pstmt.setString(3, row[10]);
								pstmt.setString(4, row[13]);
								pstmt.setString(5, row[5]);
								pstmt.setString(6, row[7]);
								pstmt.addBatch();
						}
				

						if (i % LEN == 0) {
							pstmt.executeBatch();
							DBUtil.release(pstmt, con);
						}

						
					} catch (Exception e) {
						log.error(e);
						log.error(i);
						e.printStackTrace();
					}
				}
				log.info("�ļ�����-->"+i);
				if (con!=null&&!con.isClosed()) {
					pstmt.executeBatch();
				}
				log.info(fileName+"�ļ��������!");
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
		 * @param fileName
		 * @return
		 */
		private String getTableName(String fileName) {
			// TODO Auto-generated method stub
			return "sms_"+fileName.substring(4,8);//SMS_0625.txt  �õ�sms_0625����
		}

		public BufferedReader getSource(String fileName)
				throws FileNotFoundException, UnsupportedEncodingException {
			File srcFile = new File(ftpconf, fileName);
			if (!srcFile.exists()) {
				log.info("no file");
				return null;
			}
			return new BufferedReader(new InputStreamReader (new FileInputStream(srcFile),"UTF-8"));
		}
		public void testA() throws IOException{
			System.out.println("dd");
			String root = ftpconf+"/SMS_0625.txt";
			File file = new File(root);
			BufferedReader br = new BufferedReader(new FileReader(file));
			long i = 0;
			while(br.readLine()!=null){
				i++;
				if(i%5000==0) log.info(i);
				
			}
			System.out.println("--"+i);
			
		}
		public static void main(String[] argv){
			//new TaskSMS().doSync();
			try {
				new TaskSMS().testA();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.fillInStackTrace());
				e.printStackTrace();
			}
			//new TaskSMS().aa();
			//String a = new TaskBill().getLasTable();
			//System.out.println("lastTable"+a);
/*			String line = "1341745,8613415155446,����,8613622594483,�㶫ʡ,��ͷ��,�ƶ�,������,proxy-150,2010-06-23 10:12:52,2010-06-23 10:13:04,��Ե����,3,imsi,��,�����澯";                         
			String regexp = "\\d+,\\d{13},.*";	
			boolean flag = new TaskSMS().isValidLine(line,regexp);
			System.out.println(flag);*/
			
		}

}
