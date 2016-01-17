package pro.vdes.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import common.util.ConfigureUtil;
import common.util.DBUtil;

public class TaskTelNecode {
	private static Logger log = Logger.getLogger(TaskTelNecode.class);
	
	/*
	 * @author junliang Lin
	 * 实现功能:首页彩信成功率呈现中，地市，号码，网元关系同步
	 */
	public void telNecodeSync(){
		log.info("TaskTelNecode start...");
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs=null;
		List numbersList=new ArrayList();
		try{
			con = DBUtil.getConnection("proxool.vdes");
			String querySql="select telNumber from bpw_mms_city_telephone_ne";
			String updateSql="update bpw_mms_city_telephone_ne set neCode=? where telNumber=?";
			pstmt = con.prepareStatement(querySql);
			rs=pstmt.executeQuery();
			while(rs.next()){
				numbersList.add(rs.getLong("telNumber"));
				//System.out.println("telNumber===========>"+rs.getLong("telNumber"));
			}
			
			pstmt = con.prepareStatement(updateSql);
			for(int i=0;i<numbersList.size();i++){
				pstmt.setString(1, getNecodeByTelnumber(numbersList.get(i)+""));
				pstmt.setLong(2,Long.parseLong(numbersList.get(i)+""));
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}catch(Exception e){
			log.error(e);
		} finally {
			DBUtil.release(rs,pstmt, con);
			
		}
		log.info("TaskTelNecode end...");
	}
	
	/*
	 * @author junliang Lin
	 * 实现功能:根据电话号码查找当天相对应的网元
	 */
	public String getNecodeByTelnumber(String telNumber){
		String line="";
		String result="";
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		try{
			httpClient = new HttpClient();
			String url =new ConfigureUtil().getValue("mmshlrUrl");
			postMethod = new PostMethod(url); 
	        postMethod.setRequestBody(new NameValuePair[]{new NameValuePair("ask", telNumber)});
	        httpClient.executeMethod(postMethod);
	        BufferedReader br = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream()));
	        StringBuffer sb = new StringBuffer();
	        while((line=br.readLine())!=null){
	        	sb.append(line);
	        }
	        result = sb.toString();
			//测试用的数据
			//result="211.139.136.144211.139.136.144    Enum query for: mms ========================================================== </br>\"E2U+mms:mmscid\" \"!^.*$!mms:+861591426/TYPE=PLMN@920003!\" . </br>\"E2U+mms:http:mm1\" \"!^.*$!http://211.139.144.165/was!\" .</br>\"E2U+mms:http:mm7\" \"!^.*$!mms:+861591426/TYPE=PLMN@http://211.139.144.165:8888/vas!\" .</br>\"E2U+mms:smtp:mm3\" \"!^.*$!mms:+861591426/TYPE=PLMN@211.139.144.165!\" .</br>\"E2U+mms:smtp:mm4\" \"!^.*$!mms:+861591426/TYPE=PLMN@211.139.144.165!\" .</br>IN NS ns.mms.e164.gprs.</br></br>MMS Portal==:</br>http://211.139.144.163:8080/portal/login.jsp</br></br>  Enum query for: dsmp ==========================================================</br> \"E2U+dsmp:dsmpid\" \"!^.*$!dsmp:+861591426/TYPE=PLMN@0011!\" .</br>\"E2U+dsmp:http\" \"!^.*$!dsmp:+861591426/TYPE=PLMN@http://211.136.253.116/dsmp/dsmp.wsdl!\" .</br>IN NS ns.dsmp.e164.gprs.</br></br>     Enum query for: pim ==========================================================</br> \"E2U+PIM:http:PIM\" \"!^.*$!http://218.200.249.187!\" .</br>\"E2U+PIM:http:PIM1\" \"!^.*$!http://221.179.221.75!\" .</br>\"E2U+PIM:http:MYPIM\" \"!^.*$!http://221.179.221.74!\" .</br>IN NS ns.pim.e164.gprs.</br></br>";
	        Pattern pattern = Pattern.compile("(.*@)(9.+?)(!.*)");
			Matcher matcher = pattern.matcher(result);
			if(matcher.find()){
				//System.out.println("matcher.group(2)==>"+matcher.group(2));
				return matcher.group(2);
			}
		}catch(Exception e){
			log.error(e);
		}finally{
			if(postMethod!=null){
				postMethod.releaseConnection(); 
			}
		}
		return "";
	}
}
