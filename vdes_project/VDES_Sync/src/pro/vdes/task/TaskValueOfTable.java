/**
 * TaskCheckReport.java 2009-11-13 ����03:32:56
 */
package pro.vdes.task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import pro.vdes.bean.WarmBean;

import common.db.CommonDao;
import common.util.ConfigureUtil;
import common.util.DBUtil;
import common.util.DateUtil;
import common.util.MailEntity;
import common.util.SendMailThread;

import config.ValueOfTableParse;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskValueOfTable{
	private static Logger log = Logger.getLogger(TaskValueOfTable.class);
	private static NumberFormat percentFormat = NumberFormat.getPercentInstance();
	
	public void doWarm(String fileName){
		ValueOfTableParse parse;
		try {
			parse = new ValueOfTableParse(fileName);
			WarmBean[] warms = parse.getWarms();
			for(WarmBean warm:warms){
				System.out.println(warm.getSql());
				handleWarm(warm);
			}
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}
		

		
	}
	
    private int getBusinessId(String type){
    	if(type.trim().equals("����")){
    		return 1;
    	}else if(type.trim().toLowerCase().equals("wap")){
    		return 2;
    	}
    	return 0;
    }
	private void handleWarm(WarmBean warm){
		List<Map> list = getRecords(warm.getSql());
		if(list ==null) return;

		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtil.getConnection("proxool.vdes");
			String sql = "insert into bpw_neCode_range(businessId,neCode,statDate,count_today,count_yesterday,range,look_range,createDate,rangeField,rangeFieldName,chineseDesc) values(?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			Date currentDate = new Date();
			Map map = null;
			String chineseDesc ="";
			float a = 0;
			float b = 0;
			//wap��TPSָ����Ҫ�ʼ����ѣ�������һ��CACHE,Ҳ����һ��List;
			List<String> contentTpsList = new ArrayList<String>();
			for(int i=0;i<list.size();i++){
				
				map = list.get(i);
				//String aa = "insert into bpw_neCode_range(businessId,neCode,statDate,count_today,count_yesterday,range,look_range,createDate,rangeField,rangeFieldName) values('"+getBusinessId(warm.getType())+"','"+map.get((warm.getNeCode()))+"','"+map.get(("statDate"))+"','"+map.get(warm.getCount())+"',null,'"+Double.parseDouble(map.get(warm.getField())+"")+"','"+warm.getRange()+"','"+DateUtil.format(currentDate, DateUtil.C_TIME_PATTON_DEFAULT)+"','"+warm.getCount()+"','"+warm.getIndicators()+"')";
				//log.info("sql"+aa);
				pstmt.setInt(1, getBusinessId(warm.getType()));
				pstmt.setString(2, map.get((warm.getNeCode()))+"");
				pstmt.setString(3, map.get(("statDate"))+"");
				pstmt.setString(4, map.get(warm.getCount())+"");
				pstmt.setString(5, null);
				pstmt.setDouble(6, Double.parseDouble(map.get(warm.getField())+""));
				pstmt.setFloat(7, warm.getRange());
				pstmt.setString(8, DateUtil.format(currentDate, DateUtil.C_TIME_PATTON_DEFAULT));
				pstmt.setString(9, warm.getCount());
				pstmt.setString(10,warm.getIndicators());
				a = Float.parseFloat(map.get(warm.getField())+"");
				b = warm.getRange();
				chineseDesc =  map.get((warm.getNeCode()))+"��"+DateUtil.getDateStr(map.get("statDate")+"",DateUtil.C_TIME_PATTON_DEFAULT)+"��"+warm.getIndicators()+"Ϊ"+(a<1?(a+"").substring(0,4):a)+","+(a>b?"����":"����")+"��ֵ"+b;
				pstmt.setString(11, chineseDesc);
				pstmt.addBatch();
				
				
				//������ӣ��������TPSҵ����-ǰ���TPSҵ������/ǰ���TPSҵ������ add by aiyan 2010-03-19		
				//������ֵ�tps2 ���ڶ��ڵ�valueofTable��sql�г��ֵģ����Կ��ĳ������ǲ��õ���Ϊ��Ӧ�ð�valueofTable�Ľ������дһ����Ϊ֧���Ǹ��������û���ȡ��
				if(warm.getCount().equals("tps")){
					String content = map.get(warm.getNeCode())+"������TPSֵΪ"+map.get(warm.getCount())+"ǰ��ֵΪ "+map.get("tps2")+"����ֵΪ:"+percentFormat.format(map.get(warm.getField()));
					contentTpsList.add(content);
				}
				
				
			}
			pstmt.executeBatch();
			
			if(contentTpsList.size()>0){
				MailEntity entity = new MailEntity();
				
				String subject="TPS���첨���澯("+DateUtil.format(new Date(),"yyyy-MM-dd")+")";
				String content = 	"<html>" +
				"��ã�"
				+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;TPS���ֲ����澯�����¼��<a href=\"http://211.139.136.107:8080/vdes\">��ֵҵ���ά�ȱ���ϵͳ-ҵ������ά-ҵ��澯��־</a>���鿴��"
				+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;���쳬��������ֵ(15%)����������Ԫ��";
				
				StringBuffer sb = new StringBuffer();
				for(String c:contentTpsList){
					sb.append("<br/>"+c);
				}
				content+=sb.toString()+"</html>";
				LinkedHashMap toAddress= new LinkedHashMap();
				ConfigureUtil cfgUtil = new ConfigureUtil();
				String mail = cfgUtil.getValue("mail");
				String[] mailAddress = mail.split(",");
				for(String oneMail:mailAddress){
					if(oneMail.length()>0&&oneMail.indexOf("|")!=-1){
						toAddress.put(oneMail.split("\\|")[0],oneMail.split("\\|")[1]);
					}
					
				}
					
				entity.setSubject(subject);
				entity.setContent(content);
				entity.setToAddress(toAddress);
				SendMailThread thread = new SendMailThread();
				thread.setMailEntity(entity);
				thread.start();
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		}finally {
			DBUtil.release(pstmt, con);
		}
		
	}
	private List<Map> getRecords(String sql){
		List<Map> list = null;
		CommonDao dao = null;
		try {
			dao = new CommonDao();
			list = dao.query(sql, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}finally{
			try {
				if(dao!=null) dao.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e);
			}
		}
		return list;
		
		
	}
	public static void main(String[] argv){
		log.info("aa");
		new TaskValueOfTable().doWarm("valueOfTable.xml");

	}

}