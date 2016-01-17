/**
 * Parner.java 2009-9-9 ÉÏÎç11:34:10
 */
package pro.vdes.service.monitor;

import org.apache.log4j.Logger;

import common.db.CommonDao;


/**
 * @author aiyan
 * @version 1.0
 *
 */
public class DBPartnerVisitor implements Visitor {
	
	private static Logger log = Logger.getLogger(DBPartnerVisitor.class);
	/* (non-Javadoc)
	 * @see pro.vdes.service.monitor.Visitor#visit()
	 */
	public String visit() {
		// TODO Auto-generated method stub
		String ret = Constant.ERROR;
		CommonDao dao = null;
	
			String pool = "proxool.partner";

				try {
					dao = new CommonDao(pool);
					if (dao.query("select getdate()", null)!=null){
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
