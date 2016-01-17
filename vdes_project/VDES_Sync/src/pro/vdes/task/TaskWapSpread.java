/**
 * TaskDNS.java 2009-7-23 下午12:16:23
 */
package pro.vdes.task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import common.util.ConfigureUtil;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskWapSpread extends TaskCommon {
	private static Logger log = Logger.getLogger(TaskWapSpread.class);

	private static String[] neCode = new String[] { "920001", "920002",
			"920003", "920004", "920005", "920006", "920007", "920008",
			"920009", "920010", "920011", "920012", "920013", "920014",
			"920015", "920016", "920017", "920018", "920019", "920020",
			"920021", "920022", "920023", "920024" };

	public TaskWapSpread(String table, int delete, int exists) {
		this.table = table;
		this.delete = delete;
		this.exists = exists;
	}

	// http://211.139.136.118:8080/HTML/mmskpi/wap/home/lizf/script/ilocate/history/ilocate.ip.wap0920006.20090713
	private String buildIpUrl(String wapSpreadFile, String neCode,
			String currentDay) {
		return wapSpreadFile + ".ip.wap0" + neCode + "." + currentDay;
	}

	// http://211.139.136.118:8080/HTML/mmskpi/wap/home/lizf/script/ilocate/history/ilocate.wap0920006.20090713
	private String buildCityUrl(String wapSpreadFile, String neCode,
			String currentDay) {
		return wapSpreadFile + ".wap0" + neCode + "." + currentDay;
	}
	
	//add by aiyan 2010-02-03 因为用户要求WAP与ggsn相互查找能力，每天凌晨4点到7点同步
	//http://218.204.251.32:8080/ggsn_history/ilocate.ggsn.wap0920008.20100203
	private String buildGGSNUrl(String wapSpreadggsnFile, String neCode,String currentDay) {
		return wapSpreadggsnFile + "/ilocate.ggsn.wap0" + neCode + "." + currentDay;
	}
	
	
	private void doSyncBeforeDayIp(String currentDay){
		// 先在表中删除目前这天的数据；
		deleteCurrentData(currentDay);
		// 增加数据;
		for (int i = 0; i < neCode.length; i++) {
			log.info(currentDay +" : "+ neCode[i]);
			HttpURLConnection httpUrl = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			BufferedInputStream bis = null;
			InputStreamReader reader = null;
			BufferedReader br = null;
			try {

				String wapSpreadFile = new ConfigureUtil()
						.getValue("wapSpreadFile");
				URL url = new URL(buildIpUrl(wapSpreadFile, neCode[i],
						currentDay));
				httpUrl = (HttpURLConnection) url
						.openConnection();
				// 连接指定的网络资源
				httpUrl.connect();
				// 获取网络输入流
				bis = new BufferedInputStream(httpUrl.getInputStream());
				reader = new InputStreamReader(bis);
				br = new BufferedReader(reader);
				String line = "";

				con = DBUtil.getConnection("proxool.vdes");
				String sql = "insert into bpw_wap_ip(neCode,statDate,ip) values(?,?,?)";
				pstmt = con.prepareStatement(sql);
				
				while ((line = br.readLine()) != null) {
					if (line.length() != 0) {
						pstmt.setString(1, neCode[i]);
						pstmt.setString(2, myDay(currentDay));
						pstmt.setString(3, line);
						pstmt.addBatch();
					}
				}
				pstmt.executeBatch();
				

			} catch (SQLException e) {
				log.error(e);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				log.error(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}catch(Exception e){
				log.error(e);
			}finally {
				if(br!=null)
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error(e);
					}
				if(reader!=null)
						try {
							reader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							log.error(e);
						}
				if(bis!=null)
					try {
						bis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error(e);
					}
					
				if(httpUrl!=null){
					httpUrl.disconnect();
				}


				DBUtil.release(pstmt, con);
			}

		}
		
	}
	private void doSyncBeforeDayCity(String currentDay){
		// 先在表中删除目前这天的数据；
		deleteCurrentData(currentDay);

		// 增加数据;
		for (int i = 0; i < neCode.length; i++) {
			log.info(currentDay +" : "+ neCode[i]);
			Connection con = null;
			PreparedStatement pstmt = null;
			try {
				con = DBUtil.getConnection("proxool.vdes");
				String sql = "insert into bpw_wap_city(neCode,statDate,city) values(?,?,?)";
				pstmt = con.prepareStatement(sql);
				String wapSpreadFile = new ConfigureUtil()
						.getValue("wapSpreadFile");
				URL url = new URL(buildCityUrl(wapSpreadFile, neCode[i],
						currentDay));
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

				while ((line = br.readLine()) != null) {
					if (line.length() != 0) {
						pstmt.setString(1, neCode[i]);
						pstmt.setString(2, myDay(currentDay));
						pstmt.setString(3, line);
						pstmt.addBatch();
					}
				}
				pstmt.executeBatch();

			} catch (SQLException e) {
				log.error(e);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				log.error(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e);
			} finally {
				DBUtil.release(pstmt, con);
			}

		}
		
	}
	private void doSyncBeforeDayGGSN(String currentDay){
		
		java.util.Calendar c = Calendar.getInstance();

		int hour = c.get(Calendar.HOUR_OF_DAY);
		if(hour>10) return;
		
		
		
		// 先在表中删除目前这天的数据；
		deleteCurrentData(currentDay);
		// 增加数据;
		for (int i = 0; i < neCode.length; i++) {
			log.info(currentDay +" : "+ neCode[i]);
			HttpURLConnection httpUrl = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			BufferedInputStream bis = null;
			InputStreamReader reader = null;
			BufferedReader br = null;
			try {

				String wapSpreadGGSNFile = new ConfigureUtil()
						.getValue("wapSpreadGGSNFile");
				URL url = new URL(buildGGSNUrl(wapSpreadGGSNFile, neCode[i],
						currentDay));
				httpUrl = (HttpURLConnection) url
						.openConnection();
				// 连接指定的网络资源
				httpUrl.connect();
				// 获取网络输入流
				bis = new BufferedInputStream(httpUrl.getInputStream());
				reader = new InputStreamReader(bis);
				br = new BufferedReader(reader);
				String line = "";

				con = DBUtil.getConnection("proxool.vdes");
				String sql = "insert into bpw_wap_ggsn(neCode,statDate,ggsn) values(?,?,?)";
				pstmt = con.prepareStatement(sql);
				
				while ((line = br.readLine()) != null) {
					if (line.length() != 0) {
						pstmt.setString(1, neCode[i]);
						pstmt.setString(2, myDay(currentDay));
						pstmt.setString(3, line);
						pstmt.addBatch();
					}
				}
				pstmt.executeBatch();
				

			} catch (SQLException e) {
				log.error(e);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				log.error(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}catch(Exception e){
				log.error(e);
			}finally {
				if(br!=null)
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error(e);
					}
				if(reader!=null)
						try {
							reader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							log.error(e);
						}
				if(bis!=null)
					try {
						bis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error(e);
					}
					
				if(httpUrl!=null){
					httpUrl.disconnect();
				}


				DBUtil.release(pstmt, con);
			}

		}
		
	}
	protected void doSyncBeforeDay(String currentDay) {
		if(table.equals("bpw_wap_ip")){
			doSyncBeforeDayIp(currentDay);
		}else if(table.equals("bpw_wap_city")){
			doSyncBeforeDayCity(currentDay);
		}else if(table.equals("bpw_wap_ggsn")){
			doSyncBeforeDayGGSN(currentDay);
		}
	}

}
