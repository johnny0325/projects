/**
 * RegUtil.java 2009-8-8 ионГ11:56:35
 */
package common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class RegUtil {
	public static String[] getGroups(String str, String reg) {
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		String[] groups = null;

		if (matcher.find()) {
			groups = new String[matcher.groupCount()+1];
			for(int i=0;i<matcher.groupCount()+1;i++){
				groups[i]= matcher.group(i);
			}
		}
		return groups;
	}

}
