/**
 * TaskCheckReport.java 2009-5-13 下午03:32:56
 */
package pro.vdes.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import common.db.CommonDao;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.StringUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskCheckReport {
	private static Logger log = Logger.getLogger(TaskCheckReport.class);

	// 已经审核通过的可以处理文件了！
	public void generateCheckReport() {
		System.out.println("start generateCheckReport");


	    List<Map> list = getCheckReport();
	    if(list==null) return ;
		for(Map map:list){
			generateCheckReportTXT2(map.get("factname")+"", map.get("city")+"");
			updateDB(map.get("id")+"");
		}
	}
	
	private List getCheckReport(){
		List result = null;
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			result = dao.query("select id,factname,city from cww_checkReport where status = 1", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			log.error(e);
		}finally{
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
		return result;
	}

	private boolean generateCheckReportTXT(String factName, String city) {
		boolean flag = true;

		Connection con = DBUtil.getConnection("proxool.mmsc");
		try {

			String uploadfile = new ConfigureUtil().getValue("uploadfile");
			File srcFile = new File(uploadfile, factName);
			BufferedReader br = new BufferedReader(new FileReader(srcFile));
			String line = "";

			Statement stmt = con.createStatement();
			// String tempTSql = "if not (select object_id('Tempdb..#t')) is
			// null drop table #t";
			// stmt.execute(tempTSql);
			String tempTSql = "create TEMPORARY table c (tcode varchar(11),city varchar(20)) ";
			stmt.execute(tempTSql);

			PreparedStatement pstmt = con
					.prepareStatement("insert into c(tcode) values(?)");
			int i = 0;
			while ((line = br.readLine()) != null) {
				pstmt.setString(1, line);
				pstmt.addBatch();
				i++;
				if (i % 300 != 0) {
					continue;
				}
				if (pstmt != null) {
					pstmt.executeBatch();
				}
				log.info(i + "-i%300--" + i % 300);
				String downloadfile = new ConfigureUtil()
						.getValue("downloadfile");
				File p = new File(downloadfile);
				if (!p.exists()) {
					p.mkdirs();
				}

				File desFile = new File(downloadfile, factName);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(desFile, true)));

				ResultSet rs = stmt
						.executeQuery("select c.tcode,i.lastmmstime from  c left join ic2 i on c.tcode=i.msisdn and i.city='"
								+ city + "'");
				while (rs.next()) {
					bw.write(rs.getString(1) + " # "
							+ StringUtil.getString(rs.getString(2), ""));
					bw.write("\r\n");
				}
				bw.close();
				pstmt.executeUpdate("delete from c");// 清空零时表防止成为下次查询的源

			}
			// 最后几行处理
			if (pstmt != null) {
				pstmt.executeBatch();
			}
			String downloadfile = new ConfigureUtil().getValue("downloadfile");
			File desFile = new File(downloadfile, factName);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(desFile, true)));

			ResultSet rs = stmt
					.executeQuery("select c.tcode,i.lastmmstime from  c left join ic2 i on c.tcode=i.msisdn and i.city='"
							+ city + "'");
			while (rs.next()) {
				bw.write(rs.getString(1) + " # "
						+ StringUtil.getString(rs.getString(2), ""));
				bw.write("\r\n");
			}
			bw.close();

			pstmt.executeUpdate("drop table c");// 清空零时表防止成为下次查询的源

			flag = true;

			if (stmt != null) {
				stmt.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			log.error(e);
		}finally {
			DBUtil.releaseConnection(con);
		}
		return flag;

	}

	private void generateCheckReportTXT2(String factName,String city) {
		log.info("start checkreport file:"+factName);

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
			final int LEN = 50;
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
					
					if(line.trim().length()==14){	// 手机号码长度14
						//System.out.println("the line is(14)==>"+line);
						rs = stmt.executeQuery("select i.lastmmstime from  ic2 i where i.msisdn="+line.substring(3,14)+" "+condition+"   limit 0,1");
						if(rs.next()){
							bw.write(line+" # "+StringUtil.getString(rs.getString(1).substring(0,10),""));
							bw.write("\r\n");
	
						}else{
							bw.write(line+" # ");
							bw.write("\r\n");
							
						}
					}
					
					if(line.trim().length()==13){	// 手机号码长度13
						//System.out.println("the line is(13)==>"+line);
						rs = stmt.executeQuery("select i.lastmmstime from  ic2 i where i.msisdn="+line.substring(2,13)+" "+condition+"   limit 0,1");
						if(rs.next()){
							bw.write(line+" # "+StringUtil.getString(rs.getString(1).substring(0,10),""));
							bw.write("\r\n");
	
						}else{
							bw.write(line+" # ");
							bw.write("\r\n");
							
						}
					}
					
					if(line.trim().length()==11){	// 手机号码长度11
						//System.out.println("the line is(11)==>"+line);
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
					log.error(e);
				}
			}
			bw.close();					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e);
			}finally{
				DBUtil.release(rs,stmt, con);	
			}
			log.info("end checkreport file:"+factName);
	}

	private void updateDB(String id) {
		Connection con = DBUtil.getConnection("proxool.vdes");
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.execute("update cww_checkreport set status=3 where id=" + id);
		} catch (SQLException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.release(stmt, con);
		}

	}

	public static void main(String[] argv) throws IOException, SQLException {
		TaskCheckReport tr = new TaskCheckReport();
		tr.generateCheckReportTXT2("801234.txt", "province");
		//tr.generateCheckReportTXT2("819313.txt", "province");
		//tr.generateCheckReportTXT2("819321.txt", "province");

	}

}