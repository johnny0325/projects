/**
 * CfgUtil.java 2010-6-29 ÉÏÎç09:47:05
 */
package common.util;

import java.util.Map;

import common.dom4j.XmlSupportImpl;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class CfgUtil{
	private static Map configureMap = null;
	static{
		String file = ClassLoader.getSystemResource("").getPath() + "cfg.xml";
		try {
			configureMap = new XmlSupportImpl().readDocument(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getValue(String key) {
		return (String) configureMap.get(key);
	}

	
	public static void main(String[] argv){
		System.out.println(CfgUtil.getValue("ftpconf"));
	}

}
