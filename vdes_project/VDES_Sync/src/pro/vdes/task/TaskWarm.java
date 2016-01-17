/**
 * TaskWarm.java 2009-4-24 下午01:58:27
 */
package pro.vdes.task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import common.util.ConfigureUtil;
import common.util.DBHelp;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskWarm {
	private static Logger log = Logger.getLogger(TaskWarm.class);

	// 活跃告警txt装换到sqlserver数据库
	
	// http://icare.gd.chinamobile.com:8080/ilook.txt.20090413  日期要变化， 10分钟更新一次  当前活跃告警 http://icare.gd.chinamobile.com:8080/ilook.txt

	public void generateWarmActive() {
		System.out.println("generateWarmActive");
		Connection con = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;

		try {
			con = DBUtil.getConnection("proxool.vdes");
			String sql = "insert into BPW_WarmActive(content,flag,createTime) values(?,1,getdate())";
			String sql2 = "update BPW_WarmActive set flag=1 where content=?";
			
			pstmt = con.prepareStatement(sql);
			pstmt2 = con.prepareStatement(sql2);
			
			String destUrl = new ConfigureUtil().getValue("warmFile");
			URL url = new URL(destUrl);
			//公司测试;
			//url = new URL("http://localhost:8080/VDES/ilook.txt");
			HttpURLConnection httpUrl = (HttpURLConnection) url
					.openConnection();
			// 连接指定的网络资源
			httpUrl.connect();
			// 获取网络输入流
			BufferedInputStream bis = new BufferedInputStream(httpUrl
					.getInputStream());
			InputStreamReader reader = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			while ((line = br.readLine()) != null) {
				//目前BPW_WarmActive是否存在这条记录。不存在，就要添加这条告警；存在，说明这个是有效的，给标志位1，最后会根据标志位清除动作
				
				if(line.length()!=0){
					if(!DBHelp.isExistRecord("select id from BPW_WarmActive where content='"+line+"'")){//不存在；
						pstmt.setString(1, line);
						pstmt.addBatch();
					}else{
						
						pstmt2.setString(1, line);
						pstmt2.addBatch();
					}
				}


			}
			pstmt.executeBatch();
			pstmt2.executeBatch();
			
			
			//删除WARMACTIVE里面的数据flag<>1的数据
			Statement stmt = con.createStatement();
			stmt.execute("delete from BPW_WarmActive where flag <> 1");
			stmt.executeUpdate("update BPW_WarmActive set flag = 0");
			stmt.close();
		

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.releaseStatement(pstmt2);
			DBUtil.release(pstmt, con);
		}

	}

	// 当天告警txt装换到sqlserver数据库
	public void generateWarmDay() {
		System.out.println("generateWarmDay");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			String sql = "insert into bpw_warmday(content,createTime) values(?,getdate())";
			pstmt = con.prepareStatement(sql);
			
			String destUrl = new ConfigureUtil().getValue("warmFile");
			
			String today=new java.text.SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
			URL url = new URL(destUrl+"."+today);
			
			//公司测试;
			//url = new URL("http://localhost:8080/VDES/ilook_txt_20090416.txt");
			HttpURLConnection httpUrl = (HttpURLConnection) url
					.openConnection();
			// 连接指定的网络资源
			httpUrl.connect();
			// 获取网络输入流
			BufferedInputStream bis = new BufferedInputStream(httpUrl
					.getInputStream());
			InputStreamReader reader = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(reader);
			String line = "";

			while ((line = br.readLine()) != null) {
				if(line.length()!=0){
					if(!DBHelp.isExistRecord("select id from bpw_warmday where datediff(d,createTime,getdate())=0 and content='"+line+"'")){
						pstmt.setString(1, line);
						pstmt.addBatch();	                    	
	                    }
				}


			}
			pstmt.executeBatch();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(pstmt, con);
		}

	}

	//将本日告警的数据指明哪些是已解决。
	public void generateWarmDayStatus(){
		System.out.println("generateWarmDayStatus");
		Connection con = null;
		Statement stmt = null;
		
		
		try {
			con = DBUtil.getConnection("proxool.vdes");
			String sql = "update bpw_warmday set status = 2 where id in (select d.id from bpw_warmday d left join bpw_warmactive a on d.content = a.content where a.id is null)";
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		finally {
			DBUtil.release(stmt, con);
		}
		
		
	}
	
	//将前一天的数据转移到历史数据表中。
	public void generateWarmDayHistory(){
		System.out.println("generateWarmDayHistory");
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String sql = "select top(1) createTime from bpw_warmdayhistory order by createTime desc";
			ResultSet rs = stmt.executeQuery(sql);
			Date currentDateTime = null;
			
			if(rs.next()){
				currentDateTime = rs.getTimestamp(1);
			}
			if(currentDateTime!=null){
				sql = "insert into bpw_warmdayhistory(content,createTime,userName,processTime,result,userId,status) select content,createTime,userName,processTime,result,userId,status from bpw_warmday where datediff(d,'"+new java.sql.Timestamp(currentDateTime.getTime())+"',getdate())>0";
				System.out.println(sql);
			}else{
				sql = "insert into bpw_warmdayhistory(content,createTime,userName,processTime,result,userId,status) select content,createTime,userName,processTime,result,userId,status from bpw_warmday";
			}
			stmt.executeUpdate(sql);
			sql = "delete from bpw_warmday where datediff(d,createTime,getdate())>0";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		finally {
			DBUtil.release(stmt, con);
		}
		
		
	}

}
