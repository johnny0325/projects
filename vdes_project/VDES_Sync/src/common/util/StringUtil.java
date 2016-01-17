/**
 * StringUtil.java 2009-4-30 ионГ11:36:17
 */
package common.util;


/**
 * @author aiyan
 * @version 1.0
 *
 */
public class StringUtil {
	public static String getString(String value,String defaultValue){
		if(value==null||value.toLowerCase().equals("null")){
			return defaultValue;
		}
		return value;
	}
	public static String addBlank(String str,int length){
		if(str==null||str.equals("")||str.toLowerCase().equals("null")){
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<length;i++){
				sb.append(" ");
			}
			return sb.toString();
		}
		if(str.length()>=length){
			return str;
		}else{
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<length-str.length();i++){
				sb.append(" ");
			}
			return str+sb.toString();
		}
	}

}
