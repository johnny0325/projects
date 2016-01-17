package pro.vdes.sync;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import pro.vdes.task.TaskBill;

public class MainSync_bill {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Timer vdesTimer = new Timer();
		final TaskBill taskBill = new TaskBill();
		TimerTask taskBillRun = new TimerTask() {
			public void run() {
				taskBill.doSync();
			}
		};
		vdesTimer.scheduleAtFixedRate(taskBillRun, calendar.getTime(),
				24 * 60 * 60 * 1000);
	}
}
