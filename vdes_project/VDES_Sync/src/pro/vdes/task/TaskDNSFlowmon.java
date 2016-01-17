/**
 * Task_Interface.java 2009-10-26 ����05:16:01
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import common.util.DBUtil;
import common.util.DateUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskDNSFlowmon {
	private static Logger log = Logger.getLogger(TaskSp.class);
	//������31�յ�����ɾ������
	public void deleteBeforDateInMonth(){
		System.out.print("TaskDNSFlowmon->deleteBeforDateInMonth");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) statDate from mtw_dns_flowmon order by statDate desc");
			if(rs.next()){
				//��nextDateTime�ں��������в�����һ��Сʱ������
				String sql = "delete from  mtw_dns_flowmon where datediff(d,statDate,'"+DateUtil.format(rs.getTimestamp(1), "yyyy-MM-dd")+"')>31";// ����31�������ɾ��;
				int i = stmt.executeUpdate(sql);
				log.info("TaskDNSFlowmon->deleteBeforDateInMonth  | ɾ�� "+i+" ��");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		
	}

	public void doSync(){
		System.out.print("syncDNSFlowmonData");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) statDate from mtw_dns_flowmon order by statDate desc");
			Date currentDate = null;
			if(rs.next()){
				currentDate = DateUtil.parseDate(DateUtil.format(rs.getTimestamp(1),"yyyy-MM-dd"));
			}else{
				Calendar c = Calendar.getInstance();
				currentDate = DateUtil.addDays(c.getTime(),-3);	
				currentDate = DateUtil.parseDate(DateUtil.format(currentDate, "yyyy-MM-dd"));			
			}
			Date today =DateUtil.parseDate(DateUtil.format(new Date(),"yyyy-MM-dd"));
			while(currentDate.before(today)||currentDate.equals(today)){
				doSyncDay(currentDate);
				currentDate = DateUtil.addDays(currentDate,1);		
			}
		} catch (SQLException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt,con);
		}

		
	}
	
	//ϵͳ��Сʱ������Ը�DNS���µ�CPU,�ڴ棬�ɹ���ѯ��ָ����м�أ�������ҵ��澯�� bpw_neCode_range��businessId=0��
	public void generateWarm(){
		
		System.out.print("generate DNSFlowmon Warm");
		Connection con = null;
		Statement stmt = null;
		PreparedStatement v_pstmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			v_pstmt = con.prepareStatement("insert into bpw_neCode_range(businessId,neCode,statDate,count_today,range,look_range,createDate,rangeField,rangeFieldName,chineseDesc) values(?,?,?,?,?,?,getdate(),?,?,?)");
			
			ResultSet rs = stmt.executeQuery("select top (1) statDate from bpw_neCode_range where businessId=-100 order by statDate desc");
			Date currentDate = null;
			if(rs.next()){
				currentDate = rs.getTimestamp(1);
			}else{
				Calendar c = Calendar.getInstance();
				currentDate = DateUtil.addDays(c.getTime(),-3);			
			}
			
			rs.close();
			
			rs = stmt.executeQuery("select * from mtw_dns_flowmon d where datediff(ss,d.statDate,'"+DateUtil.format(currentDate,DateUtil.C_TIME_PATTON_DEFAULT)+"')<0");
			while(rs.next()){
				//businessId,neCode,statDate,count_today,range,look_range,rangeField,rangeFieldName
				if(rs.getInt("cpu")>70){
				v_pstmt.setInt(1, -100);//-100��ʾmtw_dns_flowmon�ĸ澯 1����ʾ���ţ�2����ʾwap
				v_pstmt.setString(2, rs.getString("dns"));
				v_pstmt.setString(3, rs.getString("statDate"));
				v_pstmt.setString(4, rs.getString("cpu"));
				v_pstmt.setString(5, rs.getString("cpu"));
				v_pstmt.setString(6, "70");
				v_pstmt.setString(7, "cpu");
				v_pstmt.setString(8, "cpu");
				
				
				String chineseDesc =  rs.getString("dns")+"��"+DateUtil.getDateStr(rs.getString("statDate"),DateUtil.C_TIME_PATTON_DEFAULT)+"��cpuΪ"+rs.getString("cpu")+",������ֵ70";
				v_pstmt.setString(9, chineseDesc);
				v_pstmt.addBatch();
				
			}
				
				if(rs.getInt("mem")>70){
					v_pstmt.setInt(1, -100);//-100��ʾmtw_dns_flowmon�ĸ澯 1����ʾ���ţ�2����ʾwap
					v_pstmt.setString(2, rs.getString("dns"));
					v_pstmt.setString(3, rs.getString("statDate"));
					v_pstmt.setString(4, rs.getString("mem"));
					v_pstmt.setString(5, rs.getString("mem"));
					v_pstmt.setString(6, "70");
					v_pstmt.setString(7, "mem");
					v_pstmt.setString(8, "mem");
					
					String chineseDesc =  rs.getString("dns")+"��"+DateUtil.getDateStr(rs.getString("statDate"),DateUtil.C_TIME_PATTON_DEFAULT)+"���ڴ�Ϊ"+rs.getString("mem")+",������ֵ70";
					v_pstmt.setString(9, chineseDesc);
					v_pstmt.addBatch();
					
				}
				
				
				if(rs.getInt("succSpeed")<80){
					v_pstmt.setInt(1, -100);//-100��ʾmtw_dns_flowmon�ĸ澯 1����ʾ���ţ�2����ʾwap
					v_pstmt.setString(2, rs.getString("dns"));
					v_pstmt.setString(3, rs.getString("statDate"));
					v_pstmt.setString(4, rs.getString("succSpeed"));
					v_pstmt.setString(5, rs.getString("succSpeed"));
					v_pstmt.setString(6, "80");
					v_pstmt.setString(7, "succSpeed");
					v_pstmt.setString(8, "succSpeed");
					String chineseDesc =  rs.getString("dns")+"��"+DateUtil.getDateStr(rs.getString("statDate"),DateUtil.C_TIME_PATTON_DEFAULT)+"��succSpeedΪ"+rs.getString("succSpeed")+",���ڷ�ֵ80";
					v_pstmt.setString(9, chineseDesc);
					v_pstmt.addBatch();
					
				}


		} 
			int[] b = v_pstmt.executeBatch();
			
			//�ر����ݿ����
			rs.close();
			stmt.close();
			v_pstmt.close();
			
		}catch (SQLException e) {
			log.error(e);
		}catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.releaseConnection(con);
		}

		
	}


	
	public void doSyncDay(Date currentDay){

		
		Connection p_con = null;
		Connection v_con = null;
		try {
			p_con = DBUtil.getConnection("proxool.mmsc");
			v_con = DBUtil.getConnection("proxool.vdes");
			Statement v_stmt = null;
			PreparedStatement v_pstmt = null;
			
			Statement p_stmt = null;
			ResultSet p_rs = null;


			v_con.setAutoCommit(false);
			
			p_stmt = p_con.createStatement();
			v_stmt = v_con.createStatement();
			v_pstmt = v_con.prepareStatement("insert into mtw_dns_flowmon(dns,statDate,cpu,mem,succSpeed,totalSpeed) values(?,?,?,?,?,?)");

			
			String sql = "delete from mtw_dns_flowmon where datediff(d,'"+DateUtil.format(currentDay,"yyyy-MM-dd")+"',statDate)=0";
			int del = v_stmt.executeUpdate(sql);
			log.info("TaskDNSFlowmon->del"+del);

			
			 p_rs  = p_stmt.executeQuery("select * from dnsflowmon where date_format(t,'%Y-%m-%d')='"+DateUtil.format(currentDay,"yyyy-MM-dd")+"'");


			 while(p_rs.next()){
					v_pstmt.setString(1, p_rs.getString("dns"));
					v_pstmt.setString(2, p_rs.getString("t"));
					v_pstmt.setInt(3, 100-p_rs.getInt("cpu"));
					v_pstmt.setString(4, p_rs.getString("mem"));
					v_pstmt.setString(5, p_rs.getString("sucspeed"));
					v_pstmt.setInt(6, p_rs.getInt("sucspeed")+p_rs.getInt("failspeed")+1);
					v_pstmt.addBatch();
				}
			int[] b = v_pstmt.executeBatch();
			
			v_con.commit();
			log.info(b.length);
			//�ر����ݿ����
			p_rs.close();
			p_stmt.close();
			v_pstmt.close();
			

		} catch (SQLException e) {
			try {			
				v_con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			log.error(e);
		} catch (Exception e) {
			try {			
				v_con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			log.error(e);
		} finally {
			try {
				v_con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBUtil.releaseConnection(p_con);
			DBUtil.releaseConnection(v_con);
		}
		
	}
	

	public static void main(String[] argv){
		//new TaskDNSFlowmon().doSync();
		
		//new TaskDNSFlowmon().deleteBeforDateInMonth();
		
		new TaskDNSFlowmon().generateWarm();
		
	}

}
