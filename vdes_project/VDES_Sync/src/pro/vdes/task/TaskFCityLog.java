/**
 * TaskFCityLog.java 2009-6-25 下午02:50:47
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

import com.moc.idgenerator.api.IIDGenerator;
import com.moc.idgenerator.api.IdGeneratorService;
import common.db.CommonDao;
import common.util.ConfigureUtil;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskFCityLog{
	private static Logger log = Logger.getLogger(TaskFCityLog.class);
	
	public void doSync(){
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) createDate from bpw_mms_fcitylog order by createDate desc");
			String currentDay = null;
			if(rs.next()){
				currentDay = DateUtil.formatDate(rs.getTimestamp(1), "yyyyMMdd");
			}else{
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDay = DateUtil.formatDate(c.getTime(), "yyyyMMdd");			
			}
			currentDay = nextDay(currentDay,-1);//如果对三天的数据进行同步；如目前是15号，则要对13号,14号,15号都同步；
			String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
			while(currentDay.compareTo(today)<0){
				deleteDayLog("=",currentDay,0);
				generateFCityLog(currentDay);
				currentDay = nextDay(currentDay,1);
				
				
			}
		} catch (SQLException e) {
			log.equals(e);
		} catch (Exception e) {
			log.equals(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
	}
	
	public void existsData(){
		Date max = maxStatDate();
		if (max != null){
			deleteDayLog(">",DateUtil.formatDate(max,"yyyy-MM-dd"),30);
		}
		
		
	}
	
	private Date maxStatDate(){
		Date max = null;
		log.info("existsData");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top(1) createDate from bpw_mms_fcitylog  order by createDate desc");
			if(rs.next()){
				max = rs.getDate("createDate");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.equals(e);
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.equals(e);
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		return max;
		
	}

	public boolean deleteDayLog(String oper,String day, int num){
		CommonDao dao = null;
		boolean flag = false;
		try {
			dao = new CommonDao();
			
			String sql1 = "delete from bpw_mms_fcitylog_code where parentid in " +
					"(select id from bpw_mms_fcitylog where datediff(d,createDate,'"+day+"')"+oper+" "+num+" )";
			String sql2 ="delete from bpw_mms_fcitylog where datediff(d,createDate,'"+day+"')"+oper+" "+num;
			
			dao.begin();
			dao.execute(sql1, null);
			dao.execute(sql2, null);
			dao.commit();
			flag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.equals(e);
			log.error(e);
		} finally{
			try {
				if(dao!=null) dao.close();			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.equals(e);
				log.error(e);
			}
		}
		return flag;
	}
	// 当天fcity_log_20090623.txt装换到sqlserver数据库
	private void generateFCityLog(String currentDay) {
		System.out.println("generateFCityLog");
		CommonDao dao = null;
		try {
			String syncUrlRoot = new ConfigureUtil().getValue("syncUrlRoot");
			URL url = new URL(syncUrlRoot +"fcity.log." + currentDay);
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			// 连接指定的网络资源
			httpUrl.connect();
			// 获取网络输入流
			BufferedInputStream bis = new BufferedInputStream(httpUrl
					.getInputStream());
			InputStreamReader reader = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			int i=0;
			final int LEN = 20;
			while ((line = br.readLine()) != null) {
				if(dao==null||i++%LEN==0){
					dao = new CommonDao();
				}
				if (line.length() > 0) {
					FCityLog f = handle(line);
					insertFCityLog(f,currentDay,dao);
				}
				if(i%LEN==0){
					try {
						if(dao!=null) dao.close();			
					} catch (Exception e) {
						log.error(e);
						// TODO Auto-generated catch block
						log.equals(e);
					}
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.equals(e);
			log.error(e);
		}finally{
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.equals(e);
				log.error(e);
				
			}
		}

	}
	private void insertFCityLog(FCityLog f,String currentDay,CommonDao dao){
		
		try {
			List params = new ArrayList();
			String sql = "insert into bpw_mms_fcitylog(id,statDate,city,status,count,createDate) values(?,?,?,?,?,?)";
			String sql_code = "insert into bpw_mms_fcitylog_code(parentid,mobile)values(?,?)";
			
			
			dao.begin();
			List<Status> statusList = f.getStatus();
			IIDGenerator idgen = IdGeneratorService.getIdGenterator();
			for(int i=0;i<statusList.size();i++){
				
				//insert主表(bpw_mms_fcitylog);				
				String id = idgen.getNextId();
				
				params.add(id);
				params.add(myDay(f.getStatDate()));
				params.add(f.getCity());
				params.add(statusList.get(i).getStatus());
				params.add(statusList.get(i).getCount());			
				params.add(myDay(currentDay));
				
				//log.info(myDay(f.getStatDate())+"|"+f.getCity()+"|"+f.getStatus().get(i).getStatus()+"|"+f.getStatus().get(i).getCount()+"|"+currentDay);
				dao.execute(sql, params);
				params.clear();
								
				//insert从表(bpw_mms_fcitylog_code);	
				String[] mobileArr = statusList.get(i).getMobile().split("\\|");
				
				for(int j=0;j<mobileArr.length;j++){
					if(mobileArr[j].length()>0){
						params.add(id);
						params.add(mobileArr[j]);
						dao.execute(sql_code, params);
						params.clear();
					}	
				}
			}
			dao.commit();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.equals(e);
			log.error(e);
		} 
		
	}

	private FCityLog handle(String line) {
		FCityLog f = null;
		String[] s = line.split("\\*");
		if (s.length > 0 && s[0].indexOf(":") != -1) {
			f = new FCityLog();
			f.setStatDate(s[0].split(":")[0]);
			f.setCity(s[0].split(":")[1]);
		}
		if (s.length > 1) {
			for (int i = 1; i < s.length; i++) {

				Pattern pattern = Pattern.compile("(\\d*)<(\\d*)>(.*)");
				Matcher matcher = pattern.matcher(s[i]);
				// log.info("s["+i+"]"+s[i]);
				if (matcher.find()) {
					Status status = new Status();
					status.setStatus(matcher.group(1));
					status.setCount(matcher.group(2));
					status.setMobile(matcher.group(3));
					f.getStatus().add(status);
				}

			}

		}
		return f;

	}
	private String nextDay(String currentDate,int next){
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0,4));
		int m = Integer.parseInt(currentDate.substring(4,6));
		int d = Integer.parseInt(currentDate.substring(6,8));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m-1);
		c.set(Calendar.DATE, d+next);
		
		
		return DateUtil.formatDate(c.getTime(), "yyyyMMdd");
		
	}
	private String myDay(String currentDate){
		Calendar c = Calendar.getInstance();
		int y = Integer.parseInt(currentDate.substring(0,4));
		int m = Integer.parseInt(currentDate.substring(4,6));
		int d = Integer.parseInt(currentDate.substring(6,8));
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m-1);
		c.set(Calendar.DATE, d);
		
		
		return DateUtil.formatDate(c.getTime(), "yyyy-MM-dd");
		
	}
}

class FCityLog {
	private String statDate;

	private String city;

	private List<Status> status = new ArrayList();

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the statDate
	 */
	public String getStatDate() {
		return statDate;
	}

	/**
	 * @param statDate
	 *            the statDate to set
	 */
	public void setStatDate(String statDate) {
		this.statDate = statDate;
	}

	/**
	 * @return the status
	 */
	public List<Status> getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(List<Status> status) {
		this.status = status;
	}

}

class Status {
	private String status;

	private String count;

	private String mobile;

	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile
	 *            the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
