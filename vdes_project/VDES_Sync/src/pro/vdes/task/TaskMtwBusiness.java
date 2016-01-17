/**
 * Task_Interface.java 2009-10-26 下午05:16:01
 */
package pro.vdes.task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import common.util.CityUtil;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.DateUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskMtwBusiness {
	private static Logger log = Logger.getLogger(TaskMtwBusiness.class);
	private final static String[] city = new String[] { "gz", "sz", "dg", "fs",
			"st", "zh", "hz", "zs", "jm", "sg", "mz", "sw", "yj", "zj", "mm",
			"zq", "qy", "cz", "jy", "yf", "hy" };

	// private final static String[] city= new String[]{"st"};

	// 将大于2日的数据删除掉；由于数据量大，我想先保持2天的数据了，add by aiyan 2009-12-01
	public void deleteBeforDate() {
		System.out.print("TaskMtwBusiness->deleteBeforDate");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt
					.executeQuery("select top (1) statDate from mtw_business order by statDate desc");
			if (rs.next()) {
				// 以nextDateTime在合作方库中查找下一个小时的数据
				String sql = "delete from  mtw_business where datediff(d,statDate,'"
						+ DateUtil.format(rs.getTimestamp(1), "yyyy-MM-dd")
						+ "')>2";// 大于2天的数据删除;
				int i = stmt.executeUpdate(sql);
				log.info("TaskMtwBusiness->deleteBeforDate  | 删除 " + i + " 条");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs, stmt, con);
		}

	}

	
	// 将大于2日的数据删除掉；由于数据量大，我想先保持2天的数据了，add by aiyan 2009-12-01
	public void deleteBeforDateWarm() {
		System.out.print("TaskMtwBusiness->deleteBeforDateWarm");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt
					.executeQuery("select top (1) createDate from mtw_business_warm order by createDate desc");
			if (rs.next()) {
				// 以nextDateTime在合作方库中查找下一个小时的数据
				String sql = "delete from  mtw_business_warm where datediff(d,createDate,'"
						+ DateUtil.format(rs.getTimestamp(1), "yyyy-MM-dd")
						+ "')>2";// 大于2天的数据删除;
				int i = stmt.executeUpdate(sql);
				log.info("TaskMtwBusiness->deleteBeforDateWarm  | 删除 " + i + " 条");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs, stmt, con);
		}

	}
	public void doSync() {
		System.out.print("syncMtwBusinessData");
		Connection con = null;

		Statement stmt = null;
		ResultSet rs = null;

		PreparedStatement pstmt = null;

		try {
			con = DBUtil.getConnection("proxool.vdes");
			// 需要用事务，这里是先删除某小时的某分钟数据，然后把文件中大于等于这个分钟的数据进入数据库中。add by aiyan
			// 2009-12-01
			con.setAutoCommit(false);

			stmt = con.createStatement();
			rs = stmt.executeQuery("select max(statdate) from mtw_business");
			String statDate = "";
			if (rs.next() && rs.getTimestamp(1) != null)
				statDate = DateUtil.format(rs.getTimestamp(1),
						"yyyy-MM-dd HH:mm");

			stmt.executeUpdate("delete from mtw_business where statDate >='"
					+ statDate + "'");

			String sql = "insert into mtw_business(msc,bsc,statDate,MMS_SEND_SUC_RATE,MMS_GET_SUC,MMS_EE_DELAY,WAP_HOME_SUC_RATE,WAP_DL_SUC,WAP_HOME_V_TIME,WAP_DL_RATE,location) values(?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			String destUrl = new ConfigureUtil().getValue("mtwBusinessURL");
			URL url = new URL(destUrl);
			HttpURLConnection httpUrl = (HttpURLConnection) url
					.openConnection();
			// 连接指定的网络资源
			httpUrl.connect();
			// 获取网络输入流
			BufferedInputStream bis = new BufferedInputStream(httpUrl
					.getInputStream());
			InputStreamReader reader = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			String[] tmp = null;
			while ((line = br.readLine()) != null) {
				tmp = line.split("#");
				if (tmp.length > 54
						&& tmp[0].replace("_", " ").compareTo(statDate) >= 0) {
					pstmt.setString(1, tmp[3].trim());
					pstmt.setString(2, tmp[4].trim());
					pstmt.setString(3, tmp[0].replace("_", " ").trim());
					pstmt.setString(4, tmp[43].trim());
					pstmt.setString(5, tmp[45].trim());

					pstmt.setString(6, tmp[54].trim());
					pstmt.setString(7, tmp[37].trim());
					pstmt.setString(8, tmp[41].trim());
					pstmt.setString(9, tmp[39].trim());
					pstmt.setString(10, tmp[42].trim());
					pstmt.setString(11, tmp[2].trim());
					pstmt.addBatch();

				}

			}
			pstmt.executeBatch();
			con.commit();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.releaseResultSet(rs);
			DBUtil.releaseStatement(stmt);
			DBUtil.release(pstmt, con);
		}

	}

	public void generateWarm() {
		System.out.print("generate-MtwBusinessData-Warn");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");

			for (String c : city) {

				stmt = con.createStatement();
				rs = stmt
						.executeQuery("select distinct(msc) from mtw_business where location = '"
								+ c + "'");

				Set<String> mscSet = new HashSet<String>();
				while (rs.next()) {
					mscSet.add(rs.getString(1));
				}
				if (rs != null)
					rs.close();

				rs = stmt
						.executeQuery("select MMS_SEND_SUC_RATE,MMS_GET_SUC,WAP_HOME_SUC_RATE,WAP_DL_SUC,WAP_HOME_V_TIME,WAP_DL_RATE, msc,statDate from mtw_business where location = '"
								+ c + "'  order by statDate desc");

				int total = mscSet.size();

				Map<String, Integer> numMap = new HashMap<String, Integer>();
				numMap.put("MMS_SEND_SUC_RATE", 0);
				numMap.put("MMS_GET_SUC", 0);
				numMap.put("WAP_HOME_SUC_RATE", 0);
				numMap.put("WAP_DL_SUC", 0);
				numMap.put("WAP_HOME_V_TIME", 0);
				numMap.put("WAP_DL_RATE", 0);
				
				Map<String, Date> statDateMap = new HashMap<String, Date>();
				statDateMap.put("MMS_SEND_SUC_RATE", null);
				statDateMap.put("MMS_GET_SUC", null);
				statDateMap.put("WAP_HOME_SUC_RATE", null);
				statDateMap.put("WAP_DL_SUC", null);
				statDateMap.put("WAP_HOME_V_TIME", null);
				statDateMap.put("WAP_DL_RATE", null);

				List<String> list5 = new ArrayList<String>();
				List<String> list6 = new ArrayList<String>();
				int num1 = 0, num2 = 0, num3 = 0, num4 = 0, num5 = 0, num6 = 0;
				while (rs.next() && !mscSet.isEmpty()) {
					if (!mscSet.contains(rs.getString("msc")))
						continue;

					if (rs.getInt(1) == 0) {
						numMap.put("MMS_SEND_SUC_RATE", ++num1);
						if (statDateMap.get("MMS_SEND_SUC_RATE") == null)
							statDateMap.put("MMS_SEND_SUC_RATE", rs
									.getTimestamp("statDate"));

					}
					if (rs.getInt(2) == 0) {
						numMap.put("MMS_GET_SUC", ++num2);
						if (statDateMap.get("MMS_GET_SUC") == null)
							statDateMap.put("MMS_GET_SUC", rs
									.getTimestamp("statDate"));

					}
					if (rs.getInt(3) == 0) {
						numMap.put("WAP_HOME_SUC_RATE", ++num3);
						if (statDateMap.get("WAP_HOME_SUC_RATE") == null)
							statDateMap.put("WAP_HOME_SUC_RATE", rs
									.getTimestamp("statDate"));
					}
					if (rs.getInt(4) == 0) {
						numMap.put("WAP_DL_SUC", ++num4);
						if (statDateMap.get("WAP_DL_SUC") == null)
							statDateMap.put("WAP_DL_SUC", rs
									.getTimestamp("statDate"));
					}
					if (rs.getDouble(5) > 7.5) {
						numMap.put("WAP_HOME_V_TIME", ++num5);
						if (statDateMap.get("WAP_HOME_V_TIME") == null)
							statDateMap.put("WAP_HOME_V_TIME", rs
									.getTimestamp("statDate"));

						// list5.add(rs.getString("statDate") + "|"
						// + rs.getString("WAP_HOME_V_TIME"));
					}
					if (rs.getDouble(6) < 1) {
						numMap.put("WAP_DL_RATE", ++num6);
						if (statDateMap.get("WAP_DL_RATE") == null)
							statDateMap.put("WAP_DL_RATE", rs
									.getTimestamp("statDate"));

						// list6.add(rs.getString("statDate") + "|"
						// + rs.getString("WAP_DL_RATE"));
					}
//					System.out.println(rs.getString("msc") + "--num1--" + num1
//							+ "--num2--" + num2 + "--num3--" + num3
//							+ "--num4--" + num4);
					mscSet.remove(rs.getString("msc"));
				}
				if (rs != null)
					rs.close();

				// add by windson 判断告警唯一性
				String checkSql = "select distinct msg from mtw_business_warm where datediff(d,createDate,getDate())=0";
				pstmt = con.prepareStatement(checkSql);
				ArrayList msgList = new ArrayList();
				rs = pstmt.executeQuery();
				msgList.clear();
				while (rs.next()) {
					msgList.add(rs.getString("msg"));
				}
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				// end add by windson

				String sql = "insert into mtw_business_warm(statDate,city,num,level,msg,target,createDate)values(?,?,?,?,?,?,getdate())";
				pstmt = con.prepareStatement(sql);

				String msg = "";
				int tmp = 0;
				int level = 1;
				// 第一个指标
				String key = "MMS_SEND_SUC_RATE";
				tmp = numMap.get(key);
				level = getLevel(total,tmp);
				if (tmp != 0) {
					msg = statDateMap.get(key) + "，" + CityUtil.getName(c)
							+ "，彩信发送成功率拨测" + total + "次，失败" + tmp + "次，"
							+ getColor(level) + "告警。";
					if (msgList.size() == 0 || !msgList.contains(msg)) {// 判断是否重复告警
																		// add
																		// by
																		// windson
						pstmt.setString(1, statDateMap.get(key) + "");
						pstmt.setString(2, c);
						pstmt.setInt(3, tmp);
						pstmt.setInt(4, level);
						pstmt.setString(5, msg);
						pstmt.setString(6, key);
						pstmt.addBatch();
					}
				}
				// 第二个指标
				key = "MMS_GET_SUC";
				tmp = numMap.get(key);
				level = getLevel(total,tmp);
				if (tmp != 0) {
					msg = statDateMap.get(key) + "，" + CityUtil.getName(c)
							+ "，彩信提取成功率拨测" + total + "次，失败" + tmp + "次，"
							+ getColor(level) + "告警。";
					if (msgList.size() == 0 || !msgList.contains(msg)) {// 判断是否重复告警

						pstmt.setString(1, statDateMap.get(key) + "");
						pstmt.setString(2, c);
						pstmt.setInt(3, tmp);
						pstmt.setInt(4, level);
						pstmt.setString(5, msg);
						pstmt.setString(6, key);
						pstmt.addBatch();
					}
				}
				// 第三个指标
				key = "WAP_HOME_SUC_RATE";
				tmp = numMap.get(key);
				level = getLevel(total,tmp);
				if (tmp != 0) {
					msg = statDateMap.get(key) + "，" + CityUtil.getName(c)
							+ "，WAP主页访问成功率拨测" + total + "次，失败" + tmp + "次，"
							+ getColor(level) + "告警。";
					if (msgList.size() == 0 || !msgList.contains(msg)) {// 判断是否重复告警
						pstmt.setString(1, statDateMap.get(key) + "");
						pstmt.setString(2, c);
						pstmt.setInt(3, tmp);
						pstmt.setInt(4, level);
						pstmt.setString(5, msg);
						pstmt.setString(6, key);
						pstmt.addBatch();
					}
				}
				// 第四个指标
				key = "WAP_DL_SUC";
				tmp = numMap.get(key);
				level = getLevel(total,tmp);
				if (tmp != 0) {
					msg = statDateMap.get(key) + "，" + CityUtil.getName(c)
							+ "，WAP下载成功率拨测" + total + "次，失败" + tmp + "次，"
							+ getColor(level) + "告警。";
					if (msgList.size() == 0 || !msgList.contains(msg)) {// 判断是否重复告警

						pstmt.setString(1, statDateMap.get(key) + "");
						pstmt.setString(2, c);
						pstmt.setInt(3, tmp);
						pstmt.setInt(4, level);
						pstmt.setString(5, msg);
						pstmt.setString(6, key);
						pstmt.addBatch();
					}
				}

				// 第五个指标
				key = "WAP_HOME_V_TIME";
				tmp = numMap.get(key);
				level = getLevel(total,tmp);
				if (tmp != 0) {
					msg = statDateMap.get(key) + "，" + CityUtil.getName(c)
							+ "，WAP主页访问时间拨测" + total + "次，超过阀值7.5s " + tmp + "次，"
							+ getColor(level) + "告警。";
					if (msgList.size() == 0 || !msgList.contains(msg)) {// 判断是否重复告警
					// pstmt.setString(1, s.substring(0, index));
						pstmt.setString(1, statDateMap.get(key) + "");
						pstmt.setString(2, c);
						pstmt.setInt(3, tmp);
						pstmt.setInt(4, level);;// 告警级别 1:提醒，2：次要，3：严重，4：紧急 add by
						// aiyan 2009-12-2
						pstmt.setString(5, msg);
						pstmt.setString(6, key);
						pstmt.addBatch();
					}
				}
								// }
				// 第六个指标
				key = "WAP_DL_RATE";
				tmp = numMap.get(key);
				level = getLevel(total,tmp);
				if (tmp != 0) {
					msg = statDateMap.get(key) + "，" + CityUtil.getName(c)
							+ "，WAP下载速率拨测" + total + "次，低于阀值1kb/s " + tmp + "次，"
							+ getColor(level) + "告警。";
					if (msgList.size() == 0 || !msgList.contains(msg)) {// 判断是否重复告警
					// pstmt.setString(1, s.substring(0, index));
						pstmt.setString(1, statDateMap.get(key) + "");
						pstmt.setString(2, c);
						pstmt.setInt(3, tmp);
						pstmt.setInt(4, level);// 告警级别 1:提醒，2：次要，3：严重，4：紧急 add by
						// aiyan 2009-12-2
						pstmt.setString(5, msg);
						pstmt.setString(6, key);
						pstmt.addBatch();
					}
				}
				// }
				pstmt.executeBatch();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.releaseResultSet(rs);
			DBUtil.releaseStatement(stmt);
			DBUtil.releaseConnection(con);
		}

	}

	public String getColor(int num) {
		if (num == 1) {
			return "蓝色";
		} else if (num == 2) {
			return "黄色";
		} else if (num == 3) {
			return "橙色";
		} else {
			return "红色";
		}

	}
	
	//total:总的拨测点；fail:失败点；返回值，级别：1：[0~0.25]； 2(0.26,0.5] 3:(0.5,0.75] 4(0.75,1];
	public int getLevel(int total,int fail) {
		float radio = 0;
		if(total!=0) radio = (float)fail/total;
		if (radio <=0.25) {
			return 1;
		} else if(radio<=0.50) {
			return 2;
		} else if (radio<=0.75) {
			return 3;
		} else if(radio<=1){
			return 4;
		}
		return 1;

	}

	public static void main(String[] argv) {
		// new TaskMtwBusiness().doSync();
		new TaskMtwBusiness().generateWarm();

	}

}
