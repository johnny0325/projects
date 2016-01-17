/**
 * TaskMBWBehavior.java 2010-7-6 ����04:44:02
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import common.db.CommonDao;
import common.util.DBUtil;
import common.util.DateUtil;
import common.util.IncrementGenerator;

/**
 * @author aiyan
 * @version 1.0
 * �������Ϊ��
 * ��һ��������Ϊ���ݵ����ɣ�
 * �ڶ������䲡���⣻
 */
public class TaskMBWBehavior{
	private static Logger log = Logger.getLogger(TaskMBWBehavior.class);
	
	//���㲡��ĳ��Ĳ�ͬ������Ŀ
	public void dostatic(){
		String currentDate = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top 1 statDate  from  MBW_Filter_Num order by statDate desc");
			
			if (rs.next() && rs.getTimestamp(1) != null) {
				currentDate = org.apache.commons.httpclient.util.DateUtil.formatDate(rs.getTimestamp(1),
						"yyyyMMdd");
			} else {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDate = org.apache.commons.httpclient.util.DateUtil.formatDate(c.getTime(), "yyyyMMdd");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.release(rs,stmt, con);
		}
		String today = DateUtil.format(new Date(), "yyyyMMdd");
		while (currentDate.compareTo(today) < 0) {
			setNumInWAP(currentDate);
			currentDate = DateUtil.nextDay(currentDate, 1);
			
		}
	}
	/**
	 * ���ⷽ����������Ϊ���ݵ����ɡ�
	 *
	 */
	public void doSync() {
		String currentDate = getCurrentDate();
		String today = DateUtil.format(new Date(), "yyyyMMdd");
		while (currentDate.compareTo(today) < 0) {
			handle(currentDate);
			addWAPFilter(currentDate);
			currentDate = DateUtil.nextDay(currentDate, 1);
			
		}
	}
	/**
	 * ��ѯ��Ҫ�����쿪ʼ������Ϊ���ݡ�
	 * @return
	 */
	private String getCurrentDate(){
		String currentDate = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			 con = DBUtil.getConnection("proxool.vdes");
			 stmt = con.createStatement();
			 rs = stmt.executeQuery("select top 1 statDate  from  MBW_BEHAVIOR order by statDate desc");
			
			if (rs.next() && rs.getTimestamp(1) != null) {
				currentDate = org.apache.commons.httpclient.util.DateUtil.formatDate(rs.getTimestamp(1),
						"yyyyMMdd");
			} else {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDate = org.apache.commons.httpclient.util.DateUtil.formatDate(c.getTime(), "yyyyMMdd");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.release(rs,stmt, con);
		}
		
		return currentDate;
	}

	
	public void handle(String date) {//date :yyyyMMdd��ʽ
		    log.info("��ʼ����"+date+"����Ϊ����");
			delete(date);
			insertSMS(date);
			insertWAP(date);
			log.info("��������"+date+"����Ϊ����");
	}
	
	/**
	 * ɾ���������Ϊ����
	 * @param con
	 * @param date
	 * @throws SQLException 
	 */
	private void delete(String date){
		log.info("delete");
		Connection con = null;
		Statement stmt = null;
		try{
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String deleteSql = "delete from MBW_BEHAVIOR where datediff(d,statDate,'"+DateUtil.myDay(date)+"')=0";
			stmt.executeUpdate(deleteSql);	
		}catch(SQLException e){
			log.info(e);
		}catch(Exception e){}
		finally{
			DBUtil.release(stmt,con);
		}

	}
	
	/**
	 * ������ŵ���Ϊ����
	 * @param con
	 * @param date
	 * @throws SQLException 
	 */
	
	private void insertSMS(String date){
		log.info("insertSMS");
		Connection con = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {//typeDate ����Ϊһ��Ķ������ݺ�ǰ�����WAP���ݹ����������Ϊ���ݡ�
			 //statDate ��˵�����������Ϊ���ݡ���  date ��20100625 ������� ����typeDate ֻ����2010-06-25 �� WAP��typeDate ��������ǰ������ġ�
			 //  statDate ֻ����2010-06-25 �������������Ϊ���ݡ� 
			
			con = DBUtil.getConnection("proxool.vdes");
			pstmt = con.prepareStatement("insert into MBW_BEHAVIOR (typeDate,callerPhone,feature,[key],city,type,statDate)" +
					"values( '"+DateUtil.myDay(date)+"',?,?,?,?,1,'"+DateUtil.myDay(date)+"')");//type = 1 �������ݣ� type = 2 WAP��Ϊ����
			String sql = 
				" select a.callerPhone,b.calledPhone,b.[key],b.city "+
				" from dbo.MBW_COMPLEX a left join sms_"+date.substring(4)+" b on a.callerPhone = b.callerPhone"+
				" where datediff(d,a.statDate,'"+DateUtil.myDay(date)+"')=0 group by a.callerPhone,b.calledPhone,b.[key],b.city order by a.callerPhone,b.calledPhone";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				pstmt.setString(1, rs.getString("callerPhone"));
				pstmt.setString(2, rs.getString("calledPhone"));
				pstmt.setString(3, rs.getString("key"));
				pstmt.setString(4, rs.getString("city"));
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBUtil.releaseStatement(pstmt);
			DBUtil.release(rs,stmt,con);
		}
	}
	/**
	 * ����wap����Ϊ����
	 * @param con
	 * @param date
	 * @throws SQLException 
	 */
	private void insertWAP(String date){
		log.info("insertWAP");
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String start = DateUtil.nextDay(date, -2);
			while (start.compareTo(date) <= 0) {//�ۺϷ����е����к��뵽WAP������ǰ3����Ѱ�Ҳ���������������Ϊ���ݡ�
				String sql = "insert into MBW_BEHAVIOR(typeDate,callerPhone,feature,[type],statDate,domain) "+
				"select '"+DateUtil.myDay(start)+"',a.callerPhone,b.url2,2,'"+DateUtil.myDay(date)+"',b.feature "+
				"from dbo.MBW_COMPLEX a  left join bill_"+start.substring(4)+" b on a.callerPhone = b.callerPhone "+
				"where b.url2 IS NOT NULL and  datediff(d,a.statDate,'"+DateUtil.myDay(date)+"')=0 "+
				"group by a.callerPhone,b.url2,b.feature";
				stmt.execute(sql);
				start = DateUtil.nextDay(start, 1);
			}
			
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBUtil.release(stmt,con);
		}
	}

	/**
	 * �����裺�鿴��URL����Ӧ�������ڲ��������Ƿ���ڣ������ھͽ��벡������
	 * @param date
	 */
	private void addWAPFilter(String date){
		log.info("addWAPFilter");
		Connection con = null;
		Statement stmt = null;
		
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String sql ="select distinct(feature)"+
			" from mbw_behavior where datediff(d,statDate,'"+DateUtil.myDay(date)+"')=0" +
			" and [type]=2 ";
			ResultSet rs = stmt.executeQuery(sql);
			String feature = null;
			while(rs.next()){
				feature = getFeature(rs.getString(1));
				if(!isExist(feature)){
					insert(date, feature);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.release(stmt,con);
		}


	}
	/**
	 * @param feature
	 */
	private void insert(String date,String feature) {
		log.info(feature);
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		
		try {//name_cn name_en status feature statDate
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String id = IncrementGenerator.generate();
			String sql ="insert into MBW_WAP_FILTER(id,cid,name_en, status, feature, statDate) " +
					"values('"+id+"','"+id+"','"+date.substring(4)+":"+feature+"','0','"+feature+"','"+DateUtil.myDay(date)+"')" ;
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.release(stmt,con);
		}
	}
	/**
	 * �鿴�������������MBW_WAP_FILTER��wap�����⣩�д��ڣ��Ƿ���ڡ�
	 * @param feature
	 * @return
	 */
	private boolean isExist(String feature) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String sql ="select 1 from MBW_WAP_FILTER where feature = '"+feature+"'";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.release(stmt,con);
		}

		return false;
	}
	/**
	 * mtk2.wapdfw.com �͵õ� wapdfw.com ��Ϊ����
	 * @param url
	 * @return
	 */
	private String getFeature(String url){
		String feature = null;
		String regEx ="(\\w+:)?(([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.)?(com.cn|" +
				"com|cn|net|org)(:\\d*)?";
		Pattern p=Pattern.compile(regEx);
		Matcher m=p.matcher(url);
		if(m.find()){
/*			for(int i =0;i<m.groupCount();i++){
				System.out.println(i+"--"+m.group(i));
			}*/
			feature = m.group(2)+m.group(4);
		}else{
			regEx ="((\\d+)\\.(\\d+)\\.(\\d+))\\.(\\d+)";
			p=Pattern.compile(regEx);
			m=p.matcher(url);
			if(m.find()){
				feature = m.group(1);
			}else{
				feature = url;//������ƥ�䶼��ͨ��ʱ���ͽ�ԭ��URLֱ�ӷ�����Ϊ����������
			}
			
		}
		return feature;
		
	}
	
	/**
	 * ��WAP����Ϊ�����趨�¼�ID
	 */
	private void setFilterIdInWAP(String date){

		 Connection con = null;
			Statement stmt = null;
			
			try {
				con = DBUtil.getConnection("proxool.vdes");
				stmt = con.createStatement();
				String sql = "update MBW_BEHAVIOR"+
				" set filterid = (select id from MBW_WAP_FILTER  where MBW_BEHAVIOR.feature like '%'+MBW_WAP_FILTER.feature+'%')" +
				" where [type]=2 and datediff(d,statDate,'"+DateUtil.myDay(date)+"')=0";
				stmt.execute(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DBUtil.release(stmt,con);
			}

		
	}
	/**
	 * ������������ͬ���е���Ŀ��
	 *
	 */
	private void setNumInWAP(String date){

		CommonDao dao = null;
			try {
				
				dao = new CommonDao();
				String sql = "select id from MBW_WAP_FILTER ";
				List<Map> list = dao.query(sql, null);
				for(Map map:list){
					updateNumByFilterId(date,map.get("id")+"");
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					dao.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	/**
	 *��ͨ����Щ���������ҵ���ͬ�����к��������
	 * @param filterId
	 */
	private void updateNumByFilterId(String date,String filterId){
		log.info(date+"--"+filterId);
		// TODO Auto-generated method stub
		 Connection con = null;
			Statement stmt = null;
			List<String> features = getFeatures(filterId);
			
/*			String likes = "url2 like ";
			for(int i = 0;i<features.size();i++){
				if(i==0){
					likes= likes + "'%"+features.get(i).trim()+"%'";
				}else {
					likes = likes + "' or url2 like '%"+features.get(i).trim()+"%'";
				}
				
			}*/
			try {
				con = DBUtil.getConnection("proxool.vdes");
				stmt = con.createStatement();
				stmt.execute("delete from MBW_Filter_Num where filterId='"+filterId+"' and datediff(d,statDate,'"+DateUtil.myDay(date)+"')=0");
				String sql = "insert into  MBW_Filter_Num "+
/*					"select '"+filterId+"','"+DateUtil.myDay(date)+"', count(1) from ("+
					"select distinct(callerPhone) from bill_"+date.substring(4)+" where "+likes +") a";*/
				
				 "select  '"+filterId+"','"+DateUtil.myDay(date)+"', count(distinct(callerPhone)) from bill_"+date.substring(4)+"  where feature in("+
						" select feature from dbo.MBW_WAP_FILTER where id = '"+filterId+"')";
				
				stmt.execute(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DBUtil.release(stmt,con);
			}
		
		
	}
	
	/**
	 *  ���ݲ���ID�õ������Ķ�������������һ������ID�����ж����������
	 * @param filterId
	 * @return
	 */
	private List<String> getFeatures(String filterId){
		// TODO Auto-generated method stub
		 Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			List<String> features = new ArrayList<String>();
			try {
				con = DBUtil.getConnection("proxool.vdes");
				stmt = con.createStatement();
				String sql = "select distinct(feature) from MBW_WAP_FILTER where id = '"+filterId+"'";
				rs = stmt.executeQuery(sql);
				while(rs.next()){
					features.add(rs.getString(1));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				DBUtil.release(stmt,con);
			}
			return features;
		
		
	}
	
	
	public static void main(String[] argv) {
		TaskMBWBehavior tt = new TaskMBWBehavior();
		tt.doSync();
		//String url = "http://lyrics.m-tunes.com.cn";
		//url = "picdown.ttpod.cn:9010";
		//String url = "211.100.45.44";
		//System.out.println(tt.getFeature(url));
	}
}
