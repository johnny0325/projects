package pro.vdes.sync;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import pro.vdes.service.monitor.Look;
import pro.vdes.task.TaskLookStatus;

public class Main_LookStatus {
	
	private static Logger log = Logger.getLogger(Main_LookStatus.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		Timer vdesTimer = new Timer();
		
		//�賿һ��ʮ��
		Calendar oneClock = Calendar.getInstance();
		oneClock.set(Calendar.HOUR_OF_DAY, 1);
		oneClock.set(Calendar.MINUTE, 10);
		oneClock.set(Calendar.SECOND, 0);

		//���˵���ʮ����
		Calendar clock50 = Calendar.getInstance();
		clock50.set(Calendar.HOUR_OF_DAY, 8);
		clock50.set(Calendar.MINUTE, 50);
		clock50.set(Calendar.SECOND, 0);
		
		final TaskLookStatus taskLookStatus = new TaskLookStatus();
		TimerTask taskLookStatusRun = new TimerTask() {
			public void run() {
				//taskLookStatus.delete("1,2,3");
				List<Look> lookList = new ArrayList<Look>();

				List<Look> osList = taskLookStatus.lookOS();
				List<Look> indexList = taskLookStatus.lookIndex();			
				List<Look> dbList = taskLookStatus.lookDB();

				lookList.addAll(osList);
				lookList.addAll(indexList);
				lookList.addAll(dbList);
				taskLookStatus.genarateTXT(lookList,"t123.txt");//����ǰϵͳ�澯��¼��������״̬��TXT, �Թ�ICARE ʹ��
				taskLookStatus.genarateDB(lookList);//����ǰϵͳ�澯��¼��������״̬��SYS_lOOK
				
				taskLookStatus.genarateTXT("t123.txt","t4.txt");//�ϲ�txt;
			}
		};
		vdesTimer.schedule(taskLookStatusRun, 10000,10* 60 * 1000);
		

		TimerTask taskLookStatusRun2 = new TimerTask() {
			public void run() {		
				//taskLookStatus.delete("4");
				List<Look> dataList = taskLookStatus.lookData();
				taskLookStatus.genarateTXT(dataList,"t4.txt");//����ǰϵͳ�澯��¼��������״̬��TXT, �Թ�ICARE ʹ��
				taskLookStatus.genarateDB(dataList);//����ǰϵͳ�澯��¼��������״̬��SYS_lOOK
			}
		};
		vdesTimer.schedule(taskLookStatusRun2, oneClock.getTime(),60 * 60 * 1000);
		
		
		TimerTask taskLookStatusRun3 = new TimerTask() {
			public void run() {		
				//��¼��TPSֵΪ�ղ���ȱ©�澯 ��ȷ��Сʱ add by aiyan 2010-03-01
				 taskLookStatus.doWarn();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskLookStatusRun3, clock50.getTime(),60 * 60 * 1000);

	}

}
