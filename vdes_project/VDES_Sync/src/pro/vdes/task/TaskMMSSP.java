/**
 * TaskMMSSP.java 2009-6-25 ����02:50:47
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
public class TaskMMSSP{
	private static Logger log = Logger.getLogger(TaskMMSSP.class);
	
	private final static String[] city=new String[]{"gz","sz","dg","fs","st","zh","hz","zs","jm","sg","mz","sw","yj","zj","mm","zq","qy","cz","jy","yf","hy","other"};
	
	public void doSync(){
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) statDate from bpw_mms_sp_end order by statDate desc");
			String currentDay = null;
			if(rs.next()){
				currentDay = DateUtil.formatDate(rs.getTimestamp(1), "yyyyMMdd");
			}else{
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -7);
				currentDay = DateUtil.formatDate(c.getTime(), "yyyyMMdd");			
			}
			currentDay = nextDay(currentDay,-2);//�������������ݽ���ͬ������Ŀǰ��15�ţ���Ҫ��13��,14��,15�Ŷ�ͬ����
			String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
			while(currentDay.compareTo(today)<0){
				deleteDay("=",currentDay,0);
				for(int i=0;i<city.length;i++){
					generateMMSSP(currentDay,city[i]);
				}
				
				currentDay = nextDay(currentDay,1);
				
				
			}
		} catch (SQLException e) {
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
	}
	
	public void existsData(){
		Date max = maxStatDate();
		if (max != null){
			deleteDay(">",DateUtil.formatDate(max,"yyyy-MM-dd"),31);
		}	
	}
	
	private Date maxStatDate(){
		Date max = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) statDate from bpw_mms_sp_end  order by statDate desc");
			if(rs.next()){
				max = rs.getDate("statDate");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		return max;
		
	}
	
	public boolean deleteDay(String oper,String day, int num){
		CommonDao dao = null;
		boolean flag = false;
		try {
			dao = new CommonDao();
			
			String sql1 = "delete from bpw_mms_sp_end_code where parentid in " +
					"(select id from bpw_mms_sp_end where datediff(d,statDate,'"+day+"')"+oper+" "+num+" )";
			String sql2 ="delete from bpw_mms_sp_end where datediff(d,statDate,'"+day+"')"+oper+" "+num;
			
			dao.begin();
			dao.execute(sql1, null);
			dao.execute(sql2, null);
			dao.commit();
			flag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		} finally{
			try {
				if(dao!=null) dao.close();			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
		return flag;
	}

	// ����sp.sz.20090902.txtװ����sqlserver���ݿ�
	public  void generateMMSSP(String currentDay,String city) {
		log.info("generateMMSSP");
		CommonDao dao = null;
		try {
			String mmsspUrl = new ConfigureUtil().getValue("mmsspUrl");
			URL url = new URL(mmsspUrl +"/sp."+city+"." + currentDay);
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			// ����ָ����������Դ
			httpUrl.connect();
			// ��ȡ����������
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
				MMSSP mmssp = handle(line);			
				if(mmssp!=null) insertMMSSP(mmssp,currentDay,city,dao);
				
				if(i%LEN==0){
					try {
						if(dao!=null) dao.close();			
					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.error(e);
					}
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}finally{
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
		log.info("end generateMMSSP");

	}
	private void insertMMSSP(MMSSP m,String currentDay,String city,CommonDao dao){
		try {
			List params = new ArrayList();
			String sql = "insert into bpw_mms_sp_end(id,enterprise,services,business,statDate,city,count,succCount) values(?,?,?,?,?,?,?,?)";
			String sql_code = "insert into bpw_mms_sp_end_code(parentid,status,count)values(?,?,?)";
			
			dao.begin();
			
			IIDGenerator idgen = IdGeneratorService.getIdGenterator();
			//insert����(bpw_mms_sp_end);				
			String id = idgen.getNextId();
			
			params.add(id);
			params.add(m.getEnterprise());
			params.add(m.getServices());
			params.add(m.getBusiness());
			params.add(myDay(currentDay));
			params.add(city);
			params.add(m.getCount());
			params.add(m.getSuccCount());
	
			
			//log.info(myDay(currentDay)+"|"+city+"|"+m..getStatus().get(i).getStatus()+"|"+f.getStatus().get(i).getCount()+"|"+currentDay);
			dao.execute(sql, params);
			params.clear();
							
			//insert�ӱ�(bpw_mms_sp_end_code);	
			//String[] mobileArr = statusList.get(i).getMobile().split("\\|");
			
			List<MMSSPCode> statusList = m.getStatus();
			for(int j=0;j<statusList.size();j++){
				params.add(id);
				params.add(statusList.get(j).getStatus());
				params.add(statusList.get(j).getCount());
				dao.execute(sql_code, params);
				params.clear();	
			}
			dao.commit();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}finally{
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
	}

	private MMSSP handle(String line) {
		MMSSP mmssp = null;
		
		Pattern pattern = Pattern.compile("(.*)# #(.*)");
		Matcher matcher = pattern.matcher(line);
		
		if (!matcher.find()) return null;
		else{
			//���lineΪ895234:10658300:101 # # 0300 # 60 # 0100 # 62 # 1000 # 49
			//s1Ϊ895234:10658300:101������
			//s2Ϊ0300 # 60 # 0100 # 62 # 1000 # 49������
			String[] s1 = matcher.group(1).split(":");
			String[] s2 = matcher.group(2).split("#");
			
			//��״̬��0400��ʱ��ҵ����
			//��״̬��Ϊ1000ʱ��ɹ���
			
			//s1�����鳤�ȹ̶�������ҪЩFORѭ����ֱ����д�� add by aiyan
			mmssp = new MMSSP();
			mmssp.setEnterprise(s1[0].trim());
			mmssp.setServices(s1[1].trim());
			mmssp.setBusiness(s1[2].trim());
			mmssp.setCount("0");//��ʼ��һ��
			mmssp.setSuccCount("0");//��ʼ��һ��
			
			//s2�������ǲ������ģ���ҪFORѭ�� add by aiyan
			for(int i=0;i<s2.length;i=i+2){
				MMSSPCode status = new MMSSPCode();
				status.setStatus(s2[i].trim());
				status.setCount(s2[i+1].trim());
				
				if(s2[i].trim().equals("0400")){
					mmssp.setCount(s2[i+1]);//ʵ�ʸ�ֵһ��
				}else if(s2[i].trim().equals("1000")){
					mmssp.setSuccCount(s2[i+1]);//ʵ�ʸ�ֵһ��
				}
				
				mmssp.getStatus().add(status);
			}
		}

		return mmssp;

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

class MMSSP {
	private String enterprise;

	private String services;
	
	private String business;
	
	private String count;
	
	private String succCount;

	private List<MMSSPCode> status = new ArrayList<MMSSPCode>();

	/**
	 * @return the business
	 */
	public String getBusiness() {
		return business;
	}

	/**
	 * @param business the business to set
	 */
	public void setBusiness(String business) {
		this.business = business;
	}

	/**
	 * @return the enterprise
	 */
	public String getEnterprise() {
		return enterprise;
	}

	/**
	 * @param enterprise the enterprise to set
	 */
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	/**
	 * @return the services
	 */
	public String getServices() {
		return services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(String services) {
		this.services = services;
	}
	
	
	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * @return the succCount
	 */
	public String getSuccCount() {
		return succCount;
	}

	/**
	 * @param succCount the succCount to set
	 */
	public void setSuccCount(String succCount) {
		this.succCount = succCount;
	}

	/**
	 * @return the status
	 */
	public List<MMSSPCode> getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(List<MMSSPCode> status) {
		this.status = status;
	}
	
	
	
}
class MMSSPCode {
	private String status;

	private String count;

	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}


