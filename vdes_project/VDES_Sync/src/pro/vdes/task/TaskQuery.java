/**
 * TaskQuery.java 2009-7-20 下午04:21:30
 */
package pro.vdes.task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import sample.xmlbean.RootDocument.Root.Time;

import common.db.CommonDao;
import common.util.DBUtil;

import config.DocParse;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskQuery {
	private static Logger log = Logger.getLogger(TaskQuery.class);
	private Set<String> currentXmlSet = new HashSet<String>();
	private Timer vdesTimer = null;
	private Set<String> newXmlSet = new HashSet<String>();

	private Map<String, TimerTask> TaskTimeQuere = new HashMap<String, TimerTask>();
	private Map<String, TimerTask> TaskTimeExistQuere = new HashMap<String, TimerTask>();

	public TaskQuery(Timer vdesTimer) {
		this.vdesTimer = vdesTimer;
	}

	public void query() {
		log.info("高可配查询的线程");
		handleOper();
		// status=0 但是 该线程所对应的采集文件又在 currentXmlSet 中，这是模拟用户从启用到停用的过程。
		handleStatus();

	}

	public void executeSQl(String sql) {
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			dao.execute(sql, null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (dao != null) {
					dao.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private void handleOper() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt
					.executeQuery("select status,gatherPath,oper from Sys_BusinessGather where oper in('ADD','MODI','DEL','HANDLE') ");
			int status = -1;
			while (rs.next()) {
				status = rs.getInt("status");
				String oper = rs.getString("oper");
				String gatherPath = rs.getString("gatherPath");
				if (status == 1
						&& (oper.equals("ADD") || oper.equals("HANDLE"))) {
					if (!currentXmlSet.contains(gatherPath)) {
						executeSQl("update Sys_BusinessGather set oper='HANDLE' where gatherPath='"
								+ gatherPath + "'");
						newXmlSet.add(gatherPath);
						currentXmlSet.add(gatherPath);
					}

				} else if (status == 1 && oper.equals("MODI")) {
					TimerTask tt = TaskTimeQuere.get(gatherPath);
					if (tt != null) {
						tt.cancel();
						vdesTimer.purge();
						TaskTimeQuere.remove(gatherPath);
						log.info(gatherPath + "被停用!");
					}
					TimerTask existtt = TaskTimeExistQuere.get(gatherPath);
					if (existtt != null) {
						existtt.cancel();
						vdesTimer.purge();
						TaskTimeExistQuere.remove(gatherPath);
						log.info(gatherPath + "--exist-被停用!");
					}

					executeSQl("update Sys_BusinessGather set oper='ADD' where gatherPath='"
							+ gatherPath + "'");
					currentXmlSet.remove(gatherPath);

				} else if (oper.equals("DEL")) {
					TimerTask tt = TaskTimeQuere.get(gatherPath);
					if (tt != null) {
						tt.cancel();
						vdesTimer.purge();
						TaskTimeQuere.remove(gatherPath);
						log.info(gatherPath + "被停用!");
					}
					TimerTask existtt = TaskTimeExistQuere.get(gatherPath);
					if (existtt != null) {
						existtt.cancel();
						vdesTimer.purge();
						TaskTimeExistQuere.remove(gatherPath);
						log.info(gatherPath + "--exist-被停用!");
					}

					executeSQl("delete from Sys_BusinessGather where gatherPath='"
							+ gatherPath + "'");
					currentXmlSet.remove(gatherPath);

				}

			}
		} catch (SQLException e) {
			log.error(e.getMessage(),e);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} finally {
			DBUtil.release(rs, stmt, con);

		}
	}

	private String Set2SQLStr(Set<String> currentXmlSet) {
		if (currentXmlSet == null) {
			return null;
		}

		Iterator<String> it = currentXmlSet.iterator();
		String sqlStr = "";
		while (it.hasNext()) {
			sqlStr += ",'" + it.next() + "'";
		}
		if (sqlStr.length() > 1) {
			return sqlStr.substring(1);
		}
		return null;
	}

	private void handleStatus() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt
					.executeQuery("select gatherPath,oper from Sys_BusinessGather where status=0 and gatherPath in("
							+ Set2SQLStr(currentXmlSet) + ") ");
			while (rs.next()) {
				String gatherPath = rs.getString("gatherPath");
				TimerTask tt = TaskTimeQuere.get(gatherPath);
				if (tt != null) {
					tt.cancel();
					vdesTimer.purge();
					TaskTimeQuere.remove(gatherPath);
					log.info(gatherPath + "被停用!");
				}
				TimerTask existtt = TaskTimeExistQuere.get(gatherPath);
				if (existtt != null) {
					existtt.cancel();
					vdesTimer.purge();
					TaskTimeExistQuere.remove(gatherPath);
					log.info(gatherPath + "--exist-被停用!");
				}
				currentXmlSet.remove(gatherPath);

			}
		} catch (SQLException e) {log.error(e.getMessage(), e);
		}catch (Exception e) {log.error(e.getMessage(), e);
		}finally {
			DBUtil.release(rs, stmt, con);
		}
	}

	public void doSyncByQuery() {
		Iterator<String> it = newXmlSet.iterator();
		while (it.hasNext()) {
			sync(it.next());
		}
		newXmlSet.clear();
	}

	protected boolean sync(String newXml) {
		DocParse docParseFinal = null;
		try {
			docParseFinal = new DocParse(newXml);
		} catch (XmlException e) {
			log.info(newXml + ":采集文件格式有误!");
			log.error(e);
			return false;
		} catch (IOException e) {
			log.info(newXml + ":采集文件IO有误!");
			log.error(e);
			return false;
		} catch (Exception e) {
			log.info(newXml + ":采集文件有误!");
			log.error(e);
			return false;
		}

		final DocParse docParse = docParseFinal;
		Time time = docParse.getTime();
		String start = time.getStart();
		Calendar c1 = Calendar.getInstance();
		if (start == null) {
			c1.set(Calendar.HOUR_OF_DAY, 1);// 凌晨1点钟执行
			c1.set(Calendar.MINUTE, 0);
			c1.set(Calendar.SECOND, 0);
		} else {
			String hour = start.substring(0, start.indexOf(":"));// 按照配置的start执行
			String minute = start.substring(start.indexOf(":") + 1);
			c1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			c1.set(Calendar.MINUTE, Integer.parseInt(minute));
			c1.set(Calendar.SECOND, 0);
		}

		final TaskEntity te = new TaskEntity(docParse);
		te.createTable();
		te.modiTable();
		TimerTask tt = new TimerTask() {

			@Override
			public void run() {
				//log.info("this.getClass():" + this.getClass());
				//String suffix = docParse.getSuffix();
				//if (suffix == null || suffix.equals("week")) {
					te.doSync();
					// te.doSyncTest();
				//} else if (suffix.equals("month")) {
					//te.doSyncWeek2Month();
					// te.doSyncWeek2MonthTest();
				//}
			}
		};

		TaskTimeQuere.put(docParse.getXmlFile(), tt);
		vdesTimer.scheduleAtFixedRate(tt, c1.getTime(),
				getDelay(time.getRedo()));

		TimerTask existTask = new TimerTask() {
			@Override
			public void run() {
				te.existData();
			}
		};
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.HOUR_OF_DAY, 1);// 凌晨1点钟执行
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		vdesTimer.scheduleAtFixedRate(existTask, c2.getTime(),
				24 * 60 * 60 * 1000);
		TaskTimeExistQuere.put(docParse.getXmlFile(), existTask);

		return true;
	}

	private long getDelay(String a) {
		long r = 1;
		String[] inta = a.split("\\*");
		for (String s : inta) {
			r *= Long.parseLong(s);
		}
		return r;
	}

}
