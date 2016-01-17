/**
 * 
 */
package pro.vdes.mms;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import pro.vdes.mms.Stater.StatResult;
import pro.vdes.task.TaskCheckReport;

/**
 * @author QiuSH
 * @version 1.0
 * 
 */
public class StatTask extends TimerTask {
	private static Logger log = Logger.getLogger(StatTask.class);
	
	private static final int DEFAULT_PERIOD = 10;

	private Date initTimestamp = null;

	private int period = DEFAULT_PERIOD;

	private SyncTimestampDao dao = null;

	/**
	 * 
	 */
	public StatTask() {
		dao = new SyncTimestampDao();
	}

	/**
	 * @param initTimestamp
	 */
	public StatTask(Date initTimestamp) {
		this(initTimestamp, DEFAULT_PERIOD);
	}

	/**
	 * @param initTimestamp
	 * @param period
	 */
	public StatTask(Date initTimestamp, int period) {
		this.initTimestamp = initTimestamp;
		dao = new SyncTimestampDao(initTimestamp);

		this.period = period;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		Date lastTime = dao.getTimestamp("mms");
		
		Date current = getCurrent(lastTime);
		Calendar end = Calendar.getInstance();
		end.setTime(current);
		end.add(Calendar.MINUTE, -1);
		Date endTime = end.getTime();

		Stater stater = new Stater(lastTime, endTime);
		try {
			stater.doSync();
			Map<String, StatResult> statResults = stater.doStat();
			stater.doSave(statResults);
			stater.doSaveToDay(statResults);

			stater.doClearTmp();
			stater.doClearStatResult(7);
			stater.doClearStatDayResult(30);

		} catch (SQLException e) {
			log.error(e);
		}

		dao.updateTimestamp(current, "mms");
	}

	/**
	 * @param startTime TODO
	 * @return
	 */
	private Date getCurrent(Date startTime) {
		Calendar current = Calendar.getInstance();
		if (initTimestamp != null) {
			current.setTime(initTimestamp);
			if(startTime.compareTo(initTimestamp)>0){
				current.setTime(startTime);
			}
			current.add(Calendar.MINUTE, period);
			initTimestamp = current.getTime();
		} else {
			current.set(Calendar.SECOND, 0);
			current.set(Calendar.MILLISECOND, 0);
		}
		return current.getTime();
	}
}
