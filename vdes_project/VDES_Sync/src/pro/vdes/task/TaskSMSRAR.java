/**
 * TaskSMSRAR.java 2010-6-30 ÏÂÎç12:36:38
 */
package pro.vdes.task;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;

import common.util.CfgUtil;
import common.util.RarUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskSMSRAR {
	private static Logger log = Logger.getLogger(TaskSMSRAR.class);
	private static String ftpconf = CfgUtil.getValue("ftpconf")+"/mbw/sms/SMS_0625.txt~";
	private void rename(){
		
	}
	public void unrar(){
		String[] filenames = getUnrarFiles();
		for (String fileName : filenames) {
			handle(fileName);
			rename();
		}
		
	}
	/**
	 * @param fileName
	 */
	private void handle(String fileName) {
		// TODO Auto-generated method stub
		log.info(fileName);
		RarUtil.unRARFile(ftpconf+"/"+fileName, ftpconf);
		
	}
	private String[] getUnrarFiles(){
		File dir = new File(ftpconf);
		String [] filenames = dir.list(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if(name.startsWith("SMS_")&&name.endsWith("rar")){
					return true;
				}
				return false;
			}
			
		});
		return filenames;
	}
	public void doSync(){
		unrar();
	}
	public static void main(String[] argv){
		new TaskSMSRAR().doSync();
	}
}
