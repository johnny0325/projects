package pro.vdes.sync;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import pro.vdes.task.TaskMail;
import pro.vdes.task.TaskMailBehavior;
/**
 * �ֻ�����ϵͳ���ֻ�������������������Ϊ����
 * MainSync_mmds_mail.java
 * @author DengJianhua
 * Sep 21, 2010  11:46:37 AM
 *
 */
public class MainSync_mmds_mail {
	private static Logger log = Logger.getLogger(MainSync_mmds_mail.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);//���쿪ʼִ��
		calendar.set(Calendar.HOUR_OF_DAY, 5);//ԭ����11
		calendar.set(Calendar.MINUTE, 20);
		calendar.set(Calendar.SECOND, 0);

		Timer vdesTimer = new Timer();
		//�ֻ������������ݲɼ�
		final TaskMail taskMail=new TaskMail();
		
		//�ֻ�����������Ϊ����
		final TaskMailBehavior taskMailBehavior=new TaskMailBehavior();
		log.info("------------>start mmds_Mail");
		TimerTask taskMailRun=new TimerTask(){
			public void run() {
				taskMail.doSync();
				log.info("------------>end mmds_taskMail");
				
				taskMailBehavior.doSync();//��Ϊ����
				
				log.info("------------>end mmds_taskMailBehavior");
			}
		};
		vdesTimer.scheduleAtFixedRate(taskMailRun, calendar.getTime(),24 * 60 * 60 * 1000);//ÿ����һ��,

	}

}
