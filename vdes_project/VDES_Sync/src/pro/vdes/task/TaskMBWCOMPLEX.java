/**
 * TaskTXT.java 2010-5-26 ����04:44:02
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import common.util.DBHelp;
import common.util.DBUtil;
import common.util.DateUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskMBWCOMPLEX{
	private static Logger log = Logger.getLogger(TaskMBWCOMPLEX.class);
	/**
	 * �����õ���Ҫ������
	 * ������Ҫ���������ǣ��쳣���ŵ�ĳһ�����ʱ���ǰ������쳣wap�������ϲ�ѯ���õ������ط������ֵ����к���ͼ�¼������������
	 *
	 */
	public void doSync() {
		String currentDate = getCurrentDate();
		String today = DateUtil.format(new Date(), "yyyyMMdd");
		while (currentDate.compareTo(today) <= 0) {
			handle(currentDate);
			currentDate = DateUtil.nextDay(currentDate, 1);
		}
	}


	private String getCurrentDate(){
		String currentDate = null;
		try {
			Connection con = DBUtil.getConnection("proxool.vdes");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select top (1) statDate from MBW_COMPLEX order by statDate desc");
			if (rs.next() && rs.getTimestamp(1) != null) {
				currentDate = DateUtil.getDateStr(rs.getString(1),"yyyy-MM-dd");
				currentDate = currentDate.replaceAll("-", "");
			} else {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDate = DateUtil.format(c.getTime(), "yyyyMMdd");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return currentDate;
	}
/*	--------------------------------------------------


	select  

	callerPhone,ms,(select count(1) from sms_0625 where callerPhone = a.callerPhone) as smsNum,
	(select sum(requestNum) from mbw_wap_ms where ms = b.ms and
	datediff(d,statDate,'2010-06-22')<0 and
	datediff(d,statDate,'2010-06-25')>=0
	) as wapNum
	from 
	(select distinct callerPhone from sms_0625) a 

	inner join
	(select distinct ms from mbw_wap_ms where 
	datediff(d,statDate,'2010-06-22')<0 and
	datediff(d,statDate,'2010-06-25')>=0


	) b on a.callerPhone = b.ms

	order by smsNum desc
	
	--------------------------------------------------*/
	public void handle(String date) {//date :yyyyMMdd��ʽ
		Connection con = null;
		String ee = DateUtil.myDay(date);
		String ee1 = DateUtil.myDay(DateUtil.nextDay(date, -3));
		try {
			con = DBUtil.getConnection("proxool.vdes");
			Statement stmt = con.createStatement();
			java.sql.PreparedStatement pstmt = con.prepareStatement("insert into MBW_COMPLEX(statDate,smsNum,wapNum,callerPhone,city)values(?,?,?,?,?)");
			ResultSet rs = null;
			// �ٿ�ʼ����
			con.setAutoCommit(false);
			//�ڶ���������MBW_COMPLEX�еġ�ee��������;
			String deleteSql = "delete from MBW_COMPLEX where datediff(d,statDate,'"+ee+"')=0";
			stmt.executeUpdate(deleteSql);
			//������������MBW_COMPLEX�еġ�ee��������;
			String sql = 
				" select"+  

				" (select count(1) from sms_"+date.substring(4)+" where callerPhone = a.callerPhone) as smsNum,"+
				" (select sum(requestNum) from mbw_wap_ms where ms = b.ms and"+
				" datediff(d,statDate,'"+ee1+"')<0 and"+
				" datediff(d,statDate,'"+ee+"')>=0"+
				" ) as wapNum,callerPhone,city"+
				" from "+
				" (select distinct (callerPhone),city from sms_"+date.substring(4)+") a"+ 

				" inner join"+
				" (select distinct ms from mbw_wap_ms where "+
				" datediff(d,statDate,'"+ee1+"')<0 and"+
				" datediff(d,statDate,'"+ee+"')>=0"+


				" ) b   on    cast(a.callerPhone as nchar(50))= b.ms  and len(callerPhone)=13"+

				" order by smsNum desc";
				
				rs = stmt.executeQuery(sql);
			while(rs.next()){
				pstmt.setString(1, ee);
				pstmt.setString(2, rs.getString(1));
				pstmt.setString(3, rs.getString(2));
				pstmt.setString(4, rs.getString(3));
				pstmt.setString(5, rs.getString(4));
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			con.commit();


		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.releaseConnection(con);
		}
	}
	
	
	public static void main(String[] argv) {
		TaskMBWCOMPLEX tt = new TaskMBWCOMPLEX();
		tt.doSync();
	}
}
