package pro.vdes.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import common.util.DBUtil;
import common.util.DateUtil;
import common.util.IncrementGenerator;

public class TaskMailBehavior {
	private static Logger log = Logger.getLogger(TaskMailBehavior.class);
	
	/**
	 * 数据库中最近一天的邮箱表
	 * @ahthor DengJianhua
	 * 2010-7-21下午02:45:06
	 * @return
	 */
	private String lastMailTable() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			String sql="select * from ( select table_name from user_tables where table_name like 'MAIL_%' order by table_name desc) where rownum <=1";//oracle
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				return rs.getString(1);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs, stmt, con);
		}
		return null;

	}
	/**
	 * 邮箱数据产生的行为数据
	 * @param currentDate
	 * @return
	 */
	private boolean handle(String currentDate){
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String mailTable="MAIL_"+currentDate;//短信表,如:MAIL_0714
		String tableName = "MBW_BEHAVIOR_"+currentDate;
		try{
			int i = 0;
			final int LEN = 2000;
			
			String sql="select statdate,callerphone,feature,city,brand,terminal,os,kind  from "+mailTable+" where status=0 ";
			
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);

			String insertSql = "insert into "+tableName
				+" (callerPhone,type,statDate,feature,city,brand,terminal,os,kind) " 
				+"values(?,?,?,?,?,?,?,?,?)";//插入行为数据的sql语句
			pstmt = con.prepareStatement(insertSql);
			while(rs.next()){
//					 对数据库连接的优化
				if (con == null || i % LEN == 0) {
					con = DBUtil.getConnection("proxool.mmds");
					pstmt = con.prepareStatement(insertSql);
				}
				++i;
				//行为数据
				pstmt.setLong(1, rs.getLong(2));//callerPhone
				pstmt.setInt(2, 6);//type
				pstmt.setTimestamp(3, rs.getTimestamp(1));//statDate
				pstmt.setString(4,rs.getString(3)); //特征
				pstmt.setString(5, rs.getString(4));//city
				pstmt.setString(6, rs.getString(5)); //brand
				pstmt.setString(7,rs.getString(6));//terminal
				pstmt.setString(8, rs.getString(7));//os
				pstmt.setString(9, rs.getString("kind"));//os
				pstmt.addBatch();
			
				if (i % LEN == 0) {
					log.info("插入了"+i+"行");
					pstmt.executeBatch();
					DBUtil.release(pstmt, con);
				}
			}	//end while
			if (con!=null&&!con.isClosed()) {//剩下的不够2000行的
				pstmt.executeBatch();
				DBUtil.releaseResultSet(rs);
				DBUtil.release(pstmt, con);
			}
			log.info("彩邮箱数据产生的行为数据产生结束");
			return true;
		   
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (con!=null&&!con.isClosed()) {
					stmt.close();
					DBUtil.release(pstmt, con);
					return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}
	/**
	 * 使用被叫与病毒库被叫匹配(病毒的被叫号放在feature字段),如果不存在,就插入病毒库
	 * @param tableName
	 */
	private void matchVirus(String currentDate){
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		PreparedStatement featurePstmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			String tableName="MBW_BEHAVIOR_"+currentDate;
			String sql="select distinct m.feature from "+tableName+" m " +
					"where m.type=6 and m.feature not in(select feature from mbw_wap_filter_feature where feature=m.feature and feature is not null) and m.feature is not null";//某天行为数据中的域名
			rs = stmt.executeQuery(sql);
			String feature=null;
			int i = 0;
			final int LEN = 2000;
			String insertSql="insert into mbw_wap_filter(id,cid,name_en, status, feature,discoverway,statdate,VIRUSISSUETIME) " +
			"values(?,?,?,?,?,?,?,?)";
			pstmt=con.prepareStatement(insertSql);
			
			String featureSql="insert into mbw_wap_filter_feature(filter_id,feature,name_en,statdate) values(?,?,?,?)";
			featurePstmt=con.prepareStatement(featureSql);//病毒特征
			while(rs.next()){
				
				feature=rs.getString(1);//病毒库中没有该病毒,则把该病毒加入病毒特征库,还没完成!!!!!!
				String id = IncrementGenerator.generate();
				if (con == null || i % LEN == 0) {
					con = DBUtil.getConnection("proxool.mmds");
					pstmt = con.prepareStatement(insertSql);
					featurePstmt=con.prepareStatement(featureSql);//病毒特征
				}
				++i;
				pstmt.setString(1,id);
				pstmt.setString(2,id);
				pstmt.setString(3,tableName.substring(13)+":"+feature);
				pstmt.setInt(4,0);
				pstmt.setString(5,feature);
				pstmt.setInt(6,6);//discoverway
				pstmt.setTimestamp(7,java.sql.Timestamp.valueOf(DateUtil.getCurrentDateStr("yyyy-MM-dd HH:mm:ss")));
				pstmt.setTimestamp(8,java.sql.Timestamp.valueOf(DateUtil.getCurrentDateStr("yyyy-MM-dd HH:mm:ss")));
				pstmt.addBatch();
				
				//插入到病毒特征库
				featurePstmt.setString(1,id);
				featurePstmt.setString(2,feature);
				featurePstmt.setString(3,tableName.substring(13)+":"+feature);
				featurePstmt.setTimestamp(4,java.sql.Timestamp.valueOf(DateUtil.getCurrentDateStr("yyyy-MM-dd HH:mm:ss")));
				featurePstmt.addBatch();
				if (i % LEN == 0) {
					log.info("插入了"+i+"行");
					featurePstmt.executeBatch();
					DBUtil.releaseStatement(featurePstmt);
					pstmt.executeBatch();
					DBUtil.release(pstmt, con);
				}
			}
			if (con!=null&&!con.isClosed()) {//剩下的不够2000行的
				featurePstmt.executeBatch();
				DBUtil.releaseStatement(featurePstmt);
				pstmt.executeBatch();
				DBUtil.releaseResultSet(rs);
				DBUtil.releaseStatement(pstmt);
				DBUtil.release(stmt, con);
			}
			log.info("end match virus--->");
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(pstmt, con);
			DBUtil.release(rs, stmt, con);
		}
	}
	

	/**
	 * 外界调用的主要方法；
	 */
	public void doSync() {
		String lastMailTable = this.lastMailTable();//最近一天的行为数据
		String currentDate = lastMailTable.substring(5);

		log.info("当前天:"+currentDate);
		String yesterday=DateUtil.preDay(DateUtil.getCurrentDateStr("yyyyMMdd"), 1,"yyyyMMdd");//yyyyMMdd
		yesterday=yesterday.substring(4);
		if (currentDate.compareTo(yesterday) == 0) {//只处理昨天的
			boolean flag=handle(currentDate);//产生行为数据，并把数据插入到行为数据表
			if(flag){
				matchVirus(currentDate);//根病毒库匹配
			}
		}
	}
	public static void main(String[] args) {
		TaskMailBehavior t=new TaskMailBehavior();
		t.doSync();

	}

}
