/**
 * TaskMMSCityRadio.java 2009-11-5 下午02:52:04
 */
package pro.vdes.task;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import common.db.CommonDao;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.DateUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskMMSCityRadio{
	private static Logger log = Logger.getLogger(TaskMMSCityRadio.class);
	
	public void doSync(){
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top(1) statDate from test_bpw_mms_city_Radio order by statDate desc");
			String currentDay = null;
			if(rs.next()){
				currentDay = DateUtil.format(rs.getTimestamp(1), "yyyy-MM-dd");
			}else{
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -10);
				currentDay = DateUtil.format(c.getTime(), "yyyy-MM-dd");			
			}
			currentDay = nextDay(currentDay,-2);//如果对三天的数据进行同步；如目前是15号，则要对13号,14号,15号都同步；
			String today = DateUtil.format(new Date(), "yyyy-MM-dd");
			while(currentDay.compareTo(today)<=0){
				generate(currentDay);
				currentDay = nextDay(currentDay,1);
				
				
			}
		} catch (SQLException e) {
			log.equals(e);
			log.error(e);
		} catch (Exception e) {
			log.equals(e);
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
	}
	
	public void existsData(){
		Date max = maxStatDate();
		if (max != null){
			delete(">",DateUtil.format(max,"yyyy-MM-dd"),30);
		}
		
		
	}
	
	private Date maxStatDate(){
		Date max = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top(1) statDate from test_bpw_mms_city_Radio order by statDate desc");
			if(rs.next()){
				max = rs.getDate("statDate");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.equals(e);
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.equals(e);
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		return max;
		
	}

	public void delete(String oper,String day, int num){
		CommonDao dao = null;
		try {
			dao = new CommonDao();			
			String sql = "delete from test_bpw_mms_city_Radio where datediff(d,statDate,'"+day+"')"+oper+" "+num+" )";
			dao.execute(sql, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally{
			try {
				if(dao!=null) dao.close();			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.equals(e);
				log.error(e);
			}
		}
	}
	// 当天msscity.log.20090623装换到sqlserver数据库
	
	/*private void generate(String currentDay) {
		System.out.println("generateMMSCityRadio");
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {		
			con = DBUtil.getConnection("proxool.vdes");
			con.setAutoCommit(false);
			stmt = con.createStatement();
			String ss = "delete from test_bpw_mms_city_Radio where datediff(d,statDate,'"+myDay(currentDay)+"')=0";
			System.out.println(ss);
			stmt.executeUpdate("delete from test_bpw_mms_city_Radio where datediff(d,statDate,'"+myDay(currentDay)+"')=0");
			pstmt = con.prepareStatement("insert into test_bpw_mms_city_Radio(statDate,city,succCount,totalCount) values(?,?,?,?)");
			
			String syncUrlRoot = new ConfigureUtil().getValue("mmsCityRadio");
			URL url = new URL(syncUrlRoot +"msscity.log." + currentDay);
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			// 连接指定的网络资源
			httpUrl.connect();
			// 获取网络输入流
			BufferedInputStream bis = new BufferedInputStream(httpUrl
					.getInputStream());
			InputStreamReader reader = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			String[] radios = null; 
			while ((line = br.readLine()) != null) {
				if(line.indexOf("|")!=-1){
					radios = line.split("\\|");
					if(radios!=null&&radios.length>4){
						pstmt.setString(1, radios[0].replaceAll("_", " "));
						pstmt.setString(2, radios[1]);
						pstmt.setInt(3, Integer.parseInt(radios[2])-Integer.parseInt(radios[3]));
						pstmt.setString(4, radios[2]);			
						pstmt.addBatch();		
					}
				}
			}
			int[] a = pstmt.executeBatch();
			con.commit();
			
			
		}catch(FileNotFoundException e){
			log.error(e);
			
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.releaseStatement(stmt);
			DBUtil.releaseStatement(pstmt);
			DBUtil.releaseConnection(con);
		}

	}*/

	// 当天output.q.2009-11-18.sql装换到sqlserver数据库
	private void generate(String currentDay) {
		System.out.println("generateMMSCityRadio");
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {		
			con = DBUtil.getConnection("proxool.vdes");
			con.setAutoCommit(false);
			stmt = con.createStatement();
			String ss = "delete from test_bpw_mms_city_Radio where datediff(d,statDate,'"+currentDay+"')=0";
			System.out.println(ss);
			stmt.executeUpdate("delete from test_bpw_mms_city_Radio where datediff(d,statDate,'"+currentDay+"')=0");
			pstmt = con.prepareStatement("insert into test_bpw_mms_city_Radio(statDate,city,succCount,totalCount) values(?,?,?,?)");
			
			String syncUrlRoot = new ConfigureUtil().getValue("mmsCityRadio");
			URL url = new URL(syncUrlRoot +"output.q." + currentDay+".sql");
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			// 连接指定的网络资源
			httpUrl.connect();
			// 获取网络输入流
			BufferedInputStream bis = new BufferedInputStream(httpUrl
					.getInputStream());
			InputStreamReader reader = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			String reg="(\\d{4}-\\d{2}-\\d{2}\\s*\\d{2}:\\d{2}:\\d{2})\\s*([a-z]+)\\s*(\\d+)\\s*(\\d+).*";
			Pattern pattern = Pattern.compile(reg);
			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					pstmt.setString(1, matcher.group(1));
					pstmt.setString(2, matcher.group(2));
					pstmt.setInt(3, Integer.parseInt(matcher.group(3))-Integer.parseInt(matcher.group(4)));
					pstmt.setString(4, matcher.group(3));			
					pstmt.addBatch();		
				}
				
			}
			pstmt.executeBatch();
			con.commit();
			
			
		}catch(FileNotFoundException e){
			log.error(e);
			
		}catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}finally{
			DBUtil.releaseStatement(stmt);
			DBUtil.releaseStatement(pstmt);
			DBUtil.releaseConnection(con);
		}

	}
	private String nextDay(String currentDate,int next){
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0,4));
		int m = Integer.parseInt(currentDate.substring(5,7));
		int d = Integer.parseInt(currentDate.substring(8,10));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m-1);
		c.set(Calendar.DATE, d+next);
		
		
		return DateUtil.format(c.getTime(), "yyyy-MM-dd");
		
	}

}
