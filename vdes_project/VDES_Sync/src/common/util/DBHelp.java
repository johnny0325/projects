/**
 * DBHelp.java 2009-4-24 ÏÂÎç04:02:56
 */
package common.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class DBHelp {
	public static boolean isExistRecord(String sql){
		Connection con = DBUtil.getConnection("proxool.vdes");
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs =  stmt.executeQuery(sql);
			return rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.release(rs,stmt, con);
		}
		
		return false;
	}

	/**
	 * @param string
	 * @return
	 */
	public static Date getMaxDate(String sql) {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection("proxool.vdes");
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs =  stmt.executeQuery(sql);
			if(rs.next()) return rs.getDate(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.release(rs,stmt, con);
		}
		
		return null;
	}



}
