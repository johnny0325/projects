/**
 * TaskRange.java 2009-9-1 下午04:27:45
 */
package pro.vdes.task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import pro.vdes.bean.Range;

import common.db.CommonDao;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.DateUtil;
import common.util.Mail;

import config.RangeParse;

/**
 * @author aiyan
 * @version 1.0
 * 程序过程：
 * step 1:查询Sys_BusinessRange得到监控的xml
 * step 2:处理每个xml所对应的表监控
 *
 */

public class TaskRange{
	private static Logger log = Logger.getLogger(TaskRange.class);
	//private NumberFormat percentFormat = NumberFormat.getPercentInstance();
	DecimalFormat decimalFormat = new DecimalFormat("######.00");
	public void doSync(){
		log.info("TaskRange   start!");
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			stmt = con.createStatement();
			rs = stmt.executeQuery("select top (1) createDate from bpw_neCode_range where businessId !=-100  order by createDate desc");
			Date currentDate = null;
			if(rs.next()){
				currentDate = rs.getTimestamp(1);
				currentDate = DateUtil.addDays(currentDate,-3);
			}else{
				currentDate = DateUtil.parseDate(DateUtil.format(new Date(),"yyyy-MM-dd"));
				currentDate = DateUtil.addDays(currentDate,-30);
			}
			Date today =DateUtil.parseDate(DateUtil.format(new Date(),"yyyy-MM-dd"));
			while(currentDate.before(today)||currentDate.equals(today)){
				delete(currentDate);//删除当天的数据的时候先
				generateRange(currentDate);
				currentDate = DateUtil.addDays(currentDate,1);
			}
		} catch (SQLException e) {
			log.error(e);
		}catch (Exception e) {
			log.error(e);
		} finally {
			DBUtil.release(rs,stmt, con);
		}
		log.info("TaskRange end!");
		


	}
	
	public void generateRange(Date currentDate){
//		int bussinesId = 1;
//		String file = "bpw_wap_neCode_range.xml";
		
		List<Map> list = queryBusinessRange();
		if(list==null) return ;
		Map map = null;
		for(int i = 0;i<list.size();i++){
			map = list.get(i);
			Range[] rangeArr = getRange(map.get("path")+"");
			for(int j =0;j<rangeArr.length;j++){
				handle(rangeArr[j],map.get("businessId"),currentDate);	
			}
			
		}
		


	}
	private List<Map> queryBusinessRange(){
		CommonDao dao = null;
		List list = null;
		try {
			dao = new CommonDao();
			list = dao.query("select * from Sys_BusinessRange", null);
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
		return list;
		
	}
	private void delete(Date currentDate){
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			dao.execute("delete from bpw_neCode_range where businessId !=-100 and datediff(d,'"+DateUtil.format(currentDate, "yyyy-MM-dd")+"',createDate)=0",null);
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
	}
	//测试的sql:
    /*declare @a varchar(10)
	set @a='2009-09-20'
	select *,
	case when
		(isnull((select avg(count) from bpw_wap_ne_month where neCode=m.neCode and datediff(d,statDate,m.statDate)>=1 and datediff(d,statDate,m.statDate)<=7 ),0))!=0
		then
		(m.count-(isnull((select avg(count) from bpw_wap_ne_month where neCode=m.neCode and datediff(d,statDate,m.statDate)>=1 and datediff(d,statDate,m.statDate)<=7 ),0)))/(select avg(count) from bpw_wap_ne_month where neCode=m.neCode and datediff(d,statDate,m.statDate)>=1 and datediff(d,statDate,m.statDate)<=7 )
		end as range
	from bpw_wap_ne_month m
		where datediff(d,statDate,@a)<3 and datediff(d,statDate,@a)>0*/
	private void handle(Range range,Object businessId,Date currentDate){
		Connection con = null;
		PreparedStatement pstmt = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			
			String sql = 
			"select *,("+range.getRangeField()+") as rangeField,"+
			" (select avg("+range.getRangeField()+") from "+range.getTable()+" where neCode=m.neCode and datediff(d,statDate,m.statDate)>=1 and datediff(d,statDate,m.statDate)<=7 ) as count_yesterday,"+		
			" case when"+
			
				" (isnull((select avg("+range.getRangeField()+") from "+range.getTable()+" where neCode=m.neCode and datediff(d,statDate,m.statDate)>=1 and datediff(d,statDate,m.statDate)<=7 ),0))!=0"+
				" then"+
				" ("+range.getRangeField()+"-(isnull((select avg("+range.getRangeField()+") from "+range.getTable()+" where neCode=m.neCode and datediff(d,statDate,m.statDate)>=1 and datediff(d,statDate,m.statDate)<=7 ),0)))/(select avg("+range.getRangeField()+") from "+range.getTable()+" where neCode=m.neCode and datediff(d,statDate,m.statDate)>=1 and datediff(d,statDate,m.statDate)<=7 )"+
				" end as range"+
			" from "+range.getTable()+"  m"+
				//" where datediff(d,statDate,'"+DateUtil.format(currentDate, "yyyy-MM-dd")+"')<3 and datediff(d,statDate,'"+DateUtil.format(currentDate, "yyyy-MM-dd")+"')>0";
				//只求每天的昨天异常流量情况，上面被注释的是求每天的昨天和前天，不好！ add by aiyan 2009-10-22
			" where datediff(d,statDate,'"+DateUtil.format(currentDate, "yyyy-MM-dd")+"')=1 order by "+range.getNeCodeField();
				
/*			String sql = " select *,"+
	" (select top(1) "+range.getRangeField()+" from "+range.getTable()+" where datediff(d,m.statDate,statDate)=-1 and neCode=m.neCode) as count_yesterday,"+		
	" case when"+
	" (isnull((select top(1) "+range.getRangeField()+" from "+range.getTable()+" where datediff(d,m.statDate,statDate)=-1 and neCode=m.neCode),0))!=0"+
	" then"+
	" (m.count-(isnull((select top(1) "+range.getRangeField()+" from "+range.getTable()+" where datediff(d,m.statDate,statDate)=-1 and neCode=m.neCode),0)))/(isnull((select top(1) "+range.getRangeField()+" from "+range.getTable()+" where datediff(d,m.statDate,statDate)=-1 and neCode=m.neCode),0))"+
	" end as range"+
	" from "+range.getTable()+"  m"+
	" where datediff(d,statDate,'"+DateUtil.formatDate(currentDate, "yyyy-MM-dd")+"')<=3 " +
	" and datediff(d,statDate,'"+DateUtil.formatDate(currentDate, "yyyy-MM-dd")+"')>0  ";*/
			
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			
			sql = "insert into bpw_neCode_range(businessId,neCode,statDate,count_today,count_yesterday,range,look_range,createDate,rangeField,rangeFieldName) values(?,?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);

			float r =0;
			//wap的TPS指标需要邮件提醒，这里做一个CACHE,也就是一个List;
			//List<String> contentTpsList = new ArrayList<String>();
			while(rs.next()){
				if(rs.getString("range")==null) continue;
				r = (float)(Math.round(rs.getFloat("range")*100)/100.00);//四舍五入，保留两位小数，add by aiyan 2009-10-22
				if(Math.abs(r)>Math.abs(range.getRange())){
					pstmt.setInt(1, Integer.parseInt(businessId+""));
					pstmt.setString(2, rs.getString(range.getNeCodeField()));
					pstmt.setString(3, rs.getString("statDate"));
					pstmt.setString(4, rs.getString("rangeField"));
					pstmt.setString(5, rs.getString("count_yesterday"));
					pstmt.setString(6, rs.getString("range"));
					pstmt.setFloat(7, range.getRange());
					pstmt.setString(8, DateUtil.format(currentDate, "yyyy-MM-dd"));
					pstmt.setString(9, range.getRangeField());
					pstmt.setString(10, range.getRangeFieldName());
					pstmt.addBatch();
					//这里的需求是第二天四五点钟统计昨天的TPS异动情况；add by aiyan 2010-02-27;
					//需求变更：（昨天的TPS业务量-前天的TPS业务量）/前天的TPS业务量，这样就改到另外一个线程完成了 add by aiyan 2010-03-19
//					if(range.getRangeField().toLowerCase().equals("tps")&&isYesterday(currentDate)){
//						String content = rs.getString(range.getNeCodeField())+"的TPS值为"+rs.getString("rangeField")+"周均值为 "+decimalFormat.format(rs.getDouble("count_yesterday"))+"波幅值为:"+percentFormat.format(rs.getFloat("range"));
//						contentTpsList.add(content);
//					}
					
					
				}


			}
			pstmt.executeBatch();
			
			//if(contentTpsList.size()>0) new SendMailThread(contentTpsList,range.getRange()).start();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}catch (Exception e) {
			log.error(e);
		}
		finally {
			DBUtil.release(pstmt, con);
		}

		
	}
	private Range[] getRange(String file){
		try {
			return new RangeParse(file).getRange();
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		return null;
		
	}
	
/*	private boolean isYesterday(Date currentDate){
		String today =DateUtil.format(new Date(),"yyyy-MM-dd");
		String current = DateUtil.format(currentDate,"yyyy-MM-dd");
		return (current.equals(today));
	}*/



}
/*class SendMailThread extends Thread{
	List<String> contentTpsList = null;
	float range ;
	public SendMailThread(List<String> contentTpsList,float range){
		this.contentTpsList =  contentTpsList;
		this.range = range;
	}
	public void run(){
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			System.out.println("邮件发送开始！");
			String aSubject="TPS波动告警("+DateUtil.format(new Date(),"yyyy-MM-dd")+")";
			String aHtml = 	"<html>" +
			"你好："
			+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;TPS出现波动告警，请登录【<a href=\"http://211.139.136.107:8080/vdes\">增值业务多维度保障系统-业务性能维-业务告警日志</a>】查看。"
			+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;昨天超过波动阀值("+percentFormat.format(range)+")，有如下网元：";
			
			StringBuffer sb = new StringBuffer();
			for(String content:contentTpsList){
				sb.append("<br/>"+content);
			}
			aHtml+=sb.toString()+"</html>";
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
			System.out.println("邮件发送结束！");			
	  }
}*/
