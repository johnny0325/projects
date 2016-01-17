/**
 * Entity.java 2009-7-7 下午05:13:43
 */
package pro.vdes.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

import sample.xmlbean.RootDocument.Root.Table.Field;

import common.db.CommonDao;
import common.util.DBUtil;

import config.DocParse;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskEntity {
	private static Logger log = Logger.getLogger(TaskEntity.class);
	private DocParse docParse = null;

	public TaskEntity(DocParse docParse) {
		this.docParse = docParse;
	}

	public void execute(String sql) {
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection(docParse.getDesDB());
			stmt = con.createStatement();
			if (sql != null) {
				stmt.execute(sql);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, con);
		}
	}

	private boolean isExistTable() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String str = null;
		try {
			con = DBUtil.getConnection(docParse.getDesDB());
			stmt = con.createStatement();
			rs = stmt.executeQuery("select objectproperty(object_id('"
					+ docParse.getTableName() + "'),'istable')");
			if (rs.next()) {
				str = rs.getString(1);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs, stmt, con);
		}
		return str != null && str.equals("1");

	}

	public void createTable() {
		log.info("enter..create table..");
		if (isExistTable()) {
			log.info("the table[" + docParse.getTableName()
					+ "] is exist,don't need create...");
			return;
		}
		log.info("the table[" + docParse.getTableName()
				+ "] is new table,need create...");
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			String sql = docParse.createSql();
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
		log.info("go away..create table..");
	}

	public void modiTable(){
		log.info("enter..modi table..");
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			String sql = docParse.modiSql();
			dao.execute(sql, null);
		} catch (Exception e) {
			log.error("注意，"+docParse.modiSql()+"这个MODI语句不被执行！");
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
		log.info("go away..modi table..");		
	}
	
	public void doSyncTest() {
		log.info("TaskEntity-->doSyncTest--->" + docParse.getXmlFile());
	}

	public void doSync() {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection(docParse.getDesDB());
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) statDate from "
					+ docParse.getTableName() + " order by statDate desc");
			String currentDate = null;
			if (rs.next() && rs.getTimestamp(1) != null) {
				// log.info("docParse.getTableName()"+docParse.getTableName());
				// log.info("rs.getTimestamp(1)"+rs.getTimestamp(1));
				currentDate = DateUtil.formatDate(rs.getTimestamp(1),
						"yyyyMMdd");
			} else {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDate = DateUtil.formatDate(c.getTime(), "yyyyMMdd");
			}
			short delete = docParse.getTime().getDelete();
			currentDate = nextDay(currentDate, delete * -1);// 对三天的数据进行同步；如目前是15号，则要对13号,14号,15号都同步；因为彩信数据48小时稳定；
			String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
			while (currentDate.compareTo(today) <= 0) {
				doSyncBeforeDay(currentDate);
				currentDate = nextDay(currentDate, 1);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}

	}

	private String nextDay(String currentDate, int next) {
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0, 4));
		int m = Integer.parseInt(currentDate.substring(4, 6));
		int d = Integer.parseInt(currentDate.substring(6, 8));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m - 1);
		c.set(Calendar.DATE, d + next);

		return DateUtil.formatDate(c.getTime(), "yyyyMMdd");

	}

	private String myDay(String currentDate) {
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0, 4));
		int m = Integer.parseInt(currentDate.substring(4, 6));
		int d = Integer.parseInt(currentDate.substring(6, 8));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m - 1);
		c.set(Calendar.DATE, d);
		return DateUtil.formatDate(c.getTime(), "yyyy-MM-dd");

	}

	protected void doSyncBeforeDay(String currentDay) {
		Connection p_con = null;
		Connection v_con = null;
		try {
			p_con = DBUtil.getConnection(docParse.getSrcDB());
			v_con = DBUtil.getConnection(docParse.getDesDB());
			Statement v_stmt = v_con.createStatement();
			Statement p_stmt = p_con.createStatement();
			// log.info("docParse.getTableName()"+docParse.getTableName());
			PreparedStatement v_pstmt = v_con.prepareStatement(docParse
					.insertSql());

			// 再开始事务
			v_con.setAutoCommit(false);

			// 先执行数据查询
			String sql = docParse.selectSql();

			// sql中含有yyyyMMdd或者yyyy-MM-dd需要做特殊处理;
			if (sql.indexOf("yyyyMMdd") != -1) {
				sql = sql.replace("yyyyMMdd", currentDay);
			}

			int myDayIndex = -1;
			if (sql.indexOf("yyyy-MM-dd") != -1) {
				String str = sql.substring(0, sql.indexOf("yyyy-MM-dd"));
				myDayIndex = str.split(",").length + 1;

				sql = sql.replace("yyyy-MM-dd", "null");

			}
			// 特殊处理结束；

			log.info("sql:" + sql);
			ResultSet p_rs = p_stmt.executeQuery(sql);

			// 先在周表中删除目前这天的数据；
			String deleteSql = "delete from  " + docParse.getTableName()
					+ " where datediff(d,'" + myDay(currentDay)
					+ "',statDate)=0";
			int del = v_stmt.executeUpdate(deleteSql);
			log.info("del：" + del);

			// 插入新数据
			int i = 0;
			Field[] fields = docParse.getFields();
			int index = 0;
			while (p_rs.next()) {

				for (Field f : fields) {
					index++;
					if (index == myDayIndex) {
						v_pstmt.setString(index, myDay(currentDay));
					} else {
						// log.info(""+index+","+f.getStringValue());
						v_pstmt.setString(index, p_rs.getString(f
								.getStringValue()));
					}

				}
				index = 0;
				v_pstmt.addBatch();

				if (++i % 500 != 0) {
					continue;
				}
				// 500 个才执行一次
				int[] ae = v_pstmt.executeBatch();
				log.info("ae.length" + ae.length);

			}
			// 最后几行处理
			int[] b = v_pstmt.executeBatch();
			log.info(b.length);

			// 提交事务
			v_con.commit();

			// 关闭数据库对象；
			p_rs.close();
			p_stmt.close();
			v_pstmt.close();

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			try {
				v_con.rollback();
			} catch (SQLException e1) {
				log.error(e1.getMessage(), e1);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.releaseConnection(p_con);
			DBUtil.releaseConnection(v_con);
		}

	}

	public void doSyncWeek2MonthTest() {
		log.info("TaskEntity-->doSyncWeek2MonthTest--->"
				+ docParse.getXmlFile());
	}

	public void doSyncWeek2Month() {
		Connection con = DBUtil.getConnection(docParse.getDesDB());
		try {
			// PreparedStatement pstmt =
			// con.prepareStatement("insert into bpw_mms_"+syncObj+"_month("+syncObj+"Code,statDate,indicator,status,count) values(?,?,?,?,?)");

			PreparedStatement pstmt = con
					.prepareStatement(docParse.insertSql());
			Statement stmt = con.createStatement();
			Statement stmt2 = con.createStatement();
			ResultSet rs = stmt.executeQuery("select top (1) statDate from "
					+ docParse.getTableName() + " order by statDate desc");
			ResultSet rs2 = null;
			Date currnetDateTime = null;
			Calendar c = Calendar.getInstance();
			short delete = docParse.getTime().getDelete();
			if (rs.next()) {
				currnetDateTime = rs.getTimestamp(1);
				c.setTime(currnetDateTime);
				c.add(Calendar.DATE, delete * -1);// 对三天的数据进行同步；如目前是15号，则要对13号,14号,15号都同步；因为彩信数据48小时稳定；
				currnetDateTime = c.getTime();
			} else {
				c.add(Calendar.DATE, -7);
				currnetDateTime = c.getTime();
			}
			String formatDate = DateUtil.formatDate(currnetDateTime,
					"yyyy-MM-dd");

			// 开始事务
			con.setAutoCommit(false);

			// 查询数据
			Field[] fields = docParse.getFields();
			StringBuffer sb = new StringBuffer();
			for (Field f : fields) {
				sb.append(",").append(f.getStringValue());
			}

			String sql = "select " + sb.toString().substring(1) + " from ("
					+ docParse.selectSql() + ") a where datediff(d,'"
					+ formatDate + "',statDate)>0";
			rs2 = stmt2.executeQuery(sql);


			// 先在月表中删除目前这些天的数据；
			String deleteSql = "delete from " + docParse.getTableName()
					+ " where datediff(d,'" + formatDate + "',statDate)>0";
			log.info("month-del-sql" + deleteSql);
			int del = stmt.executeUpdate(deleteSql);
			log.info("doSyncWeek2Month->del" + del);

			int index = 0;
			while (rs2.next()) {
				for (Field f : fields) {
					pstmt.setString(++index, rs2.getString(f.getStringValue()));
				}
				index = 0;
				pstmt.addBatch();
			}
			int[] a = pstmt.executeBatch();
			log.info("a.length" + a.length);

			// 提交事务
			con.commit();

			rs.close();
			rs2.close();
			stmt.close();
			stmt2.close();
			pstmt.close();

		} catch (SQLException e) {
			log.error(e.getMessage(), e);

			try {
				con.rollback();
			} catch (SQLException e1) {
				log.error(e1.getMessage(), e1);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.releaseConnection(con);
		}

	}

	public void existData() {

		log.info("existData");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) statDate from "
					+ docParse.getTableName() + "  order by statDate desc");
			if (rs.next()) {
				short exist = docParse.getTime().getExist();
				String sql = "delete from  " + docParse.getTableName()
						+ " where datediff(d,statdate,'"
						+ DateUtil.formatDate(rs.getTimestamp(1), "yyyy-MM-dd")
						+ "')>" + exist;
				int i = stmt.executeUpdate(sql);
				log.info("existsData  | 删除 " + i + " 条");
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(rs, stmt, con);
		}

	}

}
