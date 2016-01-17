/**
 * NewDataHandle.java 2009-12-2 上午11:49:55
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class NewDataHandle {
	private static Logger log = Logger.getLogger(NewDataHandle.class);

	Map dateMap = new HashMap();

	public void handle() {
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;

		Map<String, Map> activeAndDaynum = getActiveAndDayNum();
		Map<String, Integer> activeNumMap = activeAndDaynum.get("active");
		Map<String, Integer> dayNumMap = activeAndDaynum.get("day");

		con = DBUtil.getConnection("proxool.vdes");
		try {
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt.executeUpdate("delete from vdes_warm where type=4");
			pstmt = con
					.prepareStatement("insert into vdes_warm(type,level,num_active,num_day,statDate)values(4,?,?,?,'"
							+ selectMaxAlarmDate(con) + "')");
			// level1
			pstmt.setInt(1, 1);
			pstmt.setInt(2, activeNumMap.get("level1"));
			pstmt.setInt(3, dayNumMap.get("level1"));
			pstmt.addBatch();
			// level2
			pstmt.setInt(1, 2);
			pstmt.setInt(2, activeNumMap.get("level2"));
			pstmt.setInt(3, dayNumMap.get("level2"));
			pstmt.addBatch();
			// level3
			pstmt.setInt(1, 3);
			pstmt.setInt(2, activeNumMap.get("level3"));
			pstmt.setInt(3, dayNumMap.get("level3"));
			pstmt.addBatch();
			// level4
			pstmt.setInt(1, 4);
			pstmt.setInt(2, activeNumMap.get("level4"));
			pstmt.setInt(3, dayNumMap.get("level4"));
			pstmt.addBatch();
			pstmt.executeBatch();
			insertContent(con);// 插入明细表
			con.commit();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);

		} finally {
			DBUtil.releaseStatement(stmt);
			DBUtil.release(pstmt, con);
		}
	}

	public Map<String, Map> getActiveAndDayNum() {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Map activeNumMap = new HashMap();

		activeNumMap.put("level1", 0);
		activeNumMap.put("level2", 0);
		activeNumMap.put("level3", 0);
		activeNumMap.put("level4", 0);

		Map dayNumMap = new HashMap();

		dayNumMap.put("level1", 0);
		dayNumMap.put("level2", 0);
		dayNumMap.put("level3", 0);
		dayNumMap.put("level4", 0);

		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			String sql = "select level,count(*) as num from mtw_business_warm where datediff(mi,createDate,(select max(createDate) from mtw_business_warm))<=3 group by level";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt("level") == 1) {
					activeNumMap.put("level1", rs.getInt("num"));
				} else if (rs.getInt("level") == 2) {
					activeNumMap.put("level2", rs.getInt("num"));
				} else if (rs.getInt("level") == 3) {
					activeNumMap.put("level3", rs.getInt("num"));
				} else if (rs.getInt("level") == 4) {
					activeNumMap.put("level4", rs.getInt("num"));
				}
			}

			if (rs != null)
				rs.close();

			String otherCityDate = " select max(statDate),location  from mtw_business where location not in "
					+ " (select city from mtw_business_warm "
					+ " where datediff(mi,createDate,(select max(createDate) from mtw_business_warm))<=3)"
					+ " and location !='SD'" + " group by location ";
			rs = stmt.executeQuery(otherCityDate);
			dateMap.clear();
			while (rs.next()) {
				String city = rs.getString("location");
				String maxDate = rs.getString(1);
				dateMap.put(city, maxDate);
			}
			if (rs != null)
				rs.close();

			
			Iterator iter = dateMap.keySet().iterator();
			while (iter.hasNext()) {
				String city=(String) iter.next();
				System.out.println(city);
				System.out.println(dateMap.get(city));
				String otherCityAlarm = "select count(*),level from mtw_business_warm where statDate='"+dateMap.get(city).toString()+"' and city='"+city+"' group by level ";
				rs = stmt.executeQuery(otherCityAlarm);
				
				while (rs.next()) {
					int level = rs.getInt("level");
					int levelNum = rs.getInt(1);

					if (level == 1) {
						activeNumMap.put("level1", (Integer) activeNumMap
								.get("level1")
								+ levelNum);
					} else if (level == 2) {
						activeNumMap.put("level2", (Integer) activeNumMap
								.get("level2")
								+ levelNum);
					} else if (level == 3) {
						activeNumMap.put("level3", (Integer) activeNumMap
								.get("level3")
								+ levelNum);
					} else if (level == 4) {
						activeNumMap.put("level4", (Integer) activeNumMap
								.get("level4")
								+ levelNum);
					}
				}

				if (rs != null)
					rs.close();
			}
			// add by windson end

			sql = "select level,count(*) as num from mtw_business_warm where datediff(d,createDate,(select max(createDate) from mtw_business_warm))=0 group by level";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt("level") == 1) {
					dayNumMap.put("level1", rs.getInt("num"));
				} else if (rs.getInt("level") == 2) {
					dayNumMap.put("level2", rs.getInt("num"));
				} else if (rs.getInt("level") == 3) {
					dayNumMap.put("level3", rs.getInt("num"));
				} else if (rs.getInt("level") == 4) {
					dayNumMap.put("level4", rs.getInt("num"));
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			DBUtil.release(rs, stmt, con);
		}
		Map<String, Map> ret = new HashMap<String, Map>();
		ret.put("active", activeNumMap);
		ret.put("day", dayNumMap);
		return ret;

	} /*
	 * 插入告警内容。
	 */

	private void insertContent(Connection conn) {
		String deleteSql = "delete from vdes_warncontent where type=4";
		String insertSql = "insert into vdes_warncontent(type,level,msg,statDate) (select 4,level,msg,createDate from mtw_business_warm where datediff(mi,createDate,(select max(createDate) from mtw_business_warm))<=3)";
		// String insertSql=
		// "insert into vdes_warncontent(type,level,msg,statDate) (select 4,level,msg,statDate from mtw_business_warm whereinsert into vdes_warncontent(type,level,msg,statDate) (select 4,level,msg,statDate from mtw_business_warm where datediff(mi,createDate,(select max(createDate) from mtw_business_warm))<=3))"
		PreparedStatement stat = null;
		try {

			stat = conn.prepareStatement(deleteSql);
			stat.executeUpdate();
			stat.close();
			stat = conn.prepareStatement(insertSql);
			stat.executeUpdate();
			stat.close();

			String insertOtherSql = "insert into vdes_warncontent(type,level,msg,statDate) (select 4,level,msg,createDate from mtw_business_warm where city=? and statDate =?)";
			stat = conn.prepareStatement(insertOtherSql);
			Iterator iter = dateMap.keySet().iterator();
			while (iter.hasNext()) {
				String city=(String) iter.next();
				log.info("-------------------"+city);
				stat.setString(1, city);
				log.info("-------------------"+dateMap.get(city));
				stat.setString(2, (String) dateMap.get(city));

				stat.addBatch();

			}

			stat.executeBatch();

		} catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.releaseStatement(stat);
		}

	}

	/*
	 * 查询最新告警日期
	 */
	private String selectMaxAlarmDate(Connection conn) {
		String selectSql = "select max(statDate) from mtw_business_warm";
		PreparedStatement stat = null;
		ResultSet rs = null;
		String maxAlarmDate = "";
		try {

			stat = conn.prepareStatement(selectSql);
			rs = stat.executeQuery();
			if (rs.next()) {
				maxAlarmDate = rs.getString(1);
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.releaseResultSet(rs);
			DBUtil.releaseStatement(stat);

			return maxAlarmDate;
		}
	}

	public static void main(String[] argv) {
		new NewDataHandle().handle();
	}
}
