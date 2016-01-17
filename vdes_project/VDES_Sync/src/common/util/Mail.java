/**
 * CommonEmail.java 2009-8-27 ����03:52:53
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

		  //email.setSubject("���֪ͨ-����-�û���");
		  email.setSubject(aSubject);

		  // set the html message
		  email.setCharset(config.getValue("charset"));
		  email.setSmtpPort(Integer.parseInt(config.getValue("smtpPort")));
		  
//		  email.setHtmlMsg("<html>"+
//		  		"��ã�"+
//		  "<br>�����µļ�鱨�������ύ�����¼��<a href=\"http://192.168.1.109:8080/vdes\">��ֵҵ���ά�ȱ���ϵͳ</a>��������ˡ�" +
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
