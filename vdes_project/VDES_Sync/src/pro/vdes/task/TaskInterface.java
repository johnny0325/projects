/**
 * Task_Interface.java 2009-5-31 ����05:16:01
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
	//��Զ�̵���������ͬ������СʱΪ��λ��;
	private String syncObj = "";//ͬ��������Ԫ:ne,���У�city.
	public TaskInterface(String syncObj){
		this.syncObj = syncObj;	
	}
	public void syncHourData(){
		System.out.print("syncHourData");
		doSync();
	}
	//����7�������ɾ��
	public void deleteBeforeDateInWeek(){
		System.out.print("deleteBeforeDateInWeek");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			//��˾���ԣ�
			String table = "bpw_mms_"+syncObj+"_week";
			rs = stmt.executeQuery("select top (1) statDate from "+table+" order by statDate desc");
			if(rs.next()){
				//��nextDateTime�ں��������в�����һ��Сʱ������
				String sql = "delete from  "+table+" where datediff(d,statdate,'"+DateUtil.formatDate(rs.getTimestamp(1), "yyyy-MM-dd")+"')>7";// ����7�������ɾ��;
				int i = stmt.executeUpdate(sql);
				log.info("deleteBeforeDateInWeek |  | ɾ�� "+i+" ��");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		
	}
	//������31�յ�����ɾ������
	public void deleteBeforDateInMonth(){
		System.out.print("deleteBeforDateInMonth");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			//��˾���ԣ�
			String table = "bpw_mms_"+syncObj+"_month";
			rs = stmt.executeQuery("select top (1) statDate from "+table+" order by statDate desc");
			if(rs.next()){
				//��nextDateTime�ں��������в�����һ��Сʱ������
				String sql = "delete from  "+table+" where datediff(d,statdate,'"+DateUtil.formatDate(rs.getTimestamp(1), "yyyy-MM-dd")+"')>31";// ����31�������ɾ��;
				int i = stmt.executeUpdate(sql);
				log.info("deleteBeforDateInMonth  | ɾ�� "+i+" ��");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		
	}

	//�Ƿ���Ҫ����ͬ����true:������false:��������
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
			currentDate = nextDay(currentDate,-3);//����������ݽ���ͬ������Ŀǰ��15�ţ���Ҫ��13��,14��,15�Ŷ�ͬ������Ϊ��������48Сʱ�ȶ���
			String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
			while(currentDate.compareTo(today)<=0){
//				if(currentDate.compareTo(today)<0){
//					doSyncBeforeDay(currentDate);
//					
//				}else{
//					doSyncCurrentDay(currentDate);
//				}
				
				//����ÿ��ͬ�����ݵ�ʱ�����������ݶ�������!����Ҫ��ǰ�ķֿ�����ʽ�� .modify by aiyan 2009-06-17
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

			
			//�����ܱ���ɾ��Ŀǰ��������ݣ�
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
			// ����д���
			int[] b = v_pstmt.executeBatch();
			log.info(b.length);
			//�ر����ݿ����
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
			// ����д���
			int[] b = v_pstmt.executeBatch();
			log.info(b.length);
			//�ر����ݿ����
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
				c.add(Calendar.DATE, -3);//����������ݽ���ͬ������Ŀǰ��15�ţ���Ҫ��13��,14��,15�Ŷ�ͬ������Ϊ��������48Сʱ�ȶ���
				currnetDateTime = c.getTime();	
			}else{
				c.add(Calendar.DATE, -7);
				currnetDateTime = c.getTime();			
			}
			
			//�����±���ɾ��Ŀǰ��������ݣ�
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
