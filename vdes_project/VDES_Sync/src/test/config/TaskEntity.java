/**
 * Entity.java 2009-7-7 ����05:13:43
 */
package test.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

import sample.xmlbean.RootDocument.Root.Table.Field;

import common.db.CommonDao;
import common.util.DBUtil;

import config.DocParse;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskEntity {
	private static Logger log = Logger.getLogger(TaskEntity.class);
    private DocParse docParse = null;
    public TaskEntity(){
    	
    }
/*	public TaskEntity(String file){
		docParse = new DocParse(file);
	}*/
	
	public void createTable(){
		log.info("enter..create table..");
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			String sql = docParse.createSql();
			dao.execute(sql, null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally{
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	public void deleteData(){
//		
//		System.out.print("deleteData");
//		Connection con = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		try {
//			con = DBUtil.getConnection("proxool.vdes");
//			stmt = con.createStatement();
//			rs = stmt.executeQuery("select top (1) statDate from "+docParse.getTableName()+"  order by statDate desc");
//			if(rs.next()){
//				short delete = docParse.getTime().getDelete();
//				String sql = "delete from  "+docParse.getTableName()+" where datediff(d,statdate,'"+DateUtil.formatDate(rs.getTimestamp(1), "yyyy-MM-dd")+"')<"+delete;
//				int i = stmt.executeUpdate(sql);
//				log.info("deleteBeforDateInMonth  | ɾ�� "+i+" ��");
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			DBUtil.release(rs,stmt, con);
//		}
//		
//		
//		
//		
//	}
	public void doSync(){
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection(docParse.getDesDB());
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select top (1) statDate from "+docParse.getTableName()+" order by statDate desc");
			String currentDate = null;
			if(rs.next()){
				currentDate = DateUtil.formatDate(rs.getTimestamp(1), "yyyyMMdd");
			}else{
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDate = DateUtil.formatDate(c.getTime(), "yyyyMMdd");			
			}
			short delete = docParse.getTime().getDelete();
			currentDate = nextDay(currentDate,delete*-1);//����������ݽ���ͬ������Ŀǰ��15�ţ���Ҫ��13��,14��,15�Ŷ�ͬ������Ϊ��������48Сʱ�ȶ���
			String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
			while(currentDate.compareTo(today)<=0){
				doSyncBeforeDay(currentDate);
				currentDate = nextDay(currentDate,1);
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
			p_con = DBUtil.getConnection(docParse.getSrcDB());
			v_con = DBUtil.getConnection(docParse.getDesDB());
			Statement v_stmt = v_con.createStatement();
			Statement p_stmt = p_con.createStatement();
			PreparedStatement v_pstmt = v_con.prepareStatement(docParse.insertSql());
			
			//�����ܱ���ɾ��Ŀǰ��������ݣ�
			String sql = "delete from  "+docParse.getTableName()+" where datediff(d,'"+myDay(currentDay)+"',statDate)=0";
			int del = v_stmt.executeUpdate(sql);
			log.info("del��"+del);
			
			
			
			sql = docParse.selectSql();
			
			//sql�к���yyyyMMdd����yyyy-MM-dd��Ҫ�����⴦��;
			if(sql.indexOf("yyyyMMdd")!=-1){
				sql = sql.replace("yyyyMMdd", currentDay);
			}
			
			int myDayIndex = -1;
			if(sql.indexOf("yyyy-MM-dd")!=-1){
				String str = sql.substring(0,sql.indexOf("yyyy-MM-dd"));
				myDayIndex = str.split(",").length+1;
				
				sql = sql.replace("yyyy-MM-dd", "null");
				


			}
			//���⴦�������

			log.info("sql:"+sql);
			ResultSet p_rs  = p_stmt.executeQuery(sql);
			int i = 0;
			
			Field[] fields = docParse.getFields();
			int index = 0;
			while(p_rs.next()){
				
				for(Field f:fields){
					index++;
					if(index==myDayIndex){
						v_pstmt.setString(4, myDay(currentDay));
					}else{
						v_pstmt.setString(index, p_rs.getString(f.getStringValue()));
					}
					 
				}
				index = 0;
				v_pstmt.addBatch();
				
				
				if (++i % 500 != 0) {
					continue;
				}		
				int[] ae = v_pstmt.executeBatch();
				log.info("ae.length"+ae.length);
				
			}
			// ����д���
			int[] b = v_pstmt.executeBatch();
			log.info(b.length);
			//�ر����ݿ����
			p_rs.close();
			p_stmt.close();
			v_pstmt.close();
			

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.releaseConnection(p_con);
			DBUtil.releaseConnection(v_con);
		}
		
	}
	
	public void doSyncWeek2Month(){	
		Connection con = DBUtil.getConnection(docParse.getDesDB());
		try {
			//PreparedStatement pstmt = con.prepareStatement("insert into bpw_mms_"+syncObj+"_month("+syncObj+"Code,statDate,indicator,status,count) values(?,?,?,?,?)");
			
			PreparedStatement pstmt = con.prepareStatement(docParse.insertSql());
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select top (1) statDate from "+docParse.getTableName()+" order by statDate desc");
			Date currnetDateTime = null;
			Calendar c = Calendar.getInstance();
			short delete = docParse.getTime().getDelete();
			if(rs.next()){
				currnetDateTime = rs.getTimestamp(1);								
				c.setTime(currnetDateTime);
				c.add(Calendar.DATE, delete*-1);//����������ݽ���ͬ������Ŀǰ��15�ţ���Ҫ��13��,14��,15�Ŷ�ͬ������Ϊ��������48Сʱ�ȶ���
				currnetDateTime = c.getTime();	
			}else{
				c.add(Calendar.DATE, -7);
				currnetDateTime = c.getTime();			
			}
			
			//�����±���ɾ��Ŀǰ��Щ������ݣ�
			String sql = "delete from "+docParse.getTableName()+" where datediff(d,'"+DateUtil.formatDate(currnetDateTime, "yyyy-MM-dd")+"',statDate)>0";
			log.info("month-del-sql"+sql);
			int del = stmt.executeUpdate(sql);
			log.info("doSyncWeek2Month->del"+del);

			Field[] fields = docParse.getFields();
			StringBuffer sb = new StringBuffer();
			for(Field f:fields){
				sb.append(",").append(f.getStringValue());
			}
			 
			
			ResultSet rs2 = null;
			sql = "select "+sb.toString().substring(1)+" from ("+ docParse.selectSql()+") a  where datediff(d,'"+DateUtil.formatDate(currnetDateTime, "yyyy-MM-dd")+"',statDate)>0";
			rs2  = stmt.executeQuery(sql);
			

			
			int index = 0;
			while(rs2.next()){				
				for(Field f:fields){
						pstmt.setString(++index, rs2.getString(f.getStringValue()));
				}
				index = 0;
				pstmt.addBatch();
			}
			int[] a = pstmt.executeBatch();
			log.info("a.length"+a.length);
			
			rs.close();
			rs2.close();
			stmt.close();
			pstmt.close();


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.releaseConnection(con);
		}

		
	}

}
