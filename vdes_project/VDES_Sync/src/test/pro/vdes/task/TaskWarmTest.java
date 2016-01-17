/**
 * TaskWarmTest.java 2009-4-24 обнГ02:47:10
 */
package test.pro.vdes.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import pro.vdes.task.TaskWarm;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskWarmTest extends TestCase{
	private static Logger log = Logger.getLogger(TaskWarmTest.class);
	public void testA(){
		TaskWarm tw = new TaskWarm();
		tw.generateWarmActive();
	}

	

}
