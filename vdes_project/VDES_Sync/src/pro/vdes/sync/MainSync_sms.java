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
					log.info("���˹���FTP�������ݽ�ѹ");
					taskMBWSMSHandle.doSync();//���˹���FTP�������ݽ�ѹ
					//taskMBWSMSHandle.doStatics();//���ͳ�Ʋ�������
					
					log.info("�ۺϷ�������");
					taskMBWCOMPLEX.doSync();//�ۺϷ�������
					log.info("��Ϊ����-�������-�����Ĳ�ͬ����");
					taskMBWBehavior.doSync();//��Ϊ����-�������-�����Ĳ�ͬ����

				}
			};
		vdesTimer.schedule(taskMBWSMSHandleRun,1000, 60* 60 * 1000);
		
		
		//����ÿ�������Ĳ�ͬ������Ŀ
		TimerTask taskWAPFilterRun = new TimerTask() {
			public void run() {
				taskMBWBehavior.dostatic();//�ò������ԭʼ�������㲡���Ĳ�ͬ���У�

			}
		};
		 vdesTimer.scheduleAtFixedRate(taskWAPFilterRun,calendar.getTime(), 24*60* 60 * 1000);
		
		


	}
}
