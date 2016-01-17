package pro.vdes.sync;

import java.util.Timer;
import java.util.TimerTask;

import pro.vdes.task.TaskCheckReport;

public class MainSync_TaskCheckReport {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Timer vdesTimer = new Timer();
		final TaskCheckReport taskCheckReport = new TaskCheckReport();
		TimerTask taskCheckReportRun = new TimerTask() {
			public void run() {
				taskCheckReport.generateCheckReport();
			}
		};
		vdesTimer.schedule(taskCheckReportRun,1000, 60* 60 * 1000);
	}
}
