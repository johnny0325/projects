package pro.vdes.sync;

import java.util.Timer;
import java.util.TimerTask;

import pro.vdes.task.TaskIndicators;
import pro.vdes.task.TaskIndicatorsvalue;

public class MainSync_mail {
	public static void main(String args[]){
		Timer vdesTimer = new Timer();
		
		//ÿ15����ɨ���������ļ�Ŀ¼����ȡ�����ļ�������ļ��������
		final TaskIndicatorsvalue taskIndicatorsvalue = new TaskIndicatorsvalue();
		TimerTask taskIndicatorsvalueRun = new TimerTask() {
			public void run() {
				taskIndicatorsvalue.doSync();
			}
		};
		vdesTimer.schedule(taskIndicatorsvalueRun, 5000,15 * 60 * 1000);//15������һ��
		
		//ÿ1Сʱɨ��ά�������ļ�Ŀ¼����ȡ����ά���ļ��������
		final TaskIndicators taskIndicators = new TaskIndicators();
		TimerTask TaskIndicatorsRun = new TimerTask() {
			public void run() {
				taskIndicators.doSync();
			}
		};
		vdesTimer.schedule(TaskIndicatorsRun, 5000,60 * 60 * 1000);//1Сʱ��һ��
	}
}
