package pro.vdes.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import common.util.CfgUtil;
import common.util.DBUtil;
import common.util.DateUtil;
import common.util.ZipUtil;
/**
 * �����ֻ��������ݣ����ݸ�ʽΪʱ�䣬���룬ָ��
 * @author Administrator
 *
 */
public class TaskMail {
	private static Logger log = Logger.getLogger(TaskMail.class);
	private static String ftpconf = CfgUtil.getValue("ftpconf")+"/mail/";
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
	 * ��Excel���ݲ��뵽���ݿ���
	 *@author DengJianhua
	 *Jul 19, 2010  1:01:55 PM
	 *
	 */
	public void handle(String fileName){
		Connection con = null;
		PreparedStatement pstmt = null;
		Statement stmt=null;
		ResultSet rs=null;
		try {
			BufferedReader br = this.getSource(fileName);
			
			if (br == null) return;
			String line = "";
			long i = 0;
			final int LEN = 2000;
			String currentDate=fileName.substring(5,9);//mail_0829.txt
			String tableName  ="mail_"+currentDate;
			String insertSql = "insert into "+tableName+"(statDate,callerPhone,feature,city,brand,terminal,os,status,kind) values(?,?,?,?,?,?,?,?,?)";
			
			String behavioTtable="mbw_behavior_"+currentDate;//��Ϊ���ݱ�
			
			PreparedStatement pstmtBehavior=null;
			PreparedStatement pstmtReal=null;
			while ((line = br.readLine()) != null) {
				try {
					
					// �����ݿ����ӵ��Ż�
					if (con == null || i % LEN == 0) {
						con = DBUtil.getConnection("proxool.mmds");
						pstmt = con.prepareStatement(insertSql);
						
						String inSql="insert into mmds_realtime(callerphone,VIRUSNAME,STATDATE,ID) values(?,?,?,?)";//ʵʱ���
						pstmtReal = con.prepareStatement(inSql);
						
						String insertBehavior = "insert into "+behavioTtable
						+" (callerPhone,type,city,brand,statDate,feature,terminal,os,kind) " //,terminal,os
						+"values(?,?,?,?,?,?,?,?,?)";//������Ϊ���ݵ�sql���
						pstmtBehavior = con.prepareStatement(insertBehavior);
					}
					++i;
					String[] row = line.split(",");
					//2010-08-29 11:41:43,15269968533,tl
					if(row.length==3)
						if(null!=row[1]&&i!=1&&!"null".equals(row[1])&&!"".equals(row[1])){
							String feature=row[2];
		
							stmt=con.createStatement();
							String sqlFilter="select t.name_cn,t.id,t.status from mbw_wap_filter t " +
									" left join mbw_wap_filter_feature f on(f.filter_id=t.id) " +
									" where f.feature='"+feature+"'";
							rs=stmt.executeQuery(sqlFilter);
							
							
							//����������� statDate,callerPhone,feature,city,brand,terminal,os,status
							Timestamp timestamp=null;
							if(row[0].length()==10){
								timestamp=java.sql.Timestamp.valueOf(row[0]+" 00:00:00");
								//log.info("timestamp:"+timestamp);
								pstmt.setTimestamp(1,timestamp);
							}else{
								timestamp=java.sql.Timestamp.valueOf(row[0]);
								pstmt.setTimestamp(1,timestamp);
							}
								
						
							pstmt.setLong(2, Long.parseLong(row[1]));
							pstmt.setString(3, feature);
							String callerPhone=row[1];
							
							String code=null;
							if(callerPhone.length()>7)
								code=callerPhone.substring(0,7);
							Statement stmt1=con.createStatement();
							ResultSet rs1=stmt1.executeQuery("select city,brand from BPW_HLR b where b.code="+code);
							String city=null;
							String brand=null;
							if(rs1.next()){
								city=rs1.getString(1)+"��";
								brand=rs1.getString(2);
								pstmt.setString(4, rs1.getString(1)+"��");//city
								pstmt.setString(5, rs1.getString(2));//brand
							}else{
								pstmt.setString(4, null);//city
								pstmt.setString(5, null);//brand
							}
							rs1.close();
							stmt1.close();
							
							Statement stmt2=con.createStatement();
							ResultSet rs2=stmt2.executeQuery("select terminal,os,type from mmds_terminal_os t where t.callerphone="+callerPhone);
							String terminal=null;
							String os=null;
							String kind=null;
							if(rs2.next()){
								terminal=rs2.getString(1);
								os=rs2.getString(2);
								kind=rs2.getString(3);
								pstmt.setString(6, rs2.getString(1));//terminal
								pstmt.setString(7, rs2.getString(2));//os
								pstmt.setString(9, rs2.getString(3));//os
							}else{
								pstmt.setString(6, null);//terminal
								pstmt.setString(7, null);//os
								pstmt.setString(9, null);//os
							}
							rs2.close();
							stmt2.close();
							if(rs.next()){
								//ƥ�䲡���������Բ���ʵʱ������ݺ���Ϊ����
								if(null!=rs.getString(3)&&"1".equals(rs.getString(3))){//ƥ�����ѷ�������������״̬Ϊ1
									pstmt.setInt(8, 1);//ƥ�䵽�ѷ����Ĳ���,״̬Ϊ1
									
									//ʵʱ����
									pstmtReal.setLong(1, Long.parseLong(row[1]));//callerphone
									pstmtReal.setString(2, rs.getString(1));//name
									pstmtReal.setTimestamp(3, timestamp);//statdate
									pstmtReal.setString(4, rs.getString(2));//virus id
									pstmtReal.addBatch();
								
									//��Ϊ����
									pstmtBehavior.setLong(1, Long.parseLong(row[1]));//callerPhone
									pstmtBehavior.setInt(2, 6);//type
									pstmtBehavior.setString(3, city);//city
									pstmtBehavior.setString(4, brand); //brand
									pstmtBehavior.setTimestamp(5, timestamp);//statDate
									pstmtBehavior.setString(6,feature); //�ñ�����Ϊ��������
									pstmtBehavior.setString(7,terminal);//terminal
									pstmtBehavior.setString(8, os);//os
									pstmtBehavior.setString(9, kind);//kind
									pstmtBehavior.addBatch();
								}else if(null!=rs.getString(3)&&"0".equals(rs.getString(3))){//ƥ����δ��������������״̬Ϊ1
									pstmt.setInt(8, 2);//ƥ�䵽�ѷ����Ĳ���,״̬Ϊ2
									
									//��Ϊ����
									pstmtBehavior.setLong(1, Long.parseLong(row[1]));//callerPhone
									pstmtBehavior.setInt(2, 6);//type
									pstmtBehavior.setString(3, city);//city
									pstmtBehavior.setString(4, brand); //brand
									pstmtBehavior.setTimestamp(5, timestamp);//statDate
									pstmtBehavior.setString(6,feature); //�ñ�����Ϊ��������
									pstmtBehavior.setString(7,terminal);//terminal
									pstmtBehavior.setString(8, os);//os
									pstmtBehavior.setString(9, kind);//kind
									pstmtBehavior.addBatch();
								}
							}else{
								pstmt.setInt(8, 0);//ûƥ����������״̬Ϊ0
							}
							rs.close();//�ر�ƥ�䲡���Ĳ�ѯ
							stmt.close();
							pstmt.addBatch();
						}

					if (i % LEN == 0) {
						log.info("handle---> "+i+" line mail");
						pstmtReal.executeBatch();
						pstmtBehavior.executeBatch();
						DBUtil.releaseStatement(pstmtReal);
						DBUtil.releaseStatement(pstmtBehavior);
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
			log.info("file lenght-->"+i);
			br.close();
			br = null;
			

			
			if (con!=null&&!con.isClosed()) {
				pstmtReal.executeBatch();
				pstmtBehavior.executeBatch();
				DBUtil.releaseStatement(pstmtReal);
				DBUtil.releaseStatement(pstmtBehavior);
				pstmt.executeBatch();
				DBUtil.release(pstmt, con);
			}
			
		} catch (IOException e) {

			log.error(e);
		} catch (Exception e) {

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
	 * ����ļ�����".ok"��β�ľ͹��˵�,����ʣ�µ��ļ�������ʱ����������,
	 * �õ���û������ļ��б�
	 * @param fileNames
	 * @return
	 */
	private String[] getSortFileNames(){
	
		List<String> noSortFile=new ArrayList<String>();
		String fileName=null;
		File dir = new File(ftpconf);
		String[] fileNames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(!name.endsWith(".ok")&&name.endsWith(".txt")){//����'.ok'��β�� mail_0829.txt
					return true;
				}
				return false;
			}
		});
		if(fileNames!=null)
		for(int i=0;i<fileNames.length;i++){
			fileName=fileNames[i];
			if(fileName!=null){
				noSortFile.add(fileName);//ʣ�²���'.ok'��β��
			}
		}
		String[] sortFile=new String[noSortFile.size()];
		Collections.sort(noSortFile);//����
		for(int i=0;i<noSortFile.size();i++){
			sortFile[i]=noSortFile.get(i);
		}
		return sortFile;
	}
	/**
	 * ����
	 * @param string
	 */
	private void createTable(String tableName) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			String sql="create table "+tableName+"(" +
			   "statDate  DATE,"+
			   "callerPhone   NUMBER(19),"+
			   "feature  varchar(50)," +
			   "city  varchar(50)," +
			   "brand  varchar(50)," +
			   "terminal varchar(50),"+
			   "os varchar(50),"+
			   "status  INTEGER," +
			   "kind varchar(50)"+
			   ")";
			//log.info("create table :"+sql);
			stmt.execute(sql);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, con);
		}
		
	}
	/**
	 * ���Ѿ�������ļ����ݵ�ָ��Ŀ¼
	 * @param fileName
	 *//*
	private void modifyFileName(String fileName){
		String filePath="/data/mmds/mail/";
		 try {    
			 File file = new File(ftpconf + File.separator + fileName);// �ļ�
           if (file.isFile()) {    
               FileInputStream input = new FileInputStream(file);    
               FileOutputStream output = new FileOutputStream(filePath + (file.getName()).toString());    
               byte[] b = new byte[1024 * 5];    
               int len;    
               while ((len = input.read(b)) != -1) {    
                   output.write(b, 0, len);    
               }    
               output.flush();    
               output.close();    
               input.close();  
               file.delete();//ɾ��
           }    
		          
	    } catch (Exception e) {    
	        System.out.println("���������ļ������ݲ�������");    
	        e.printStackTrace();    
	    }  
	}*/
	/**
	 * ���Ѿ�������ļ�,���ļ�������".ok"��β
	 * @param fileName
	 */
	private void modifyFileName(String fileName){
		File file=new File(ftpconf+File.separator+fileName);//�ļ�
		
		file.renameTo(new File(ftpconf+File.separator+fileName+".ok"));//����
		
	}
	/**
	 * @param string
	 */
	private void createIndex(String currentDate) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();

			String sql = "create index IX_MAIL_"+currentDate+" on MAIL_"+currentDate+"(callerphone)";
			stmt.execute(sql);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, con);
		}
		log.info("finish create index:IX_MAIL_"+currentDate+"");
	}
	/**
	 * �жϱ��Ƿ����
	 * @ahthor DengJianhua
	 * 2010-7-21����02:45:06
	 * @return
	 */
	private boolean isExistTable(String tableName) {
		boolean flag=false;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			String sql=" select table_name from user_tables where table_name= '"+tableName+"' ";//oracle
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				return flag=true;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs, stmt, con);
			return flag;
		}
	}
	/**
	 * �����õ���Ҫ������
	 */
	public void doSync() {
		String[] filenames = null;
		String tableName = null;
		filenames = getSortFileNames();//����ļ���,�����Դ����('.ok'��β)�ļ�����,����ʱ���������� 
		String currentDate=null;
		if(filenames!=null&&filenames.length>0){
			for (String fileName : filenames) {
				//currentDate=fileName.substring(5, 7)+fileName.substring(8, 10);//2010-08-29_detail.txt  
				currentDate=fileName.substring(5,9);//mail_0829.txt
				tableName="MAIL_"+currentDate;
				if(!isExistTable(tableName)){
					createTable(tableName);
				}
				handle(fileName);//���ļ����ݵ������ݿ�
				modifyFileName(fileName);//������õ��ļ�����
				log.info("do with "+currentDate+" mail end");
			}
		}
	//	createIndex(currentDate);//���ֻ����佨����
		log.info("-------------------->end mail import");
	}
	
	public static void main(String[] argv) {
		TaskMail t=new TaskMail();
	
		t.doSync();
	}
}
