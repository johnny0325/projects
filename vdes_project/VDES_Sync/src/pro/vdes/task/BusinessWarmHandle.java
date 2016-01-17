/**
 * BusinessWarmHandle.java 2009-12-2 ����11:47:10
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class BusinessWarmHandle {
	private static Logger log = Logger.getLogger(BusinessWarmHandle.class);
	/*
	 * ������л�Ծ�澯��Ŀ
	 * �����㷨�ǣ��������bpw_neCode_range�����һ��Сʱ�ĸ澯��Ŀ������������в��ܰ���DNS�ģ�����BUSINESSID!=-100
	 */
	public void  handle(){
		Connection con = null;
		Statement stmt = null;	
		ResultSet rs = null;	
		PreparedStatement pstmt = null;
		
		String activeAndDaynum= getActiveAndDayNum();
		
		int index = activeAndDaynum.indexOf("|");
		if(index==-1) return;
		
		con = DBUtil.getConnection("proxool.vdes");
		try {
			stmt = con.createStatement();
			//type 1:��Ԫ�澯 2��ҵ��澯 3������澯
			rs = stmt.executeQuery("select * from vdes_warm where type=2 ");
			boolean exist = false;
			if(rs.next()) exist = true;
			if(exist){
				pstmt = con.prepareStatement("update vdes_warm set num_active =?,num_day=?,statDate='"+selectMaxAlarmDate(con)+"' where type=2");
				pstmt.setInt(1, Integer.parseInt(activeAndDaynum.substring(0,index)));
				pstmt.setInt(2, Integer.parseInt(activeAndDaynum.substring(index+1)));
				pstmt.addBatch();
				pstmt.executeBatch();
			}else{
				pstmt = con.prepareStatement("insert into vdes_warm(type,level,num_active,num_day,statDate)values(2,2,?,?,'"+selectMaxAlarmDate(con)+"')");
				pstmt.setInt(1, Integer.parseInt(activeAndDaynum.substring(0,index)));
				pstmt.setInt(2, Integer.parseInt(activeAndDaynum.substring(index+1)));
				pstmt.addBatch();
				pstmt.executeBatch();
			}
			insertContent(con);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch(Exception e){
			e.printStackTrace();
			log.error(e);
			
		}
	}
	/*
	 * ���������澯��Ŀ
	 * �����㷨�ǣ��ڱ���bpw_neCode_range,������ͬ��Ԫ��ҵ��ʱ��ĸ�����
	 */
	public String getActiveAndDayNum(){
		// TODO Auto-generated method stub
		int x =0;
		int y =0;
		int z =0;
		Connection con = null;
		Statement stmt = null;	
		ResultSet rs = null;	
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			//x
			String sql = "select count(*) from bpw_neCode_range where  businessId!=-100 and datediff(d,createDate,getdate())=0 and datepart(hh,getdate())=0 and datepart(mi,getdate())=0 and datepart(ss,getdate())=0";
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				x = rs.getInt(1);
			}
			if(rs!=null) rs.close();
			
			//y
			sql = "select count(*) from bpw_neCode_range where  businessId!=-100 and datediff(d,createDate,getdate())=0 and (datepart(hh,getdate())!=0 or datepart(mi,getdate() )!=0 or datepart(ss,getdate())!=0) group by neCode,statDate";
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				y =  rs.getInt(1);
			}
			if(rs!=null) rs.close();
			
			//z
			sql = "select count(*) from bpw_neCode_range where  datediff(hh,createDate,(select max(createDate) from  dbo.bpw_neCode_range where businessId!=-100))=0";
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				z =  rs.getInt(1);
			}
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		return (x+z)+"|"+(x+y);

	}
	
	/*
	 * ����澯���ݡ�
	 */
	private void insertContent(Connection conn)
	{
		String deleteSql="delete from vdes_warncontent where type=2";
		String insertHourSql="insert into vdes_warncontent(type,level,msg,statDate) (select 2,2,chineseDesc,statDate from bpw_neCode_range where  businessId!=-100 and datediff(d,createDate,getdate())=0 and datepart(hh,getdate())=0 and datepart(mi,getdate())=0 and datepart(ss,getdate())=0)";
		String insertDaySql="insert into vdes_warncontent(type,level,msg,statDate) (select 2,2,chineseDesc,statDate from bpw_neCode_range where  datediff(hh,createDate,(select max(createDate) from  dbo.bpw_neCode_range where businessId!=-100))=0)";
		
		PreparedStatement stat = null;	
		try
		{
			
		  
		   stat=conn.prepareStatement(deleteSql);
		   stat.executeUpdate();
		   stat.close();
		   stat=conn.prepareStatement(insertHourSql);
		   stat.executeUpdate();
		   stat.close();
		   stat=conn.prepareStatement(insertDaySql);
		   stat.executeUpdate();
		}
		catch(Exception e)
		{
			log.error(e);
		}
		finally {
			DBUtil.releaseStatement(stat);
		}
		
		
	}
	
	/*
	 * ��ѯ���¸澯����
	 */
	private String selectMaxAlarmDate(Connection conn)
	{
		String selectSql="select max(statDate) from bpw_neCode_range where businessId!=100";
		PreparedStatement stat = null;	
		ResultSet rs=null;
		String maxAlarmDate="";
		try
		{
			
		  
		   stat=conn.prepareStatement(selectSql);
		   rs=stat.executeQuery();
		   if(rs.next())
		   {
			   maxAlarmDate=rs.getString(1);
		   }
		}
		catch(Exception e)
		{
			log.error(e);
		}
		finally {
			DBUtil.releaseResultSet(rs);
			DBUtil.releaseStatement(stat);
			
			return maxAlarmDate;
		}
	}

	public static void main(String[] argv){
		new BusinessWarmHandle().handle();
	}
}
