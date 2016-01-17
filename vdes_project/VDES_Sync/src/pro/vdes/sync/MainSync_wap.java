package pro.vdes.sync;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import pro.vdes.task.ITaskTXT;
import pro.vdes.task.TaskMBWMS;
import pro.vdes.task.TaskMBWSP;
import pro.vdes.task.TaskMBWUA;

public class MainSync_wap {

	private static Logger log = Logger.getLogger(MainSync_wap.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 3);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Timer vdesTimer = new Timer();
		final ITaskTXT sp = new TaskMBWSP();
		final ITaskTXT ms = new TaskMBWMS();
		final ITaskTXT ua = new TaskMBWUA();
		TimerTask run = new TimerTask() {
			public void run() {
				sp.doSync();
				ua.doSync();
				ms.doSync();
			}
		};
		// vdesTimer.schedule(run,1000, 5* 60 * 1000);
		vdesTimer.scheduleAtFixedRate(run, calendar.getTime(),
				24 * 60 * 60 * 1000);

	}
}
