/**
 * ConfigureUtil.java 2009-5-5 ÏÂÎç04:54:23
 */
package common.util;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import common.dom4j.XmlSupportImpl;



/**
 * @author aiyan
 * @version 1.0
 *
 */
public class ConfigureUtil {
	 private Logger log = Logger.getLogger(getClass());

	    private String filename = "cfg.xml";

	    private Document doc = null;
	    
	    public static ConfigureUtil instance = null;
	    
	    private Map configureMap = null;
	    


	    public ConfigureUtil() {
	    	 String file = this.getClass().getResource("/").getPath()+"cfg.xml";
	    	try {
	    		configureMap = new XmlSupportImpl().readDocument(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    /*
	     * (non-Javadoc)
	     * @see proc.gmcc.sso.config.Configure#getValue(java.lang.String)
	     */
	    public String getValue(String key) {
	    	return (String) configureMap.get(key);
	    }
	    public static void main(String[] argv){
	    	System.out.println(new ConfigureUtil().getValue("hlr_query_max"));
	    }


}
