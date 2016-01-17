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
public class TaskSp {
	private static Logger log = Logger.getLogger(TaskSp.class);
	//������31�յ�����ɾ������
	public void deleteBeforDateInMonth(){
		System.out.print("TaskSp->deleteBeforDateInMonth");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String table = "bpw_mms_sp";
			rs = stmt.executeQuery("select top (1) createTime from "+table+" order by createTime desc");
			if(rs.next()){
				//��nextDateTime�ں��������в�����һ��Сʱ������
				String sql = "delete from  "+table+" where datediff(d,createTime,'"+DateUtil.formatDate(rs.getTimestamp(1), "yyyy-MM-dd")+"')>31";// ����31�������ɾ��;
				int i = stmt.executeUpdate(sql);
				log.info("TaskSp->deleteBeforDateInMonth  | ɾ�� "+i+" ��");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		
	}

	public void doSync(){
		System.out.print("syncSpData");
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select top (1) createTime from bpw_mms_sp order by createTime desc");
			String currentDate = null;
			if(rs.next()){
				currentDate = DateUtil.formatDate(rs.getTimestamp(1), "yyyyMMdd");
			}else{
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -4);
				currentDate = DateUtil.formatDate(c.getTime(), "yyyyMMdd");			
			}
			
			currentDate = nextDay(currentDate,-2);//����������ݽ���ͬ������Ŀǰ��15�ţ���Ҫ��13��,14��,15�Ŷ�ͬ������Ϊ��������48Сʱ�ȶ���
			String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
			while(currentDate.compareTo(today)<=0){
				doSyncDay(currentDate);
				currentDate = nextDay(currentDate,1);			
			}
		} catch (SQLException e) {
			log.error(e);
		} finally {
			DBUtil.release(stmt, con);
		}

		
	}


	
	public void doSyncDay(String currentDay){

		
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
			v_pstmt = v_con.prepareStatement("insert into bpw_mms_sp(neCode,spName,spCode,createTime,AOCount,AOSubmitRate,AOReceiveRate,ATCount,ATSubmitRate) values(?,?,?,?,?,?,?,?,?)");

			
			//����SP�±���ɾ��Ŀǰ��������ݣ�
			String table_from = "mms_sp_"+currentDay;	
			String sql = "delete from bpw_mms_sp where datediff(d,'"+myDay(currentDay)+"',createTime)=0";
			int del = v_stmt.executeUpdate(sql);
			log.info("TaskSp->del"+del);
	
		    p_rs  = p_stmt.executeQuery("select * from "+ table_from);
		

			int i = 0;
			while(p_rs.next()){
				//log.info(syncObj.toLowerCase()+"----"+p_rs.getString(syncObj+"Code")+"----"+CityUtil.getCode(p_rs.getString(syncObj+"Code")+""));
				v_pstmt.setString(1, p_rs.getString("neCode"));
				v_pstmt.setString(2, p_rs.getString("spName"));
				v_pstmt.setString(3, p_rs.getString("spCode"));
				
				v_pstmt.setString(4, myDay(currentDay));
				
				v_pstmt.setString(5, p_rs.getString("AOCount"));
				v_pstmt.setString(6, p_rs.getString("AOSubmitRate"));
				v_pstmt.setString(7, p_rs.getString("AOReceiveRate"));
				v_pstmt.setString(8, p_rs.getString("ATCount"));
				v_pstmt.setString(9, p_rs.getString("ATSubmitRate"));
				v_pstmt.addBatch();
				
				
				if (++i % 500 != 0) {
					continue;
				}		
				int[] a = v_pstmt.executeBatch();
				log.info("TaskSp->a.length"+a.length);
				
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
	

	public static void main(String[] argv){
		new TaskSp().doSync();
		
	}

}
