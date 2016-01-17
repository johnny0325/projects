/**
 * AcitiveWarmHandle.java 2009-12-2 上午11:40:03
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import pro.vdes.bean.Range;
import pro.vdes.bean.WarnContent;

import common.util.DBUtil;
import common.util.DateUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class AcitiveWarmHandle {
	private static Logger log = Logger.getLogger(AcitiveWarmHandle.class);

	/**
	 * 要把活跃告警中的四个告警级别的数量记录下来，到表VDES_WARM中。
	 */
	public void handle() {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;	
		ResultSet rs = null;	
		PreparedStatement pstmt = null;
		List<WarnContent> warnContentList = new ArrayList<WarnContent>();//告警明细列表
		warnContentList.clear();
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from BPW_WarmActive");
			//level1,level2,level3,levle4分别表示提醒、次要、严重、紧急
			int level1 = 0,level2=0,level3=0,level4=0,level=0;;
			String content = "";
			
			String regex1 = "\\w*?(FW|SW|RT).*";
			Pattern pattern1 = Pattern.compile(regex1);
			String regex2 = "\\w*?sucrate\\slow\\s(\\d\\.?\\d*).*";
			Pattern pattern2 = Pattern.compile(regex2);
			String regex3 = "\\w*?GW.*";
			Pattern pattern3 = Pattern.compile(regex3);
			Matcher matcher = null;
			while (rs.next()) {
				level=0;
				matcher = pattern1.matcher(content);
				content= rs.getString("content");
				WarnContent warnContent=new WarnContent();			
				warnContent.setMsg(content);
				warnContent.setType(1);
				warnContent.setStatDate(rs.getString("createTime"));
				if(matcher.find()) {
					++level4;
					level=4;
					warnContent.setLevel(level);
					warnContentList.add(warnContent);
					continue;
				}
				matcher = pattern2.matcher(content);
				if(matcher.find()){
					double low = Double.parseDouble(matcher.group(1)+"");				
					if(low<=5)
					{
						++level4;
						level=4;
					}
					else
					{
						++level3;
						level=3;
					}
					warnContent.setLevel(level);
					warnContentList.add(warnContent);
					continue;
				}
				matcher = pattern3.matcher(content);
				if(matcher.find()){
					++level2;
					level=2;
				}
				if(level==0)//假如没有判断
				{
					++level1;
					level=1;
				}
				
				warnContent.setLevel(level);
				warnContentList.add(warnContent);
				
			}
			log.info("level1-------"+level1);
			log.info("level2-------"+level2);
			log.info("level3-------"+level3);
			log.info("level4-------"+level4);
			
			if(rs!=null) rs.close();
			
			//type 1:网元告警 2：业务告警 3：拨测告警
			rs = stmt.executeQuery("select * from vdes_warm where type=1 ");
			boolean exist = false;
			if(rs.next()) exist = true;
			if(exist){
				pstmt = con.prepareStatement("update vdes_warm set num_active =?,statDate='"+selectMaxAlarmDate(con)+"' where type=1 and level=?");
				pstmt.setInt(1, level1);
				pstmt.setInt(2, 1);
				pstmt.addBatch();
				pstmt.setInt(1, level2);
				pstmt.setInt(2, 2);
				pstmt.addBatch();
				pstmt.setInt(1, level3);
				pstmt.setInt(2, 3);
				pstmt.addBatch();
				pstmt.setInt(1, level4);
				pstmt.setInt(2, 4);
				pstmt.addBatch();
				pstmt.executeBatch();
			}else{
				pstmt = con.prepareStatement("insert into vdes_warm(type,level,num_active,statDate)values(1,?,?,'"+selectMaxAlarmDate(con)+"')");
				pstmt.setInt(1, 1);
				pstmt.setInt(2, level1);
				pstmt.addBatch();
				pstmt.setInt(1, 2);
				pstmt.setInt(2, level2);
				pstmt.addBatch();
				pstmt.setInt(1, 3);
				pstmt.setInt(2, level3);
				pstmt.addBatch();
				pstmt.setInt(1, 4);
				pstmt.setInt(2, level4);
				pstmt.addBatch();
				pstmt.executeBatch();
			}
			insertContent(con,warnContentList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}

	}
	
	/*
	 * 插入告警内容。
	 */
	private void insertContent(Connection conn,List<WarnContent> warnContentList)
	{
		String deleteSql="delete from vdes_warncontent where type=1";
		String insertSql="insert into vdes_warncontent(type,level,msg,statDate) values(?,?,?,?)";
		
		PreparedStatement stat = null;	
		try
		{
			
		  
		   stat=conn.prepareStatement(deleteSql);
		   stat.executeUpdate();
		   stat.close();
		   stat=conn.prepareStatement(insertSql);
		   for (WarnContent warnContent : warnContentList)
		   {
			   stat.setInt(1, warnContent.getType());
			   stat.setInt(2, warnContent.getLevel());
			   stat.setString(3, warnContent.getMsg());
			   stat.setString(4, warnContent.getStatDate());
			   stat.addBatch();
		   }
		  
		   stat.executeBatch();
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
	 * 查询最新告警日期
	 */
	private String selectMaxAlarmDate(Connection conn)
	{
		String selectSql="select max(createTime) from BPW_WarmActive";
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
		new AcitiveWarmHandle().handle();
	}

}
