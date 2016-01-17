/**
 * DayWarmHandle.java 2009-12-2 上午11:40:48
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
public class DayWarmHandle {

	private static Logger log = Logger.getLogger(DayWarmHandle.class);

	/**
	 * 要把活跃告警中的四个告警级别的数量记录下来，到表VDES_WARM中。
	 */
	public void handle() {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;	
		ResultSet rs = null;	
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from BPW_WarmDay");
			//level1,level2,level3,levle4分别表示提醒、次要、严重、紧急
			int level1 = 0,level2=0,level3=0,level4=0;
			String content = "";
			
			String regex1 = "\\w*?(FW|SW|RT).*";
			Pattern pattern1 = Pattern.compile(regex1);
			String regex2 = "\\w*?sucrate\\slow\\s(\\d\\.?\\d*).*";
			Pattern pattern2 = Pattern.compile(regex2);
			String regex3 = "\\w*?GW.*";
			Pattern pattern3 = Pattern.compile(regex3);
			Matcher matcher = null;
			while (rs.next()) {
				matcher = pattern1.matcher(content);
				content= rs.getString("content");
				if(matcher.find()) {
					++level4;
					continue;
				}
				matcher = pattern2.matcher(content);
				if(matcher.find()){
					double low = Double.parseDouble(matcher.group(1)+"");				
					if(low<=5)
						++level4;
					else
						++level3;
					
					continue;
				}
				matcher = pattern3.matcher(content);
				if(matcher.find()){
					++level2;
				}
				++level1;
			}
			if(rs!=null) rs.close();
			
			//type 1:网元告警 2：业务告警 3：拨测告警
			rs = stmt.executeQuery("select * from vdes_warm where type=1 ");
			boolean exist = false;
			if(rs.next()) exist = true;
			if(exist){
				pstmt = con.prepareStatement("update vdes_warm set num_day =?,statDate=getdate() where type=1 and level=?");
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
				pstmt = con.prepareStatement("insert into vdes_warm(type,level,num_day,statDate)values(1,?,?,getdate())");
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
	}
	public static void main(String[] argv){
		new DayWarmHandle().handle();
	}

}
