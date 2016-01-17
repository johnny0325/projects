package pro.vdes.sync;


import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import pro.vdes.task.AcitiveWarmHandle;
import pro.vdes.task.BusinessWarmHandle;
import pro.vdes.task.NewDataHandle;
import pro.vdes.task.TaskCheckReport;
import pro.vdes.task.TaskDNS;
import pro.vdes.task.TaskDNSFlowmon;
import pro.vdes.task.TaskFCityLog;
import pro.vdes.task.TaskMMSCityRadio;
import pro.vdes.task.TaskMMSSP;
import pro.vdes.task.TaskMtwBusiness;
import pro.vdes.task.TaskQuery;
import pro.vdes.task.TaskRange;
import pro.vdes.task.TaskValueOfTable;
import pro.vdes.task.TaskWAPTPSExcel;
import pro.vdes.task.TaskWapSpread;
import pro.vdes.task.TaskWapTpsReport;
import pro.vdes.task.TaskWarm;

public class MainSync {

	private static Logger log = Logger.getLogger(MainSync.class);
	private static Set<String> currentXmlSet = new HashSet<String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//晚上一点钟;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(Calendar.HOUR_OF_DAY, 1);
		calendar2.set(Calendar.MINUTE, 0);
		calendar2.set(Calendar.SECOND, 0);
		
		Calendar calendar5 = Calendar.getInstance();
		calendar2.set(Calendar.HOUR_OF_DAY, 5);
		calendar2.set(Calendar.MINUTE, 0);
		calendar2.set(Calendar.SECOND, 0);
		
		Calendar calendar9 = Calendar.getInstance();
		calendar9.set(Calendar.HOUR_OF_DAY, 9);
		calendar9.set(Calendar.MINUTE, 0);
		calendar9.set(Calendar.SECOND, 0);

		Timer vdesTimer = new Timer();
		
		//拨测告警。 ADD BY WINDSON 2009-12-10放入生产区
		final TaskMtwBusiness taskMtw = new TaskMtwBusiness();
		TimerTask taskValueOftaskMtw = new TimerTask() {
			public void run() {
			log.info("taskMtw.doSync()-------------------------");
				taskMtw.doSync();//采集业务
				log.info("taskMtw.generateWarm()-------------------------");
				taskMtw.generateWarm();//分析告警
				
				new NewDataHandle().handle();//统计告警
			}
		};
		vdesTimer.schedule(taskValueOftaskMtw,1000, 5* 60 * 1000);
		
		final TaskDNSFlowmon taskDNSFlowmon = new TaskDNSFlowmon();
		TimerTask taskDNSFlowmonRun = new TimerTask() {
			public void run() {
				taskDNSFlowmon.doSync();
				taskDNSFlowmon.generateWarm();
				
			}
		};
		vdesTimer.schedule(taskDNSFlowmonRun,1000, 60* 60 * 1000);
		
		final TaskWarm taskWarm = new TaskWarm();
		TimerTask taskWarmRun = new TimerTask() {
			public void run() {
				taskWarm.generateWarmActive();
				taskWarm.generateWarmDay();
				new AcitiveWarmHandle().handle();//网元激活告警分析 add by WINDSON
				//taskWarm.generateWarmDayStatus();
			}

		};
		//生产环境；
		vdesTimer.schedule(taskWarmRun, 1000,  5*60 * 1000);
		
		TimerTask taskWarmRun2 = new TimerTask() {
			public void run() {
				taskWarm.generateWarmDayHistory();
			}

		};
		//生产环境；
		vdesTimer.scheduleAtFixedRate(taskWarmRun2, calendar.getTime(),24 * 60 * 60 * 1000);


		 
		 


/*		final TaskCheckReport taskCheckReport = new TaskCheckReport();
		TimerTask taskCheckReportRun = new TimerTask() {
			public void run() {
				taskCheckReport.generateCheckReport();

			}

		};
		vdesTimer.schedule(taskCheckReportRun, calendar2.getTime(),  24 * 60 * 60 * 1000);*/


		// 彩信同步(和对方数据库同步接口程序)和将本日的数据从周表到月表
/*		final TaskInterface taskInterface_ne = new TaskInterface("ne");
		final TaskInterface taskInterface_city = new TaskInterface("city");
		TimerTask taskInterfaceRun = new TimerTask() {
			public void run() {
				taskInterface_ne.syncHourData();
				taskInterface_city.syncHourData();
				
				taskInterface_ne.doSyncWeek2Month();
				taskInterface_city.doSyncWeek2Month();

			}

		};
		
		//每个小时的第35分钟;
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(Calendar.MINUTE, 35);
		calendar2.set(Calendar.SECOND, 0);
		vdesTimer.scheduleAtFixedRate(taskInterfaceRun, calendar2.getTime(), 60* 60 * 1000);*/
		//vdesTimer.schedule(taskInterfaceRun, 3000, 60* 60 * 1000);
		
		
		//彩信晚上一点钟夜间数据处理		
		//1:删除周表>7天的数据
		//2:删除月表>31天的数据
/*		TimerTask taskInterfaceRun2 = new TimerTask() {
			public void run() {				
				taskInterface_ne.deleteBeforeDateInWeek();
				taskInterface_ne.deleteBeforDateInMonth();
				
				
				taskInterface_city.deleteBeforeDateInWeek();
				taskInterface_city.deleteBeforDateInMonth();
			}

		};
		vdesTimer.scheduleAtFixedRate(taskInterfaceRun2, calendar.getTime(),24 * 60 * 60 * 1000);*/
		//vdesTimer.scheduleAtFixedRate(taskInterfaceRun2, 1000,5 * 60 * 1000);
		
		//mms_sp同步数据;
/*		final TaskSp taskSp = new TaskSp();
		TimerTask taskSpRun = new TimerTask() {
			public void run() {
				taskSp.doSync();

			}

		};
		vdesTimer.schedule(taskSpRun, 1000,5 * 60 * 1000);
		
		
		TimerTask taskSpRun2 = new TimerTask() {
			public void run() {
				taskSp.deleteBeforDateInMonth();

			}

		};
		vdesTimer.scheduleAtFixedRate(taskSpRun2,  calendar.getTime(),24 * 60 * 60 * 1000);*/
		//vdesTimer.schedule(taskSpRun2, 1000,5 * 60 * 1000);
		
		
		//早上八点钟;
		Calendar eightClock = Calendar.getInstance();
		eightClock.set(Calendar.HOUR_OF_DAY, 8);
		eightClock.set(Calendar.MINUTE, 0);
		eightClock.set(Calendar.SECOND, 0);
		
		final TaskFCityLog taskFCityLog = new TaskFCityLog();
		TimerTask TaskFCityLogRun = new TimerTask() {
			public void run() {
				taskFCityLog.doSync();
			}

		};
		vdesTimer.scheduleAtFixedRate(TaskFCityLogRun, eightClock.getTime(),24 * 60 * 60 * 1000);
		
		//高可配的查询并生成相关的线程 add by aiyan 2009-07-22
		final TaskQuery taskQurey = new TaskQuery(vdesTimer);
		TimerTask taskQureyRun = new TimerTask() {
			public void run() {
				taskQurey.query();
				taskQurey.doSyncByQuery();	
			}
		};
		vdesTimer.schedule(taskQureyRun,1000, 1*60 * 1000);
		
//		早上三点
		/*Calendar threeClock = Calendar.getInstance();
		threeClock.set(Calendar.HOUR_OF_DAY, 3);
		threeClock.set(Calendar.MINUTE, 0);
		threeClock.set(Calendar.SECOND, 0);
		
		//首页彩信成功率呈现中，地市，号码，网元关系同步
		final TaskTelNecode taskTelNecode = new TaskTelNecode();
		TimerTask taskTelNecodeRun = new TimerTask() {
		public void run() {
			taskTelNecode.telNecodeSync();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskTelNecodeRun,threeClock.getTime(), 24 * 60 * 60 * 1000);*/

		
		
		final TaskDNS taskDNS = new TaskDNS("bpw_wap_dns",2,31);
		TimerTask taskDNSRun = new TimerTask() {
		public void run() {
				//taskDNS.deleteData();
				taskDNS.generateDNS();
			}
		};
		vdesTimer.schedule(taskDNSRun,1000, 5* 60 * 1000);
		
		TimerTask taskDNSRun2 = new TimerTask() {
			public void run() {
				taskDNS.existsData();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskDNSRun2,calendar2.getTime(), 24*60* 60 * 1000);
		
		
		final TaskWapSpread taskWapSpread = new TaskWapSpread("bpw_wap_ip",0,31);
		TimerTask taskWapSpreadRun = new TimerTask() {
			public void run() {
				taskWapSpread.doSync();
			}
		};
		vdesTimer.schedule(taskWapSpreadRun,1000, 60* 60 * 1000);
		
		
		final TaskWapSpread taskWapSpreadByCity = new TaskWapSpread("bpw_wap_city",0,31);
		TimerTask taskWapSpreadByCityRun = new TimerTask() {
			public void run() {
				taskWapSpreadByCity.doSync();
			}
		};
		vdesTimer.schedule(taskWapSpreadByCityRun,1000, 60* 60 * 1000);
		
		final TaskWapSpread taskWapSpreadByGGSN = new TaskWapSpread("bpw_wap_ggsn",0,31);
		TimerTask taskWapSpreadByGGSNRun = new TimerTask() {
			public void run() {
				taskWapSpreadByGGSN.doSync();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskWapSpreadByGGSNRun,1000, 60* 60 * 1000);

		final TaskMMSSP taskMMSSP = new TaskMMSSP();
		TimerTask taskMMSSPRun = new TimerTask() {
			public void run() {
			
				taskMMSSP.doSync();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskMMSSPRun,calendar9.getTime(), 24*60* 60 * 1000);
		
		
		TimerTask taskExistsDataRun = new TimerTask(){
			public void run(){
				taskFCityLog.existsData();
				taskWapSpread.existsData();
				taskWapSpreadByCity.existsData();
				taskMMSSP.existsData();
				
				taskDNSFlowmon.deleteBeforDateInMonth();
				taskMtw.deleteBeforDate();//add by aiyan 2009-12-10
				taskMtw.deleteBeforDateWarm(); //add by aiyan 2009-12-15
			}
			
		};
		vdesTimer.scheduleAtFixedRate(taskExistsDataRun,calendar2.getTime(), 24*60* 60 * 1000);
		
		
		final TaskRange taskRange = new TaskRange();
		TimerTask taskRangeRun = new TimerTask() {
			public void run() {
				taskRange.doSync();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskRangeRun,calendar2.getTime(), 24*60* 60 * 1000);
		
		//地市实时成功率，由ICARE的文件过来的。 ADD BY AIYAN 2009-11-20放入生产区
		final TaskMMSCityRadio taskMMSCityRadio = new TaskMMSCityRadio();
		TimerTask taskMMSCityRadioRun = new TimerTask() {
			public void run() {
			
				taskMMSCityRadio.doSync();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskMMSCityRadioRun,calendar2.getTime(), 15* 60 * 1000);

		//对表中某个值做告警。 ADD BY AIYAN 2009-11-20放入生产区
		final TaskValueOfTable taskValueOfTable = new TaskValueOfTable();
		TimerTask taskValueOfTableRun = new TimerTask() {
			public void run() {
			
				taskValueOfTable.doWarm("valueOfTable.xml");
				new BusinessWarmHandle().handle();//业务告警分析 add  by WINDSON
			}
		};
		vdesTimer.schedule(taskValueOfTableRun,1000, 60* 60 * 1000);
		
		TimerTask taskValueOfTableRun2 = new TimerTask() {
			public void run() {
				taskValueOfTable.doWarm("valueOfTable_tps.xml");
				new BusinessWarmHandle().handle();//业务告警分析 add  by AIYAN 2010-03-19
			}
		};
		vdesTimer.scheduleAtFixedRate(taskValueOfTableRun2,eightClock.getTime(), 24*60*60*1000);
		
		//VDES的用户信息纬里面的WAP月报表在这里每个月的3号后生产出来。ADD BY AIYAN 2010-03-12
		final TaskWAPTPSExcel taskWAPExcel = new TaskWAPTPSExcel();
		TimerTask taskWAPExcelRun = new TimerTask() {
			public void run() {
				taskWAPExcel.generateDBExcel();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskWAPExcelRun,calendar5.getTime(), 24*60* 60 * 1000);
		
		final TaskWapTpsReport taskWapTpsReport = new TaskWapTpsReport();
		TimerTask taskWapTpsReportRun = new TimerTask() {
			public void run() {
				taskWapTpsReport.doSync();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskWapTpsReportRun,calendar5.getTime(), 24*60* 60 * 1000);
	}
}
