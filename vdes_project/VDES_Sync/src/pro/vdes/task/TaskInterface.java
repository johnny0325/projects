/**
 * Task_Interface.java 2009-5-31 下午05:16:01
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

import common.util.CityUtil;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskInterface {
	private static Logger log = Logger.getLogger(TaskInterface.class);
	//从远程到本地数据同步（以小时为单位）;
	private String syncObj = "";//同步对象：网元:ne,城市：city.
	public TaskInterface(String syncObj){
		this.syncObj = syncObj;	
	}
	public void syncHourData(){
		System.out.print("syncHourData");
		doSync();
	}
	//大于7天的数据删除
	public void deleteBeforeDateInWeek(){
		System.out.print("deleteBeforeDateInWeek");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			//公司测试；
			String table = "bpw_mms_"+syncObj+"_week";
			rs = stmt.executeQuery("select top (1) statDate from "+table+" order by statDate desc");
			if(rs.next()){
				//以nextDateTime在合作方库中查找下一个小时的数据
				String sql = "delete from  "+table+" where datediff(d,statdate,'"+DateUtil.formatDate(rs.getTimestamp(1), "yyyy-MM-dd")+"')>7";// 大于7天的数据删除;
				int i = stmt.executeUpdate(sql);
				log.info("deleteBeforeDateInWeek |  | 删除 "+i+" 条");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		
	}
	//将大于31日的数据删除掉；
	public void deleteBeforDateInMonth(){
		System.out.print("deleteBeforDateInMonth");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			//公司测试；
			String table = "bpw_mms_"+syncObj+"_month";
			rs = stmt.executeQuery("select top (1) statDate from "+table+" order by statDate desc");
			if(rs.next()){
				//以nextDateTime在合作方库中查找下一个小时的数据
				String sql = "delete from  "+table+" where datediff(d,statdate,'"+DateUtil.formatDate(rs.getTimestamp(1), "yyyy-MM-dd")+"')>31";// 大于31天的数据删除;
				int i = stmt.executeUpdate(sql);
				log.info("deleteBeforDateInMonth  | 删除 "+i+" 条");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		
	}

	//是否需要继续同步，true:继续，false:不继续。
	public void doSync(){
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select top (1) statDate from bpw_mms_"+syncObj+"_week order by statDate desc");
			String currentDate = null;
			if(rs.next()){
				currentDate = DateUtil.formatDate(rs.getTimestamp(1), "yyyyMMdd");
			}else{
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDate = DateUtil.formatDate(c.getTime(), "yyyyMMdd");			
			}
			currentDate = nextDay(currentDate,-3);//对三天的数据进行同步；如目前是15号，则要对13号,14号,15号都同步；因为彩信数据48小时稳定；
			String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
			while(currentDate.compareTo(today)<=0){
//				if(currentDate.compareTo(today)<0){
//					doSyncBeforeDay(currentDate);
//					
//				}else{
//					doSyncCurrentDay(currentDate);
//				}
				
				//由于每次同步数据的时候把三天的数据都清理了!不需要以前的分开处理方式了 .modify by aiyan 2009-06-17
				doSyncBeforeDay(currentDate);
				currentDate = nextDay(currentDate,1);
				
			}
		} catch (SQLException e) {
			log.error(e);
		} finally {
			DBUtil.release(stmt, con);
		}

		
	}
	private String nextDay(String currentDate,int next){
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0,4));
		int m = Integer.parseInt(currentDate.substring(4,6));
		int d = Integer.parseInt(currentDate.substring(6,8));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m-1);
		c.set(Calendar.DATE, d+next);
		
		
		return DateUtil.formatDate(c.getTime(), "yyyyMMdd");
		
	}
	
	private String myDay(String currentDate){
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0,4));
		int m = Integer.parseInt(currentDate.substring(4,6));
		int d = Integer.parseInt(currentDate.substring(6,8));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m-1);
		c.set(Calendar.DATE, d);
		
		
		return DateUtil.formatDate(c.getTime(), "yyyy-MM-dd");
		
	}
	
	public void doSyncBeforeDay(String currentDay){

		
		Connection p_con = null;
		Connection v_con = null;
		try {
			p_con = DBUtil.getConnection("proxool.partner");
			v_con = DBUtil.getConnection("proxool.vdes");
			Statement v_stmt = null;
			PreparedStatement v_pstmt = null;
			
			Statement p_stmt = null;
			ResultSet p_rs = null;


			
			p_stmt = p_con.createStatement();
			v_stmt = v_con.createStatement();
			v_pstmt = v_con.prepareStatement("insert into bpw_mms_"+syncObj+"_week("+syncObj+"Code,statDate,indicator,status,count) values(?,?,?,?,?)");

			
			//先在周表中删除目前这天的数据；
			String table_from = "mms_"+syncObj+"_"+currentDay;	
			String sql = "delete from bpw_mms_"+syncObj+"_week where datediff(d,'"+myDay(currentDay)+"',statDate)=0";
			int del = v_stmt.executeUpdate(sql);
			log.info("del"+del);
	
		    p_rs  = p_stmt.executeQuery("select * from "+ table_from);
		

			int i = 0;
			while(p_rs.next()){
				//log.info(syncObj.toLowerCase()+"----"+p_rs.getString(syncObj+"Code")+"----"+CityUtil.getCode(p_rs.getString(syncObj+"Code")+""));
				if(syncObj.toLowerCase().equals("ne")){
					v_pstmt.setString(1, p_rs.getString(syncObj+"Code"));
					
				}else{
					v_pstmt.setString(1, CityUtil.getCode(p_rs.getString(syncObj+"Code")));
				}
				
				v_pstmt.setTimestamp(2, p_rs.getTimestamp("statDate"));
				v_pstmt.setString(3, p_rs.getString("indicator"));
				v_pstmt.setString(4, p_rs.getString("status"));
				v_pstmt.setString(5, p_rs.getString("count"));
				v_pstmt.addBatch();
				
				
				if (++i % 500 != 0) {
					continue;
				}		
				int[] a = v_pstmt.executeBatch();
				log.info("a.length"+a.length);
				
			}
			// 最后几行处理
			int[] b = v_pstmt.executeBatch();
			log.info(b.length);
			//关闭数据库对象；
			p_rs.close();
			p_stmt.close();
			v_pstmt.close();
			

		} catch (SQLException e) {
			log.error(e);
		} finally {
			DBUtil.releaseConnection(p_con);
			DBUtil.releaseConnection(v_con);
		}
		
	}
	
	public void doSyncCurrentDay(String currentDay){

		
		Connection p_con = null;
		Connection v_con = null;
		try {
			p_con = DBUtil.getConnection("proxool.partner");
			v_con = DBUtil.getConnection("proxool.vdes");
			PreparedStatement v_pstmt = null;
			
			Statement p_stmt =  p_con.createStatement();
			ResultSet p_rs = null;
			
			Statement v_stmt = v_con.createStatement();
			ResultSet v_rs = v_stmt.executeQuery("select top (1) statDate from bpw_mms_"+syncObj+"_week order by statDate desc");
			v_pstmt = v_con.prepareStatement("insert into bpw_mms_"+syncObj+"_week("+syncObj+"Code,statDate,indicator,status,count) values(?,?,?,?,?)");
			
			Date nextDateTime = null;
			if(v_rs.next()){
				nextDateTime = getNextDateTime(v_rs.getTimestamp(1));
			}
			String table_from = "mms_"+syncObj+"_"+currentDay;			
			
			if(nextDateTime!=null){
				String sql = "select * from "+ table_from+" where datediff(hh,statdate,'"+new java.sql.Timestamp(nextDateTime.getTime())+"')<=0";
				log.info("sql:       "+sql);
				p_rs  = p_stmt.executeQuery(sql);
			}
			
			int i = 0;
			while(p_rs.next()){
				//log.info(syncObj.toLowerCase()+"----"+p_rs.getString(syncObj+"Code")+"----"+CityUtil.getCode(p_rs.getString(syncObj+"Code")+""));
				if(syncObj.toLowerCase().equals("ne")){
					v_pstmt.setString(1, p_rs.getString(syncObj+"Code"));
					
				}else{
					v_pstmt.setString(1, CityUtil.getCode(p_rs.getString(syncObj+"Code")));
				}
				
				v_pstmt.setTimestamp(2, p_rs.getTimestamp("statDate"));
				v_pstmt.setString(3, p_rs.getString("indicator"));
				v_pstmt.setString(4, p_rs.getString("status"));
				v_pstmt.setString(5, p_rs.getString("count"));
				v_pstmt.addBatch();
				
				
				if (++i % 500 != 0) {
					continue;
				}		
				int[] a = v_pstmt.executeBatch();
				log.info("a.length"+a.length);
				
			}
			// 最后几行处理
			int[] b = v_pstmt.executeBatch();
			log.info(b.length);
			//关闭数据库对象；
			p_rs.close();
			v_rs.close();
			p_stmt.close();
			v_stmt.close();
			v_pstmt.close();
			

		} catch (SQLException e) {
			log.error(e);
		} finally {
			DBUtil.releaseConnection(p_con);
			DBUtil.releaseConnection(v_con);
		}
		
	}
	
	
	public void doSyncWeek2Month(){
		
		Connection con = DBUtil.getConnection("proxool.vdes");
		try {
			PreparedStatement pstmt = con.prepareStatement("insert into bpw_mms_"+syncObj+"_month("+syncObj+"Code,statDate,indicator,status,count) values(?,?,?,?,?)");
			Statement stmt = con.createStatement();
			ResultSet rs1 = stmt.executeQuery("select top (1) statDate from bpw_mms_"+syncObj+"_month order by statDate desc");
			Date currnetDateTime = null;
			Calendar c = Calendar.getInstance();
			if(rs1.next()){
				currnetDateTime = rs1.getTimestamp(1);								
				c.setTime(currnetDateTime);
				c.add(Calendar.DATE, -3);//对三天的数据进行同步；如目前是15号，则要对13号,14号,15号都同步；因为彩信数据48小时稳定；
				currnetDateTime = c.getTime();	
			}else{
				c.add(Calendar.DATE, -7);
				currnetDateTime = c.getTime();			
			}
			
			//先在月表中删除目前这天的数据；
			String sql = "delete from bpw_mms_"+syncObj+"_month where datediff(d,statDate,getdate())>0 and datediff(d,'"+DateUtil.formatDate(currnetDateTime, "yyyy-MM-dd")+"',statDate)>0";
			int del = stmt.executeUpdate(sql);
			log.info("doSyncWeek2Month->del"+del);
			
			
			
			String table = "bpw_mms_"+syncObj+"_week";
			ResultSet rs = null;
			sql = "select "+syncObj+"Code,convert(varchar(10),statDate,120) as statDate,indicator,status,sum([count]) as [count] from "+ table+" where datediff(d,statDate,getdate())>0 and datediff(d,'"+DateUtil.formatDate(currnetDateTime, "yyyy-MM-dd")+"',statDate)>0 group by "+syncObj+"Code, convert(varchar(10),statDate,120),indicator,status ";
				log.info("sql:       "+sql);
				rs  = stmt.executeQuery(sql);
			

			while(rs.next()){
				pstmt.setString(1, rs.getString(syncObj+"Code"));
				pstmt.setString(2,rs.getString("statDate"));
				pstmt.setString(3, rs.getString("indicator"));
				pstmt.setString(4, rs.getString("status"));
				pstmt.setString(5, rs.getString("count"));
				pstmt.addBatch();	
			}
			int[] a = pstmt.executeBatch();
			log.info("a.length"+a.length);
			
			rs.close();
			rs1.close();
			stmt.close();
			pstmt.close();


		} catch (SQLException e) {
			log.error(e);
		} finally {
			DBUtil.releaseConnection(con);
		}

		
	}
	private Date getNextDateTime(Date currentdate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentdate);
		cal.add(Calendar.HOUR_OF_DAY, 1);
		return cal.getTime();
	}


	

	public static void main(String[] argv){
		//new TaskInterface("ne").syncHourData();
		//new TaskInterface("ne").sysToDayDateTOMonth();
		
	}

}
