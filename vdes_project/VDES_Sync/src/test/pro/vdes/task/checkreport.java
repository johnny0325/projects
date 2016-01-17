package test.pro.vdes.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import pro.vdes.task.TaskCheckReport;

import common.db.CommonDao;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.StringUtil;

public class checkreport extends TestCase{
	private static Logger log = Logger.getLogger(TaskCheckReport.class);
	public void testA(){
		boolean a = generateCheckReportTXT2("1253072583445.txt","province");
		System.out.println("a:"+a);
	}
	
	public void testB(){
		final TaskCheckReport taskCheckReport = new TaskCheckReport();
		CommonDao dao;
		try {
			dao = new CommonDao();
			List<Map> list = dao.query("select id,factname,city from cww_checkReport where status = 1", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private boolean generateCheckReportTXT2(String factName,String city) {
		log.info("start checkreport file:"+factName);
		boolean flag = false;

		String uploadfile = new ConfigureUtil().getValue("uploadfile");
		String downloadfile = new ConfigureUtil().getValue("downloadfile");
		File srcFile = new File(uploadfile, factName);
		
		File p = new File(downloadfile);
		if(!p.exists()){
			p.mkdirs();
		}
				
		File desFile = new File(downloadfile, factName);
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			br = new BufferedReader(new FileReader(srcFile));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(desFile, false)));
			
			String condition = "";
			if(!city.equals("province")){
				condition = " and i.city='"+city+"'";
			}		

			String line = "";
			int i=0;
			//int num = 0;
			final int LEN = 1000;
			//final int NUM_MSISDN = 50;//每50个手机号码执行一次SQL;由于是向ICARE系统查询,这里做点优化而已 add by aiyan 2009-09-17 
			//StringBuffer sb = new StringBuffer(); 
		
			while ((line = br.readLine()) != null) {
				try{
					//对数据库连接的优化
					if(con==null||i%LEN==0) {
						con = DBUtil.getConnection("proxool.mmsc");
						log.info("icare de con:"+con);
						log.info(i);
					}
					++i;
					
					stmt = con.createStatement();
					
					if(line.trim().length()==11){	// 手机号码长度11
						rs = stmt.executeQuery("select i.lastmmstime from  ic2 i where i.msisdn="+line+" "+condition+"   limit 0,1");
						if(rs.next()){
							bw.write(line+" # "+StringUtil.getString(rs.getString(1).substring(0,10),""));
							bw.write("\r\n");
	
						}else{
							bw.write(line+" # ");
							bw.write("\r\n");
							
						}
					}
					
					DBUtil.releaseResultSet(rs);
					DBUtil.releaseStatement(stmt);


					
					if(i%LEN==0) DBUtil.releaseConnection(con);	
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			bw.close();					
			flag = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DBUtil.release(rs,stmt, con);	
			}
	        
			log.info("end checkreport file:"+factName);
			
			return flag;

	}

	private void updateDB(String id) {
		Connection con = DBUtil.getConnection("proxool.vdes");
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.execute("update cww_checkreport set status=3 where id=" + id);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.releaseStatement(stmt);
		}

	}

}
