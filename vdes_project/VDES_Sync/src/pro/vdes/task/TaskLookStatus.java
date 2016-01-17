package pro.vdes.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import pro.vdes.service.monitor.Constant;
import pro.vdes.service.monitor.DBICareVisitor;
import pro.vdes.service.monitor.DBPartnerVisitor;
import pro.vdes.service.monitor.DBVDESVisitor;
import pro.vdes.service.monitor.IMonitorService;
import pro.vdes.service.monitor.IndexVisitor;
import pro.vdes.service.monitor.Look;
import pro.vdes.service.monitor.MonitorInfoBean;
import pro.vdes.service.monitor.MonitorServiceImpl;

import common.db.CommonDao;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.DateUtil;
import common.util.Mail;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskLookStatus {
	private static Logger log = Logger.getLogger(TaskLookStatus.class);

	public void delete(String types) {
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			dao.execute("delete from Sys_look_status where type in(" + types
					+ ")", null);
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

	public List<Look> lookOS() {
		log.info("lookOS start!");
		List<Look> osList = new ArrayList<Look>();
		IMonitorService iMonitorService = new MonitorServiceImpl();
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		
		MonitorInfoBean info = null;
		try {
			info = iMonitorService.getMonitorInfoBean();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(info==null) return null;
		
		float cpuRatio = (float)(info.getCpuRatio() * 0.01);
		cpuRatio = (float)(Math.round(cpuRatio*100)/100.00);//四舍五入，保留两位小数，add by aiyan 2009-10-22
		
		float memoryRatio = (float) info.getUsedMemory()
				/ info.getTotalMemorySize();		
		memoryRatio = (float)(Math.round(memoryRatio*100)/100.00);//四舍五入，保留两位小数，add by aiyan 2009-10-22

		ConfigureUtil cfgUtil = new ConfigureUtil();
		float sysOsCpuRadio = Float.valueOf(cfgUtil
				.getValue("sysOsCpuRadio"));
		float sysOsMemRadio = Float.valueOf(cfgUtil
				.getValue("sysOsMemRadio"));
		float sysOsDiskRadio = Float.valueOf(cfgUtil
				.getValue("sysOsDiskRadio"));


		int status_cpu = cpuRatio > sysOsCpuRadio ? 0 : 1;
		int status_memory = memoryRatio > sysOsMemRadio ? 0 : 1;

		NumberFormat df = NumberFormat.getPercentInstance();
		String cpuRatioStr = df.format(cpuRatio);
		String memRatioStr = df.format(memoryRatio);
		
		
		try {
			con = DBUtil.getConnection("proxool.vdes");
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt.executeUpdate("delete from Sys_look_status where type = 1");
			
			String sql = "insert into Sys_look_status(type,Indicators,value,statDate,status) values(?,?,?,getdate(),?)";
			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, 1);
			pstmt.setString(2, "cpu");
			pstmt.setString(3, cpuRatioStr);
			pstmt.setInt(4, status_cpu);
			pstmt.addBatch();

			pstmt.setInt(1, 1);
			pstmt.setString(2, "memory");
			pstmt.setString(3, memRatioStr);
			pstmt.setInt(4, status_memory);
			pstmt.addBatch();

			
			float r = 0;
			for (File root : info.getListRoots()) {
				pstmt.setInt(1, 1);
				pstmt.setString(2, "disk_" + root.getPath().substring(0, 1));
				r = root.getTotalSpace() == 0 ? 0 : (float) (root
						.getTotalSpace() - root.getUsableSpace())
						/ root.getTotalSpace();
				log.info("r:" + r);
				pstmt.setString(3, df.format(r));
				
				r = (float)(Math.round(r*100)/100.00);//四舍五入，保留两位小数，add by aiyan 2009-10-22
				
				pstmt.setInt(4, r > sysOsDiskRadio ? 0 : 1);
				pstmt.addBatch();
			}

			pstmt.executeBatch();
			
			con.commit();

			

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(pstmt, con);
		}
		
		///txt generate begin
		if (cpuRatio > sysOsCpuRadio) {
			Look look = new Look();
			look.setType(1);// Sys_look_status中的type 字段
			// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控

			String cpuNormalRatioStr = df.format(sysOsCpuRadio);
			look.setContent("cpu usage high: " + cpuRatioStr
					+ ", normal value is " + cpuNormalRatioStr);
			look.setChineseDesc("vdes 服务器cpu 使用率是" + cpuRatioStr
					+ ", 它超过了阀值" + cpuNormalRatioStr);
			osList.add(look);
		}
		if (memoryRatio > sysOsMemRadio) {
			Look look = new Look();
			look.setType(1);// Sys_look_status中的type 字段
			// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控

			String memNormalRatioStr = df.format(sysOsMemRadio);
			look.setContent("memory usage high: " + memRatioStr
					+ ", normal value is " + memNormalRatioStr);
			look.setChineseDesc("vdes 服务器内存使用率是" + memRatioStr + ", 它超过了阀值"
					+ memNormalRatioStr);
			osList.add(look);
		}

		String diskNormalRatioStr = df.format(sysOsDiskRadio);
		for (File root : info.getListRoots()) {
			if (root.getTotalSpace() == 0) {
				continue;
			}

			float diskRatio =  (float)(root.getTotalSpace() - root
					.getUsableSpace())
					/ root.getTotalSpace();
			
			diskRatio = (float)(Math.round(diskRatio*100)/100.00);//四舍五入，保留两位小数，add by aiyan 2009-10-22
			
			if (diskRatio > sysOsDiskRadio) {
				Look look = new Look();
				look.setType(1);
				String diskName = root.getPath().substring(0, 1);
				String diskRatioStr = df.format(diskRatio);
				look.setContent("Disk_" + diskName + " usage high: "
						+ diskRatioStr + ", normal value is "
						+ diskNormalRatioStr);
				look.setChineseDesc("vdes 服务器硬盘" + diskName + " 使用率是"
						+ diskRatioStr + ", 它超过了阀值" + diskNormalRatioStr);
				osList.add(look);
			}
		}
		///txt generate end
		
		
		log.info("lookOS end!");
		return osList;

	}

	public List<Look> lookIndex() {
		log.info("lookIndex start!");
		List<Look> indexList = new ArrayList<Look>();
		String ret = new IndexVisitor().visit();
		//txt generate begin
		if (ret.equals(Constant.ERROR)) {
			Look look = new Look();
			look.setType(2);// Sys_look中的type 字段
			// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控
			look.setContent("can't access homepage");
			look.setChineseDesc("Vdes 不能访问首页。");
			indexList.add(look);
		}
		//txt generate end
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			dao.begin();
			dao.execute("delete from Sys_look_status where type = 2",null);
			List<String> params = new ArrayList<String>();
			params.add(ret);
			params.add(ret);
			String sql = "insert into Sys_look_status(type,Indicators,value,statDate,status) values(2,'vdes',?,getdate(),?)";
			dao.execute(sql, params);
			
			dao.commit();


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
		

		
		log.info("lookIndex end!");
		return indexList;
	}

	public List<Look> lookDB() {
		log.info("lookDB start!");
		List<Look> dbList = new ArrayList<Look>();
		Look lookDBVDES = lookDBVDES();
		Look lookDBPartner = lookDBPartner();
		Look lookDBICare = lookDBICare();
		log.info("--------------------------=========lookDBVDES"+lookDBVDES+"==================-----------------------");
		if (lookDBVDES != null) {
			dbList.add(lookDBVDES);
		}
		log.info("--------------------------=========lookDBPartner"+lookDBPartner+"==================-----------------------");
		if (lookDBPartner != null) {
			dbList.add(lookDBPartner);
		}
		log.info("--------------------------=========lookDBPartner"+lookDBICare+"==================-----------------------");
		if (lookDBICare != null) {
			dbList.add(lookDBICare);
		}
		log.info("lookDB end!");
		return dbList;

	}

	public Look lookDBVDES() {
		log.info("lookDBVDES start!");
		Look look = null;
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		String visit = new DBVDESVisitor().visit();

		//txt generate begin
		if (visit.equals(Constant.ERROR)) {
			look = new Look();
			look.setType(3);// Sys_look中的type 字段
			// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控
			look.setContent("can't access local database");
			look.setChineseDesc("VDES 不能连通本地数据库。");
		}
		//txt generate end
		try {
			con = DBUtil.getConnection("proxool.vdes");
			
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt.executeUpdate("delete from Sys_look_status where type = 3 and Indicators='db_vdes'");
			
			String sql = "insert into Sys_look_status(type,Indicators,value,statDate,status) values(?,?,?,getdate(),?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, 3);
			pstmt.setString(2, "db_vdes");
			pstmt.setString(3, visit);
			pstmt.setString(4, visit);
			pstmt.addBatch();

			pstmt.executeBatch();

			con.commit();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBUtil.release(pstmt, con);
		}
		log.info("lookDBVDES end!");
		return look;

	}

	public Look lookDBPartner() {
		log.info("lookDBPartner start!");
		Look look = null;
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		String visit = new DBPartnerVisitor().visit();

		if (visit.equals(Constant.ERROR)) {
			look = new Look();
			look.setType(3);// Sys_look中的type 字段
			// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控
			look.setContent("can't access exusoft's database");
			look.setChineseDesc("VDES 不能连通优讯数据库。");
		}

		try {
			con = DBUtil.getConnection("proxool.vdes");
			
			con.setAutoCommit(false);
			
			stmt = con.createStatement();
			stmt.executeUpdate("delete from Sys_look_status where type = 3 and Indicators='db_partner'");		
			
			String sql = "insert into Sys_look_status(type,Indicators,value,statDate,status) values(?,?,?,getdate(),?)";
			pstmt = con.prepareStatement(sql);
			


			pstmt.setInt(1, 3);
			pstmt.setString(2, "db_partner");
			pstmt.setString(3, visit);
			pstmt.setString(4, visit);
			pstmt.addBatch();

			pstmt.executeBatch();
			
			con.commit();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBUtil.release(pstmt, con);
		}
		log.info("lookDBPartner end!");
		return look;
	}

	public Look lookDBICare() {
		log.info("lookDBICare start!");
		Look look = null;
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		String visit = new DBICareVisitor().visit();
		if (visit.equals(Constant.ERROR)) {
			look = new Look();
			look.setType(3);// Sys_look中的type 字段
			// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控
			look.setContent("can't access icare's database");
			look.setChineseDesc("VDES 不能连通icare 数据库。");
		}

		try {
			con = DBUtil.getConnection("proxool.vdes");
			con.setAutoCommit(false);
			
			stmt = con.createStatement();
			stmt.executeUpdate("delete from Sys_look_status where type = 3 and Indicators='db_icare'");	
			
			String sql = "insert into Sys_look_status(type,Indicators,value,statDate,status) values(?,?,?,getdate(),?)";
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, 3);
			pstmt.setString(2, "db_icare");
			pstmt.setString(3, visit);
			pstmt.setString(4, visit);
			pstmt.addBatch();

			pstmt.executeBatch();
			
			con.commit();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DBUtil.release(pstmt, con);
		}
		log.info("lookDBICare end!");
		return look;
	}

	public List<Look> lookData() {
		log.info("lookData start!");
		List<Look> dataList = new ArrayList<Look>();
		List<Look> mmsList = getMMSListLook();
		List<Look> wapList = getWAPListLook();

		CommonDao dao = null;
		try {
			dao = new CommonDao();// Sys_look_status中的type 字段
			dao.begin();
			dao.execute("delete from Sys_look_status where type = 4",null);
			// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控
			if(mmsList!=null){
				int status = mmsList.size() > 0 ? 0 : 1;
				String mmsSql = "insert into Sys_look_status(type,Indicators,value,statDate,status) values(4,'mms','"
						+ status + "',getdate(),'" + status + "')";
				dao.execute(mmsSql, null);
			}

			if(wapList!=null){
				int status = wapList.size() > 0 ? 0 : 1;
				String wapSql = "insert into Sys_look_status(type,Indicators,value,statDate,status) values(4,'wap','"
						+ status + "',getdate(),'" + status + "')";
				dao.execute(wapSql, null);			
			}

			
			dao.commit();

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

		if(mmsList!=null) dataList.addAll(mmsList);
		
		if(wapList!=null) dataList.addAll(wapList);
		log.info("lookData end!");
		return dataList;

	}

	public List<Look> getWAPListLook() {
		return doLookData("wap");
	}

	public List<Look> getMMSListLook() {
		return doLookData("mms");
	}
	
	private List genHourData(String type){
		List<Map> ret = new ArrayList();
		CommonDao dao = null;
		try {
			String weekTable="";
			String monthTable = "";
			
			if (type.equals("mms")) {
				weekTable="bpw_mms_ne_week";
				monthTable = "bpw_mms_ne_month";
				
			} else if (type.equals("wap")) {
				weekTable="bpw_wap_ne_week";
				monthTable = "bpw_wap_ne_month";
			}
			int i=-6;
			dao = new CommonDao();
			
			Calendar c = Calendar.getInstance();
			c.set(c.MINUTE, 0);
			c.set(c.SECOND, 0);
			
			String currentDate = DateUtil.format(c.getTime(),DateUtil.C_TIME_PATTON_DEFAULT);
			//前3到前7
			while(i<=-3){
				String sql = "select distinct(neCode),DateAdd(hh, "+i+", '"+currentDate+"') as currentDate from "+monthTable+" where neCode  not in"+
				" (select distinct(neCode) from "+weekTable+" where"+
				" datediff(hh,DateAdd(hh, "+i+", '"+currentDate+"'),statDate)=0)  order by neCode";
				List<Map> notInclue = dao.query(sql, null);
				System.out.println("sql"+sql);
				for(Map m:notInclue){
					ret.add(m);
				}
				i++;
				
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}	
		return ret;
	}

	public List<Look> doLookData(String type) {
		List<Look> l = new ArrayList();
		try {
			
			List list = genHourData(type);
			if (list.size() == 0) {
				return null;
			}


			
			String neCodes = "";
			String datetime = "";
			String next = "";
			Map map = null;
			for (int i = 0; i < list.size(); i++) {

				
				map = (Map)(list.get(i));
				datetime = map.get("currentDate")+"";
				neCodes += ","+map.get("neCode");
				if(i+1<list.size()){
					next = ((Map)(list.get(i+1))).get("currentDate")+"";
				}else{
					next = null;
				}
				if(datetime.equals(next)){
					continue;
				}else{
					Look ret = new Look();
					ret.setType(4);// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控
					
					
					if (neCodes.startsWith(",")) {
						neCodes = neCodes.substring(1);
					}

					
					String content = "report data of " + type + " " + neCodes
							+ " missed in " + DateUtil.getDateStr(map.get("currentDate")+"",DateUtil.C_TIME_PATTON_DEFAULT);
					ret.setContent(content);

					String typeDesc = type;
					if (type.equals("mms")) {
						typeDesc = "彩信";
					} else if (type.equals("wap")) {
						typeDesc = "WAP";
					}
					String desc = typeDesc + "网元" + neCodes + "在" +  DateUtil.getDateStr(map.get("currentDate")+"",DateUtil.C_TIME_PATTON_DEFAULT)
							+ "缺漏业务数据。";
					ret.setChineseDesc(desc);
					l.add(ret);
					neCodes = "";
				}
				
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return l;

	}

	public void genarateTXT(List<Look> lookList, String file) {
		String syslookfile = new ConfigureUtil().getValue("syslookfile");
		File p = new File(syslookfile);
		if (!p.exists()) {
			p.mkdirs();
		}
		File desFile = new File(syslookfile, file);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(desFile, false)));

			for (Look look : lookList) {
				bw.write("vdes:" + look.getContent());
				bw.write("\r\n");
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

	}

	public void genarateTXT(String t123, String t4) {
		String syslookfile = new ConfigureUtil().getValue("syslookfile");
		String desStr = "sysLook" + DateUtil.format(new Date(), "yyyyMMdd")
				+ ".txt";
		File desFile = new File(syslookfile, desStr);
		if (!desFile.exists()) {
			try {
				desFile.createNewFile();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		File srcFile1 = new File(syslookfile, t123);
		File srcFile2 = new File(syslookfile, t4);

		copy(desFile, srcFile1, false);// 开始清空
		copy(desFile, srcFile2, true);// 追加
	}

	private void copy(File desFile, File srcFile, boolean flag) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(srcFile));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(desFile, flag)));
			String line = "";
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.write("\r\n");
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	public void genarateDB(List<Look> lookList) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "insert into Sys_look(type,content,statDate,chineseDesc) values(?,?,getdate(),?)";

		try {
			con = DBUtil.getConnection("proxool.vdes");
			pstmt = con.prepareStatement(sql);
			Look look = null;
			for (int i = 0; i < lookList.size(); i++) {
				look = lookList.get(i);
				pstmt.setInt(1, look.getType());
				pstmt.setString(2, look.getContent());
				pstmt.setString(3, look.getChineseDesc());
				pstmt.addBatch();
			}

			pstmt.executeBatch();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(pstmt, con);
		}

	}
	
	public static void main(String[] argv){
//		List<Look> l= new TaskLookStatus().doLookData("wap");
//		System.out.println("uuuuuuu "+l.size());
//		for(Look k:l){
//			System.out.println(k.getContent());
//		}

		
		List<Look> l= new TaskLookStatus().getLookDataTps();
		System.out.println("uuuuuuu "+l.size());
		new TaskLookStatus().genarateDB(l);
		new SendMailThread2(l).start();

		
	}

	public void doWarn(){
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		//add by aiyan 雷工因为每个小时都存在TPS缺漏，觉得不好，然后决定早上八点和下午五点做这个TPS缺漏问题。2010-03-19
		if(hour==8||hour==17){
			List<Look> l=getLookDataTps();
			genarateDB(l);
			new SendMailThread2(l).start();
		}
	}
	/**
	 * //记录当TPS值为空产生缺漏告警 精确到小时 add by aiyan 2010-03-01
	 */
	public List<Look> getLookDataTps() {
		// TODO Auto-generated method stub
		List<Look> lookList = new ArrayList();
		List queryList = null;
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			String sql =" select a.neCode,"+
			" isnull(statDate,getdate()) as statDate "+
			" from ("+
			" select distinct(neCode) from bpw_wap_ne_week) a left join "+
			" (select * from bpw_wap_ne_week where datediff(hh,statDate,getdate())=0) b "+
			" on a.neCode = b.neCode where tps is null order by a.neCode";
			
			
			queryList = dao.query(sql, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(queryList==null||queryList.size()==0) return null;
		Map map = null;
		for(int i=0;i<queryList.size();i++){
			map = (Map)queryList.get(i);
			Look ret = new Look();
			ret.setType(4);// 1:操作系统监控，2：网站首页自检，3数据库连接监控，4：数据缺漏监控
			String content = "report data of tps "+map.get("neCode")
					+ " missed in " + DateUtil.getDateStr2(map.get("statDate")+"","yyyy-MM-dd HH:00:00");
			ret.setContent(content);
			String desc = "网元" + map.get("neCode") + " 的TPS值在" +  DateUtil.getDateStr2(map.get("statDate")+"","yyyy-MM-dd HH:00:00")
					+ "缺漏业务数据。";
			System.out.println(desc);
			ret.setChineseDesc(desc);
			lookList.add(ret);
		}
		
		return lookList;
			
	}

}
class SendMailThread2 extends Thread{
	List<Look> contentTpsList = null;
	public SendMailThread2(List<Look> contentTpsList){
		this.contentTpsList =  contentTpsList;
	}
	public void run(){
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			System.out.println("邮件发送开始！");
			String aSubject="TPS缺漏告警("+DateUtil.format(new Date(),"yyyy-MM-dd")+")";
			String aHtml = 	"<html>" +
			"你好："
			+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;TPS出现缺漏告警，请登录【<a href=\"http://211.139.136.107:8080/vdes\">增值业务多维度保障系统-系统管理维-系统告警</a>】查看。"
			+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;今天出现TPS数据缺漏的有如下网元：";
			
			StringBuffer sb = new StringBuffer();
			for(Look look:contentTpsList){
				sb.append("<br/>"+look.getChineseDesc());
			}
			aHtml+=sb.toString()+"</html>";
			LinkedHashMap map= new LinkedHashMap();
			ConfigureUtil cfgUtil = new ConfigureUtil();
			String mail = cfgUtil.getValue("mail");
			String[] mailAddress = mail.split(",");
			for(String oneMail:mailAddress){
				if(oneMail.length()>0&&oneMail.indexOf("|")!=-1){
					map.put(oneMail.split("\\|")[0],oneMail.split("\\|")[1]);
				}
				
			}
				
			 Mail.send(aSubject, aHtml, map);
			System.out.println("邮件发送结束！");			
	  }
}
