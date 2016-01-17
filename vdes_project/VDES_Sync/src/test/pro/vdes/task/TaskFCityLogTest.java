/**
 * TaskFCityLogTest.java 2009-6-25 下午03:50:17
 */
package test.pro.vdes.task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskFCityLogTest extends TestCase{
	
	//[TaskFCityLogTest ] - 88 ---- 20090620:mz*3401<5>008613750593038|008615017803699|008613750533632|008615016273586|008613543240832|*4441<1>008613549110642|*4442<2>008613670877444|008613670786237|*3402<8>008613750546263|008613431808555|008615014551823|008615016261720|008613642517490|008613750562728|008615819030660|008613536719538|*4401<1>008613502535963|*	
	private static Logger log = Logger.getLogger(TaskFCityLogTest.class);
	public void testA(){
		//公司测试;
		try {
			URL url = new URL("http://localhost:8080/VDES/fcity_log_20090623.txt");
			HttpURLConnection httpUrl = (HttpURLConnection) url
					.openConnection();
			httpUrl.connect();
			// 获取网络输入流
			BufferedInputStream bis = new BufferedInputStream(httpUrl
					.getInputStream());
			InputStreamReader reader = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			int i=0;
			while ((line = br.readLine()) != null) {
				if(line.length()>0){
					log.info(++i+" ---- "+line);				
					FCityLog f = handle(line);
					log.info(f);
				}


			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testB(){
		String line = "20090620:mz*3401<5>008613750593038|008615017803699|008613750533632|008615016273586|008613543240832|*4441<1>008613549110642|*4442<2>008613670877444|008613670786237|*3402<8>008613750546263|008613431808555|008615014551823|008615016261720|008613642517490|008613750562728|008615819030660|008613536719538|*4401<1>008613502535963|*";
		handle(line);
	}
	private FCityLog handle(String line){
			FCityLog  f = null;
			String[] s = line.split("\\*");
			if(s.length>0&&s[0].indexOf(":")!=-1){
				f = new FCityLog();
				f.setStatDate(s[0].split(":")[0]);
				f.setCity(s[0].split(":")[1]);
			}
			if(s.length>1){
				for(int i=1;i<s.length;i++){
					
					Pattern pattern = Pattern.compile("(\\d*)<(\\d*)>(.*)");
					Matcher matcher = pattern.matcher(s[i]);
					log.info("s["+i+"]"+s[i]);
					if(matcher.find()){
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
	}
	class FCityLog{
		private String statDate;
		private String city;
		private List<Status>  status = new ArrayList();
		/**
		 * @return the city
		 */
		public String getCity() {
			return city;
		}
		/**
		 * @param city the city to set
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
		 * @param statDate the statDate to set
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
		 * @param status the status to set
		 */
		public void setStatus(List<Status> status) {
			this.status = status;
		}
		
	}
	class Status{
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
		 * @param count the count to set
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
		 * @param mobile the mobile to set
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
		 * @param status the status to set
		 */
		public void setStatus(String status) {
			this.status = status;
		}
		
	}
