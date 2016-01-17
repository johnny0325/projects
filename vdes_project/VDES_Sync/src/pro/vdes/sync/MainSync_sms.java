package pro.vdes.sync;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import pro.vdes.task.TaskMBWBehavior;
import pro.vdes.task.TaskMBWCOMPLEX;
import pro.vdes.task.TaskMBWSMS;
import pro.vdes.task.TaskMBWSMSHandle;

public class MainSync_sms {

	private static Logger log = Logger.getLogger(MainSync_sms.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 13);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		final TaskMBWSMS  taskMBWSMS = new TaskMBWSMS();
		Timer vdesTimer = new Timer();

		TimerTask taskMBWSMSRun = new TimerTask() {
			public void run() {
				taskMBWSMS.doSync();

			}
		};
		 vdesTimer.schedule(taskMBWSMSRun,1000, 5* 60 * 1000);
		 
		 
		 final TaskMBWSMSHandle taskMBWSMSHandle = new TaskMBWSMSHandle();
		 final TaskMBWCOMPLEX taskMBWCOMPLEX = new TaskMBWCOMPLEX();
		 final TaskMBWBehavior taskMBWBehavior = new TaskMBWBehavior();
		 TimerTask taskMBWSMSHandleRun = new TimerTask() {
				public void run() {
					log.info("对人工的FTP短信数据解压");
					taskMBWSMSHandle.doSync();//对人工的FTP短信数据解压
					//taskMBWSMSHandle.doStatics();//这个统计不能用先
					
					log.info("综合分析数据");
					taskMBWCOMPLEX.doSync();//综合分析数据
					log.info("行为数据-病毒添加-病毒的不同主叫");
					taskMBWBehavior.doSync();//行为数据-病毒添加-病毒的不同主叫

				}
			};
		vdesTimer.schedule(taskMBWSMSHandleRun,1000, 60* 60 * 1000);
		
		
		//计算每个病毒的不同主叫数目
		TimerTask taskWAPFilterRun = new TimerTask() {
			public void run() {
				taskMBWBehavior.dostatic();//拿病毒库和原始话单计算病毒的不同主叫；

			}
		};
		 vdesTimer.scheduleAtFixedRate(taskWAPFilterRun,calendar.getTime(), 24*60* 60 * 1000);
		
		


	}
}
