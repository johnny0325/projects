/**
 * TaskCommon.java 2009-7-23 下午04:07:41
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

import common.db.CommonDao;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskCommon {
	private static Logger log = Logger.getLogger(TaskCommon.class);
	protected String table = null;
	
	protected int exists = 0; 
	
	protected int delete = 0;
	
	
	public void doSync(){
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select top (1) statDate from "+table+" order by statDate desc");
			String currentDate = null;
			if(rs.next()){
				currentDate = DateUtil.formatDate(rs.getTimestamp(1), "yyyyMMdd");
			}else{
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDate = DateUtil.formatDate(c.getTime(), "yyyyMMdd");			
			}
			currentDate = nextDay(currentDate,delete*-1);//如果对三天的数据进行同步；如目前是15号，则要对13号,14号,15号都同步；
			String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
			while(currentDate.compareTo(today)<0){//不处理今天的,因为http://211.139.136.118:8080/HTML/mmskpi/wap/home/lizf/script/ilocate/history/ilocate.wap0920006.20090916
				                                  //没有今天的数据,对今天操作容易引起java.lang.OutOfMemoryError: Java heap space
				                                  //add by aiyan 2009-09-16
				doSyncBeforeDay(currentDate);
				currentDate = nextDay(currentDate,1);
				
				
			}
		} catch (SQLException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		}finally {
			DBUtil.release(stmt, con);
		}

		
	}
	
	public String nextDay(String currentDate,int next){
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0,4));
		int m = Integer.parseInt(currentDate.substring(4,6));
		int d = Integer.parseInt(currentDate.substring(6,8));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m-1);
		c.set(Calendar.DATE, d+next);
		
		
		return DateUtil.formatDate(c.getTime(), "yyyyMMdd");
		
	}
	public String myDay(String currentDate){
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0,4));
		int m = Integer.parseInt(currentDate.substring(4,6));
		int d = Integer.parseInt(currentDate.substring(6,8));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m-1);
		c.set(Calendar.DATE, d);
		
		
		return DateUtil.formatDate(c.getTime(), "yyyy-MM-dd");
		
	}
	protected void doSyncBeforeDay(String currentDay){}
	
	protected void deleteCurrentData(String currentDay){
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			String sql = "delete from  " + table + " where datediff(d,'"
					+ myDay(currentDay) + "',statDate)=0";
			dao.execute(sql, null);
		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
		
	}
	
	
	public void deleteData(){
		log.info("deleteData");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
				String sql = "delete from  "+table+" where datediff(d,statdate,getdate())<"+delete;
				int i = stmt.executeUpdate(sql);
				log.info("deleteDate  | 删除 "+i+" 条");
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
	
	public void existsData(){
		
		log.info("existsData");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) statDate from "+table+"  order by statDate desc");
			if(rs.next()){
				String sql = "delete from  "+table+" where datediff(d,statdate,'"+DateUtil.formatDate(rs.getTimestamp(1), "yyyy-MM-dd")+"')>"+exists;
				int i = stmt.executeUpdate(sql);
				log.info("existsData  | 删除 "+i+" 条");
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

	/**
	 * @return the exists
	 */
	protected int getExists() {
		return exists;
	}

	/**
	 * @param exists the exists to set
	 */
	protected void setExists(int exists) {
		this.exists = exists;
	}

	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}
	
	
	

}
