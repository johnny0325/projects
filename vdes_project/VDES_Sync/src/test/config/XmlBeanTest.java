/**
 * XmlBeanTest.java 2009-7-7 ÏÂÎç04:28:07
 */
package test.config;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import sample.xmlbean.RootDocument.Root.Time;
import test.common.util.DBUtilTest;
import config.DocParse;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class XmlBeanTest extends TestCase{
	private static Logger log = Logger.getLogger(DBUtilTest.class);
	/*public static void main(String[] argv) throws XmlException, IOException{
		String file = "db_bpw_mms_ne_new.xml";
		file="db_bpw_mms_city_new.xml";
		file="db_bpw_mms_sp_new.xml";
		//file="bpw_mms_ne_statusCode.xml";
		//file="db_bpw_mms_ne_new_month.xml";
		//file="db_bpw_mms_city_new_month.xml";
		final DocParse docParse = new DocParse(file);
			Time time = docParse.getTime();
			Timer vdesTimer = new Timer();
			String start = time.getStart();
			String hour = start.substring(0,start.indexOf(":"));
			String minute = start.substring(start.indexOf(":")+1);
			Calendar c1 = Calendar.getInstance();
			c1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			c1.set(Calendar.MINUTE, Integer.parseInt(minute));
			c1.set(Calendar.SECOND, 0);
			
			final TaskEntity e1 = new TaskEntity(file);
			TimerTask t1 = new TimerTask() {
				public void run() {
					if(docParse.getSuffix()==null||docParse.getSuffix().equals("week")){
						e1.createTable();
						e1.doSync();
					}else if(docParse.getSuffix().equals("month")){
								e1.createTable();
								e1.doSyncWeek2Month();
					}
							
				}

			};
			vdesTimer.scheduleAtFixedRate(t1, c1.getTime(),getDelay(time.getRedo()));
	}*/
	
	private static long getDelay(String a){
		long r =1;
		String[] inta= a.split("\\*");
		for(String s:inta){
			r*=Long.parseLong(s);
		}
		return r;
	}

}
