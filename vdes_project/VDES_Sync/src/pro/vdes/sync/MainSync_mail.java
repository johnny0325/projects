package pro.vdes.sync;

import java.util.Timer;
import java.util.TimerTask;

import pro.vdes.task.TaskIndicators;
import pro.vdes.task.TaskIndicatorsvalue;

public class MainSync_mail {
	public static void main(String args[]){
		Timer vdesTimer = new Timer();
		
		//每15分钟扫描监测数据文件目录，读取增量的监测数据文件解析入库
		final TaskIndicatorsvalue taskIndicatorsvalue = new TaskIndicatorsvalue();
		TimerTask taskIndicatorsvalueRun = new TimerTask() {
			public void run() {
				taskIndicatorsvalue.doSync();
			}
		};
		vdesTimer.schedule(taskIndicatorsvalueRun, 5000,15 * 60 * 1000);//15分钟做一次
		
		//每1小时扫描维度数据文件目录，读取增量维度文件解析入库
		final TaskIndicators taskIndicators = new TaskIndicators();
		TimerTask TaskIndicatorsRun = new TimerTask() {
			public void run() {
				taskIndicators.doSync();
			}
		};
		vdesTimer.schedule(TaskIndicatorsRun, 5000,60 * 60 * 1000);//1小时做一次
	}
}
