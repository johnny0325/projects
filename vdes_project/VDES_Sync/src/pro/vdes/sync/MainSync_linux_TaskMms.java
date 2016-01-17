package pro.vdes.sync;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import pro.vdes.task.TaskMBWSMS;
import pro.vdes.task.TaskMms;

public class MainSync_linux_TaskMms {
	public static void main(String args[]){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		final TaskMms  taskMms = new TaskMms();
		Timer vdesTimer = new Timer();

		TimerTask taskMmsRun = new TimerTask() {
			public void run() {
				taskMms.doSync();

			}
		};
		 vdesTimer.schedule(taskMmsRun,1000, 15 * 60 * 1000);
	}
}
