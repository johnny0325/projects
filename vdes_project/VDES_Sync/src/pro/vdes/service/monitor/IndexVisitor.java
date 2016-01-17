/**
 * VdesIndexCheck.java 2009-9-8 ÏÂÎç05:00:18
 */
package pro.vdes.service.monitor;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import pro.vdes.task.TaskCheckReport;

import common.db.CommonDao;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class IndexVisitor implements Visitor{
	private static Logger log = Logger.getLogger(IndexVisitor.class);
	public String visit(){
		String ret_visitIndex = visitIndex();
		String ret_visitDB = visitDB();
		if(ret_visitIndex.equals(Constant.SUCCESS)&&ret_visitDB.equals(Constant.SUCCESS)){
			return Constant.SUCCESS;
		}
		return Constant.ERROR;
		
		
	}
	private String  visitIndex() {
		// TODO Auto-generated method stub
		String ret = Constant.ERROR;
		try {
			HttpClient httpClient = new HttpClient();

			String url = "http://211.139.136.107:8080/vdes/bottom.jsp";

			HttpMethod method = new GetMethod(url);
			httpClient.executeMethod(method);
//			System.out.println(method.getStatusLine());
//			System.out.println(method.getResponseBodyAsString());
			method.releaseConnection();
			if(method.getStatusLine().toString().indexOf("200")!=-1){
				ret = Constant.SUCCESS;
			}else{
				ret = Constant.ERROR;
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		return ret;
	}
	private String visitDB(){
		String ret = Constant.ERROR;
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			if(dao.query("select * from SYS_USER", null)!=null){
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
	public static  void main(String[] argv){
		System.out.print(new IndexVisitor().visit());
	}

}
