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
	 * ���ݿ������һ��������
	 * @ahthor DengJianhua
	 * 2010-7-21����02:45:06
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
	 * �������ݲ�������Ϊ����
	 * @param currentDate
	 * @return
	 */
	private boolean handle(String currentDate){
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String mailTable="MAIL_"+currentDate;//���ű�,��:MAIL_0714
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
				+"values(?,?,?,?,?,?,?,?,?)";//������Ϊ���ݵ�sql���
			pstmt = con.prepareStatement(insertSql);
			while(rs.next()){
//					 �����ݿ����ӵ��Ż�
				if (con == null || i % LEN == 0) {
					con = DBUtil.getConnection("proxool.mmds");
					pstmt = con.prepareStatement(insertSql);
				}
				++i;
				//��Ϊ����
				pstmt.setLong(1, rs.getLong(2));//callerPhone
				pstmt.setInt(2, 6);//type
				pstmt.setTimestamp(3, rs.getTimestamp(1));//statDate
				pstmt.setString(4,rs.getString(3)); //����
				pstmt.setString(5, rs.getString(4));//city
				pstmt.setString(6, rs.getString(5)); //brand
				pstmt.setString(7,rs.getString(6));//terminal
				pstmt.setString(8, rs.getString(7));//os
				pstmt.setString(9, rs.getString("kind"));//os
				pstmt.addBatch();
			
				if (i % LEN == 0) {
					log.info("������"+i+"��");
					pstmt.executeBatch();
					DBUtil.release(pstmt, con);
				}
			}	//end while
			if (con!=null&&!con.isClosed()) {//ʣ�µĲ���2000�е�
				pstmt.executeBatch();
				DBUtil.releaseResultSet(rs);
				DBUtil.release(pstmt, con);
			}
			log.info("���������ݲ�������Ϊ���ݲ�������");
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
	 * ʹ�ñ����벡���ⱻ��ƥ��(�����ı��кŷ���feature�ֶ�),���������,�Ͳ��벡����
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
					"where m.type=6 and m.feature not in(select feature from mbw_wap_filter_feature where feature=m.feature and feature is not null) and m.feature is not null";//ĳ����Ϊ�����е�����
			rs = stmt.executeQuery(sql);
			String feature=null;
			int i = 0;
			final int LEN = 2000;
			String insertSql="insert into mbw_wap_filter(id,cid,name_en, status, feature,discoverway,statdate,VIRUSISSUETIME) " +
			"values(?,?,?,?,?,?,?,?)";
			pstmt=con.prepareStatement(insertSql);
			
			String featureSql="insert into mbw_wap_filter_feature(filter_id,feature,name_en,statdate) values(?,?,?,?)";
			featurePstmt=con.prepareStatement(featureSql);//��������
			while(rs.next()){
				
				feature=rs.getString(1);//��������û�иò���,��Ѹò������벡��������,��û���!!!!!!
				String id = IncrementGenerator.generate();
				if (con == null || i % LEN == 0) {
					con = DBUtil.getConnection("proxool.mmds");
					pstmt = con.prepareStatement(insertSql);
					featurePstmt=con.prepareStatement(featureSql);//��������
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
				
				//���뵽����������
				featurePstmt.setString(1,id);
				featurePstmt.setString(2,feature);
				featurePstmt.setString(3,tableName.substring(13)+":"+feature);
				featurePstmt.setTimestamp(4,java.sql.Timestamp.valueOf(DateUtil.getCurrentDateStr("yyyy-MM-dd HH:mm:ss")));
				featurePstmt.addBatch();
				if (i % LEN == 0) {
					log.info("������"+i+"��");
					featurePstmt.executeBatch();
					DBUtil.releaseStatement(featurePstmt);
					pstmt.executeBatch();
					DBUtil.release(pstmt, con);
				}
			}
			if (con!=null&&!con.isClosed()) {//ʣ�µĲ���2000�е�
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
	 * �����õ���Ҫ������
	 */
	public void doSync() {
		String lastMailTable = this.lastMailTable();//���һ�����Ϊ����
		String currentDate = lastMailTable.substring(5);

		log.info("��ǰ��:"+currentDate);
		String yesterday=DateUtil.preDay(DateUtil.getCurrentDateStr("yyyyMMdd"), 1,"yyyyMMdd");//yyyyMMdd
		yesterday=yesterday.substring(4);
		if (currentDate.compareTo(yesterday) == 0) {//ֻ���������
			boolean flag=handle(currentDate);//������Ϊ���ݣ��������ݲ��뵽��Ϊ���ݱ�
			if(flag){
				matchVirus(currentDate);//��������ƥ��
			}
		}
	}
	public static void main(String[] args) {
		TaskMailBehavior t=new TaskMailBehavior();
		t.doSync();

	}

}
