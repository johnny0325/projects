/**
 * DBUtil.java 2009-4-19 下午10:52:30
 */
package common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class DBUtil {
	static {
		try {
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
			JAXPConfigurator.configure(DBUtil.class.getResource(
					"/config/proxool.xml").getFile(), false);
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Connection getConnection(String pool) {
		Connection con = null;
		try {
			con = DriverManager.getConnection(pool);
		} catch (SQLException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return con;
	}
	
	public static void releaseConnection(Connection con) {
		// TODO 自动生成方法存根
		try {
			if (con != null) {
				con.close();

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static void releaseResultSet(ResultSet rs) {
		// TODO 自动生成方法存根
		try {
			if (rs != null) {
				rs.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	public static void releaseStatement(Statement stmt) {
		// TODO 自动生成方法存根
		try {
			if (stmt != null) {
				stmt.close();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static void release(Statement stmt,Connection con) {
		// TODO 自动生成方法存根
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				con.close();
			}			

		} catch (Exception e) {
			e.printStackTrace();

		}
	}	

	public static void release(ResultSet rs,Statement stmt,Connection con) {
		// TODO 自动生成方法存根
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				con.close();
			}			

		} catch (Exception e) {
			e.printStackTrace();

		}
	}




}
