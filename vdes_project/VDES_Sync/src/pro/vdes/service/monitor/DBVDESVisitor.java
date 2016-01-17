/**
 * VDESDBVisitor.java 2009-9-9 ÉÏÎç11:33:54
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
public class DBVDESVisitor  implements Visitor {
	
	private static Logger log = Logger.getLogger(DBVDESVisitor.class);
	/* (non-Javadoc)
	 * @see pro.vdes.service.monitor.Visitor#visit()
	 */
	public String visit() {
		// TODO Auto-generated method stub
		String ret = Constant.ERROR;
		CommonDao dao = null;
		try {
			String pool = "proxool.vdes";
			dao = new CommonDao(pool);
			if(dao.query("select getdate()", null)!=null){
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
