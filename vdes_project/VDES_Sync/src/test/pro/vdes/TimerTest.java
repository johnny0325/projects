/**
 * TimerTest.java 2009-5-19 ����11:01:19
 */
package test.pro.vdes;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import common.util.ConfigureUtil;
import common.util.DateUtil;
import common.util.Mail;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TimerTest extends TestCase {
	final String[] a=null;
	public void testA() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		calendar.set(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		System.out.println(calendar.getTime());
	}
	
	public void testC(){
		Calendar calendar = Calendar.getInstance();

		System.out.println(calendar.getTime());
	
	}
	
	public void testB(){
		String a = "select neCode,spName,spCode,yyyy-MM-dd AS statDate,AOcount,AOSubmitRate,AOReceiveRate,ATCount,ATSubmitRate  from mms_sp_yyyyMMdd";
		Pattern p = Pattern.compile("yyyy-MM-dd.*(as|AS)");
		Matcher m = p.matcher(a);
		System.out.print(m.replaceAll("aiyan"));

	}
	
	public static void main(String[] argv){
		//new TestSendMailThread().start();
		String mail = "leizhiyong@gd.chinamobile.com|�׹�";
			String[] mailAddress = mail.split(",");
		for(String oneMail:mailAddress){
			if(oneMail.length()>0&&oneMail.indexOf("|")!=-1){
				System.out.println(oneMail.split("\\|")[0]+"---"+oneMail.split("\\|")[1]);
			}
			
		}	
		
	}

}
class TestSendMailThread extends Thread{
	List<String> contentTpsList = null;
	public TestSendMailThread(){
	}
	public void run(){
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			System.out.println("�ʼ����Ϳ�ʼ��");
			String aSubject="TPS�����澯("+DateUtil.format(new Date(),"yyyy-MM-dd")+")";
			String aHtml = 	"<html>" +
			"��ã�"
			+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;TPS���ֲ����澯�����¼��<a href=\"http://211.139.136.107:8080/vdes\">��ֵҵ���ά�ȱ���ϵͳ-ҵ������ά-ҵ��澯��־</a>���鿴��"
			+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;���쳬��������ֵ(15%_aiyan)����������Ԫ��"
			+"</html>";
			LinkedHashMap map= new LinkedHashMap();
			ConfigureUtil cfgUtil = new ConfigureUtil();
			String mail = cfgUtil.getValue("mail");
			String[] mailAddress = mail.split(",");
			for(String oneMail:mailAddress){
				if(oneMail.length()>0&&oneMail.indexOf("|")!=-1){
					map.put(oneMail.split("\\|")[0],oneMail.split("\\|")[1]);
				}
				
			}
				
			Mail.send(aSubject, aHtml, map);
			System.out.println("�ʼ����ͽ�����");			
	  }
}
