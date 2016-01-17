/**
 * TaskMBWSMSHandle.java 2010-5-26 下午04:44:02
 */
package pro.vdes.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.log4j.Logger;

import common.util.CfgUtil;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.DateUtil;
import common.util.ZipUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskMBWSMSHandle {
	private static Logger log = Logger.getLogger(TaskMBWSMSHandle.class);
	private static String ftpconf = CfgUtil.getValue("ftpconf")+"/mbw/sms/";
	/**
	 * 外界调用的主要方法
	 *
	 */
	public void doSync(){
		//加载各个文件所对于的数据到库中；
		final Set<SMSUploadFileBean> filenames = getFileName(0);//得到已上传的文件
		for (SMSUploadFileBean bean : filenames) {
			//File file = new File(ftpconf+"/"+bean.getFilename());
			//File file = new File(ftpconf+"/SMS_0519.zip");
			//像这样releaseZipToFile(ftpconf+"/SMS_0626.zip",ftpconf);
			try {
				ZipUtil.releaseZipToFile(ftpconf+"/SMS_"+bean.getFilename().substring(4,8)+"/"+bean.getFilename(),ftpconf);
				changeMSUploadFileStatus(bean.getId(),1);//0:已上传，1：已解压，2：已入库
				doLoad();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		
	}
	
	/**
	 * 
	 *将数据入库。
	 */
	private void doLoad(){
		//加载各个文件所对于的数据到库中；
		final Set<SMSUploadFileBean> filenames = getFileName(1);//得到已解压的文件
		for (SMSUploadFileBean bean : filenames) {
			try {
				//SMS_0626.zip 这样的压缩包里面放了SMS_0626_1.txt,SMS_0626_2.txt,SMS_0626_3.txt
				loadFile(bean.getFilename().substring(0,bean.getFilename().indexOf(".")));
				changeMSUploadFileStatus(bean.getId(),2);//0:已上传，1：已解压，2：已入库;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	/**
	 * @param filename
	 * @throws Exception 
	 */
	private void loadFile(String startWith) throws Exception {//startWith 如：SMS_0626
		// TODO Auto-generated method stub
		createTable(startWith.substring(0,8));
		//delete(startWith.substring(0,8));//这理需要考虑一下2010-07-15
		String[] filenames = getFilenames(startWith);
		for (String fileName : filenames) {
			handle(fileName);//解析FTP短信文本文件到库中，如SMS_0625
		}
	}
	/**
	 * @param fileName
	 */
	private void handle(String fileName) throws Exception {
		// TODO Auto-generated method stub
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
					//数据验证;
					if(!flag){
						log.info(line);
						flag = isValidLine(line,regexp);
						if(!flag){
							continue;
						}
					}
					
					
					// 对数据库连接的优化
					if (con == null || i % LEN == 0) {
						con = DBUtil.getConnection("proxool.vdes");
						pstmt = con.prepareStatement(insertSql);
					}
					++i;
					String[] row = line.split(",");
					//int[] col = new int[]{2,4,11,14,6,8};
					//"insert into MBW_SMS(callerPhone,calledPhone,alarmDate,[key],city,brand,parentId) values(?,?,?,?,?,?,?)";
					//1341745,8613415155446,本地,8613622594483,广东省,汕头市,移动,神州行,proxy-150,2010-06-23 10:12:52,2010-06-23 10:13:04,点对点短信,3,imsi,否,产生告警                         
					//log.info(i);
					//log.info(row[1]+"----"+row[3]+"----"+row[10]+"----"+row[13]+"----"+row[5]+"----"+row[7]);
					if(i%5000==0){
						log.info("已经处理"+fileName+" "+i+"行.");
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
					throw e;
				}
			}
			log.info("文件长度-->"+i);
			br.close();br = null;
			

			
			if (con!=null&&!con.isClosed()) {
				pstmt.executeBatch();
			}
			log.info(fileName+"文件处理完毕!");
			
			File file = new File(fileName);
			file.delete();//处理完就删除掉；
			log.info(fileName+"文件删除掉!");
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
	 * 判断该行是否为有效的数据。
	 * @param line //1341745,8613415155446,本地,8613622594483,广东省,汕头市,移动,神州行,proxy-150,2010-06-23 10:12:52,2010-06-23 10:13:04,点对点短信,3,imsi,否,产生告警  		
	 * @return
	 */
	private boolean isValidLine(String line,String regexp){
       Pattern pattern = Pattern.compile(regexp);
       Matcher match = pattern.matcher(line);                
       return match.matches();
	}
	/**
	 * @param fileName
	 * @return
	 */
	private String getTableName(String fileName) {
		// TODO Auto-generated method stub
		return "SMS_"+fileName.substring(4,8);//SMS_0625.txt  得到SMS_0625表名
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
	/**
	 * @param startWith
	 */
	private void delete(String startWith) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			stmt.execute("TRUNCATE TABLE "+ startWith);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, con);
		}
	}

	/**
	 * @param startWith
	 */
	private void createTable(String startWith) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		String sql = "CREATE TABLE [dbo].["+startWith+"]("+
				"[callerPhone] [numeric](19, 0) NULL,"+
				"[calledPhone] [numeric](19, 0) NULL,"+
				"[statDate] [datetime] NULL,"+
				"[key] [varchar](50) COLLATE Chinese_PRC_CI_AS NULL,"+
				"[city] [varchar](50) COLLATE Chinese_PRC_CI_AS NULL,"+
				"[brand] [varchar](50) COLLATE Chinese_PRC_CI_AS NULL"+
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
		log.info("tableName:"+startWith);
		
	}

	/**
	 * 获得当天的人工FTP短信文件列表；
	 */
	private String[] getFilenames(final String startWith){//由SMS_0626字符串得到SMS_0626_1.txt,SMS_0626_2.txt,SMS_0626_3.txt文件系列
		File dir = new File(ftpconf);
		String[] filenames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if(name.startsWith(startWith)&&name.toLowerCase().endsWith("txt")){
					return true;
				}
				return false;
				
			}

		});
		return filenames;
	}
	/**
	 * @param id
	 * @param i
	 */
	private void changeMSUploadFileStatus(String id, int i) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection("proxool.vdes");
			String sql = "update MBW_SMSUploadFile set status= "+i+" where id='"+id+"' ";
			stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.release(rs,stmt,conn);
		}
	}

	//以前是对MBW_SMS的数据做呈现，而这个表中的数据多达1000万，呈现比较慢，为此，对这个表做预处理，提高呈现速度。add by aiyan 2010-06-21
	public void doStatics(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		List<Map> list = getData();
		if(list==null||list.size()==0) return;
		try {
			
			conn = DBUtil.getConnection("proxool.vdes");
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("delete from MBW_SMS_STATICS");
			
			String sql = "insert into MBW_SMS_STATICS values(?,?,?)";
			pstmt = conn.prepareStatement(sql);
			for(Map map:list){
				pstmt.setString(1, map.get("key")+"");
				pstmt.setString(2, map.get("statDate")+"");
				pstmt.setString(3, map.get("c")+"");
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.release(pstmt, conn);
		}

		
	}
	
	public List<Map>  getData(){
		List list = new ArrayList();
		Connection con = null;
		PreparedStatement pstmt = null;
	    String sql = "SELECT [key], alarmDate_forIndex, count(1) as c  FROM MBW_SMS GROUP BY alarmDate_forIndex,[key] ";
		try {
			con = DBUtil.getConnection("proxool.vdes");
			pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map map = new HashMap();
				map.put("key",rs.getString("key"));
				map.put("alarmDate_forIndex", rs.getString("alarmDate_forIndex"));
				map.put("c",rs.getString("c"));
				list.add(map);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){ 
			e.printStackTrace();
		}finally {
			DBUtil.release(pstmt, con);

		}
		return list.size()==0?null:list;
	}
	
	class SMSUploadFileBean{
		private String id;
		private String filename;
		
		public SMSUploadFileBean(String id,String filename){
			this.id = id;
			this.filename = filename;
		}
		/**
		 * @return the filename
		 */
		public String getFilename() {
			return filename;
		}
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}		
	}
	/**
	 * 
	 * @return 得到需要Handle的FTP的文件名；
 	 */
	private Set<SMSUploadFileBean> getFileName(int status){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Set<SMSUploadFileBean> set = new HashSet<SMSUploadFileBean>();
		try {
			conn = DBUtil.getConnection("proxool.vdes");
			String sql = "select id, filename from MBW_SMSUploadFile where type='ftp' and status ="+status +" order by filename asc";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				set.add(new SMSUploadFileBean(rs.getString(1),rs.getString(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.release(rs,pstmt, conn);
		}
		return set;
		
	}
	public static void main(String[] argv) {
		TaskMBWSMSHandle tt = new TaskMBWSMSHandle();
		//tt.doStatics();
		tt.doSync();
		tt.doLoad();
	}
}
