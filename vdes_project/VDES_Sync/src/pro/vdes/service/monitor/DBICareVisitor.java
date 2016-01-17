/**
 * DBICareVisitor.java 2009-9-9 ÉÏÎç11:37:34
 */
package pro.vdes.service.monitor;

import org.apache.log4j.Logger;

import pro.vdes.task.TaskCheckReport;
import common.db.CommonDao;


/**
 * @author aiyan
 * @version 1.0
 *
 */
public class DBICareVisitor  implements Visitor {
	
	private static Logger log = Logger.getLogger(DBICareVisitor.class);
	/* (non-Javadoc)
	 * @see pro.vdes.service.monitor.Visitor#visit()
	 */
	public String visit() {
		// TODO Auto-generated method stub
		String ret = Constant.ERROR;
		CommonDao dao = null;
		
		try {
			String pool = "proxool.mmsc";
			dao = new CommonDao(pool);
			if(dao.query("select 1", null)!=null){
				ret = Constant.SUCCESS;
			}else{
				ret = Constant.ERROR;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}finally{
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
		return ret;
	}

}
