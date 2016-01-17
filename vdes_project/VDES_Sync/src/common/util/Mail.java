/**
 * CommonEmail.java 2009-8-27 下午03:52:53
 */
package common.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;



/**
 * @author aiyan
 * @version 1.0
 *
 */
public class Mail {
	public static void send(String aSubject,String aHtml,LinkedHashMap map) {
//	 Create the email message
	  try {
		HtmlEmail email = new HtmlEmail();
		
		ConfigureUtil config = new ConfigureUtil();
		
		  email.setAuthentication(config.getValue("Authentication_Name"), config.getValue("Authentication_Password"));
		  

		  email.setHostName(config.getValue("host"));

		  for(Iterator it =  map.entrySet().iterator();it.hasNext();){
			  Map.Entry  a= (Map.Entry)it.next();
			  email.addTo(a.getKey().toString(), a.getValue().toString());
		  }
		  

		  email.setFrom(config.getValue("from"), config.getValue("fromName"));

		  //email.setSubject("审核通知-地市-用户名");
		  email.setSubject(aSubject);

		  // set the html message
		  email.setCharset(config.getValue("charset"));
		  email.setSmtpPort(Integer.parseInt(config.getValue("smtpPort")));
		  
//		  email.setHtmlMsg("<html>"+
//		  		"你好："+
//		  "<br>有最新的检查报告申请提交，请登录【<a href=\"http://192.168.1.109:8080/vdes\">增值业务多维度保障系统</a>】进行审核。" +
//
//		  		"</html>");
		  email.setHtmlMsg(aHtml);

		  // set the alternative message

		  email.setTextMsg("Your email client does not support HTML messages");

		  // send the email

		  email.send();
	} catch (EmailException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
