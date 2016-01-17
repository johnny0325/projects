package com.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JdbcTest {

	/**
	 * JdbcTest.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-5-17 ����10:18:12
	 */
	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		//oracle�����������������URL
		String driverClassForOracle = "oracle.jdbc.driver.OracleDriver";
		String urlForOracleString = "jdbc:oracle:thin:@localhost:1521:mydb";
		//mysql�����������������URL
		String driverClassForMySQL = "com.mysql.jdbc.Driver";
		String UrlForMySQL = "jdbc:mysql://localhost:3306/mydb";
		
		try{
			//������������
			Class.forName(driverClassForMySQL);
			conn = DriverManager.getConnection(UrlForMySQL, "user", "password");
			String sql = "select * from student";
			pst = conn.prepareStatement(sql);
			
			rs = pst.executeQuery(sql);
			while(rs.next()) {
				System.out.println(rs.getString(1));
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs != null) {
					rs.close();
				}
				
				if(pst != null) {
					pst.close();
				}
				
				if(!conn.isClosed()) {
					conn.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
