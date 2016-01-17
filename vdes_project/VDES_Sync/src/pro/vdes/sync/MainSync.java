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
		//����һ����;
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
		
		//����澯�� ADD BY WINDSON 2009-12-10����������
		final TaskMtwBusiness taskMtw = new TaskMtwBusiness();
		TimerTask taskValueOftaskMtw = new TimerTask() {
			public void run() {
			log.info("taskMtw.doSync()-------------------------");
				taskMtw.doSync();//�ɼ�ҵ��
				log.info("taskMtw.generateWarm()-------------------------");
				taskMtw.generateWarm();//�����澯
				
				new NewDataHandle().handle();//ͳ�Ƹ澯
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
				new AcitiveWarmHandle().handle();//��Ԫ����澯���� add by WINDSON
				//taskWarm.generateWarmDayStatus();
			}

		};
		//����������
		vdesTimer.schedule(taskWarmRun, 1000,  5*60 * 1000);
		
		TimerTask taskWarmRun2 = new TimerTask() {
			public void run() {
				taskWarm.generateWarmDayHistory();
			}

		};
		//����������
		vdesTimer.scheduleAtFixedRate(taskWarmRun2, calendar.getTime(),24 * 60 * 60 * 1000);


		 
		 


/*		final TaskCheckReport taskCheckReport = new TaskCheckReport();
		TimerTask taskCheckReportRun = new TimerTask() {
			public void run() {
				taskCheckReport.generateCheckReport();

			}

		};
		vdesTimer.schedule(taskCheckReportRun, calendar2.getTime(),  24 * 60 * 60 * 1000);*/


		// ����ͬ��(�ͶԷ����ݿ�ͬ���ӿڳ���)�ͽ����յ����ݴ��ܱ��±�
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
		
		//ÿ��Сʱ�ĵ�35����;
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(Calendar.MINUTE, 35);
		calendar2.set(Calendar.SECOND, 0);
		vdesTimer.scheduleAtFixedRate(taskInterfaceRun, calendar2.getTime(), 60* 60 * 1000);*/
		//vdesTimer.schedule(taskInterfaceRun, 3000, 60* 60 * 1000);
		
		
		//��������һ����ҹ�����ݴ���		
		//1:ɾ���ܱ�>7�������
		//2:ɾ���±�>31�������
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
		
		//mms_spͬ������;
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
		
		
		//���ϰ˵���;
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
		
		//�߿���Ĳ�ѯ��������ص��߳� add by aiyan 2009-07-22
		final TaskQuery taskQurey = new TaskQuery(vdesTimer);
		TimerTask taskQureyRun = new TimerTask() {
			public void run() {
				taskQurey.query();
				taskQurey.doSyncByQuery();	
			}
		};
		vdesTimer.schedule(taskQureyRun,1000, 1*60 * 1000);
		
//		��������
		/*Calendar threeClock = Calendar.getInstance();
		threeClock.set(Calendar.HOUR_OF_DAY, 3);
		threeClock.set(Calendar.MINUTE, 0);
		threeClock.set(Calendar.SECOND, 0);
		
		//��ҳ���ųɹ��ʳ����У����У����룬��Ԫ��ϵͬ��
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
		
		//����ʵʱ�ɹ��ʣ���ICARE���ļ������ġ� ADD BY AIYAN 2009-11-20����������
		final TaskMMSCityRadio taskMMSCityRadio = new TaskMMSCityRadio();
		TimerTask taskMMSCityRadioRun = new TimerTask() {
			public void run() {
			
				taskMMSCityRadio.doSync();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskMMSCityRadioRun,calendar2.getTime(), 15* 60 * 1000);

		//�Ա���ĳ��ֵ���澯�� ADD BY AIYAN 2009-11-20����������
		final TaskValueOfTable taskValueOfTable = new TaskValueOfTable();
		TimerTask taskValueOfTableRun = new TimerTask() {
			public void run() {
			
				taskValueOfTable.doWarm("valueOfTable.xml");
				new BusinessWarmHandle().handle();//ҵ��澯���� add  by WINDSON
			}
		};
		vdesTimer.schedule(taskValueOfTableRun,1000, 60* 60 * 1000);
		
		TimerTask taskValueOfTableRun2 = new TimerTask() {
			public void run() {
				taskValueOfTable.doWarm("valueOfTable_tps.xml");
				new BusinessWarmHandle().handle();//ҵ��澯���� add  by AIYAN 2010-03-19
			}
		};
		vdesTimer.scheduleAtFixedRate(taskValueOfTableRun2,eightClock.getTime(), 24*60*60*1000);
		
		//VDES���û���Ϣγ�����WAP�±���������ÿ���µ�3�ź�����������ADD BY AIYAN 2010-03-12
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
