/**
 * TaskWapTps.java 2010-3-26 ÏÂÎç04:40:30
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskWapTpsReport {
	
	public void doSync(){
		String table = " test_bpw_wap_tps_month ";
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			con.setAutoCommit(false);
			stmt = con.createStatement();
			String delete_sql = "delete from  "+table;
			stmt.executeUpdate(delete_sql);
			List<Map> list = getData();
			if(list==null||list.size()==0){
				return;
			}
			String insert_sql = "insert into "+table+"(statDate,neCode,maxTps,avgTps,avgCount,rate ) values(?,?,?,?,?,?)";
			pstmt = con.prepareStatement(insert_sql);
			for(Map map:list){

				pstmt.setString(1, map.get("statDate")+"");
				pstmt.setString(2, map.get("neCode")+"");
				pstmt.setDouble(3, Double.valueOf(map.get("maxTps")+""));
				pstmt.setDouble(4, Double.valueOf(map.get("avgTps")+""));
				pstmt.setDouble(5, Double.valueOf(map.get("avgCount")+""));
				pstmt.setDouble(6, Double.valueOf(map.get("rate")+""));
				pstmt.addBatch();


			}
			pstmt.executeBatch();
			con.commit();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			DBUtil.release(stmt, con);
		}
		
	}
	
	public List<Map>  getData(){
		List list = new ArrayList();
		Connection con = null;
		PreparedStatement pstmt = null;
	    String sql = "select * from dbo.v_WAP_Month  order by statDate, neCode";
		try {
			con = DBUtil.getConnection("proxool.partner");
			pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map map = new HashMap();
				map.put("neCode",rs.getString("neCode"));
				map.put("statDate", rs.getDate("statDate"));
				map.put("maxTps",rs.getDouble("maxTps"));
				map.put("avgTps",rs.getDouble("avgTps"));
				map.put("avgCount",rs.getDouble("avgCount"));
				map.put("rate",rs.getDouble("rate"));
				list.add(map);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}finally {
			DBUtil.release(pstmt, con);

		}
		return list.size()==0?null:list;
	}
	public static void main(String[] argv){
		new TaskWapTpsReport().doSync();
	}

}
