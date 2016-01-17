package pro.vdes.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import common.util.CfgUtil;
import common.util.DBUtil;
import common.util.DateUtil;
/**
 * ��Excel�Ĳ������ݵ��뵽���ݿ�
 * @author DengJianhua
 * Jul 19, 2010 11:33:28 AM
 * TaskMms.java
 */
public class TaskMms 	{
	private static Logger log = Logger.getLogger(TaskMms.class);
	
	
	private static String ftpconf = CfgUtil.getValue("ftpconf")+"/mbw/mms/";//D:\ftp\vdes\mbw\mms
	/**
	 * ��Excel���ݲ��뵽���ݿ���
	 *@author DengJianhua
	 *Jul 19, 2010  1:01:55 PM
	 *
	 */
	public void handle(String fileName){
		log.info("<------start handle");
		Connection con = null;
		PreparedStatement pstmt = null;
		String tableName  =fileName.substring(0,8);//mms_0725_5.xml
	
		try{
			String insert_sql = "insert into "+tableName+"(statDate,callerPhone,calledPhone,mmsTitle,mmsContent,url,domain,feature,city,brand,terminal,os) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			String filePath=ftpconf+fileName;
			log.info("fileName------------->"+filePath);
	    	//��ȡexcel����
			InputStream in = new FileInputStream(filePath);
			//System.out.println(in);
	    	HSSFWorkbook workbook=new HSSFWorkbook(in);
	    	
			String[] columns = {"sendTime","callerPhone","calledPhone","mmsTitle","mmsContent"};//Excel����
			
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			int firstRow = sheet.getFirstRowNum();
			int lastRow = sheet.getLastRowNum();
			int titleRows = 1;
			String url=null;
			String domain=null;
			String feature=null;
			int i = 0;
			final int LEN = 2000;
			for(int rowNum = firstRow + titleRows; rowNum <= lastRow; rowNum ++)
			{
				HSSFRow row = sheet.getRow(rowNum);
				
				if(row != null)
				{
					Map<String,String> recordMap = new HashMap<String,String>();
					for(int cellNum = 0; cellNum < columns.length; cellNum ++)
					{
						HSSFCell cell = row.getCell((short)cellNum);
						String cellString = "";
						if(cell != null)
						{
							//log.info("neirong---->:"+cell.getRichStringCellValue().getString());
							switch(cell.getCellType())
							{
							case HSSFCell.CELL_TYPE_STRING:
								cellString = cell.getRichStringCellValue().getString();
								
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								if(HSSFDateUtil.isCellDateFormatted(cell))
								{
									cellString = cell.getDateCellValue().toLocaleString();
								}
								else
								{
									cellString = String.valueOf((long)cell.getNumericCellValue());
								}
								break;
							case HSSFCell.CELL_TYPE_FORMULA:
								cellString = String.valueOf((long)cell.getNumericCellValue());
								break;
							default:
								cellString = "";
								break;
							}
						}
						recordMap.put(columns[cellNum], cellString);
						cell=null;
						cellString=null;
					}//end cellNum
					
					if (con == null || i % LEN == 0) {
						con = DBUtil.getConnection("proxool.mmds");
						pstmt = con.prepareStatement(insert_sql);
					}
					String callerPhone=recordMap.get("callerPhone");
					if(callerPhone.startsWith("86")){
						++i;
						pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(recordMap.get("sendTime")));
						pstmt.setLong(2, Long.parseLong(recordMap.get("callerPhone")));
						pstmt.setLong(3, Long.parseLong(recordMap.get("calledPhone")));
						pstmt.setString(4, recordMap.get("mmsTitle")+"");
						pstmt.setString(5, recordMap.get("mmsContent")+"");
						String content=recordMap.get("mmsContent");
						if(content!=null){
							url= getRegexUrl(content+"");
							pstmt.setString(6, url);//��ȡurl
						}else{
							pstmt.setString(6, "");//��ȡurl
						}
						if(url.startsWith("http://")){
							domain = url.replace("http://","");
						}else{
							domain=url;
						}
						feature=getFeature(domain);
						pstmt.setString(7, domain);
						pstmt.setString(8, feature);
						
						String code=callerPhone.substring(2,9);
						Statement stmt1=con.createStatement();
						ResultSet rs1=stmt1.executeQuery("select city,brand from BPW_HLR b where b.code="+code);
						if(rs1.next()){
							pstmt.setString(9, rs1.getString(1)+"��");//city
							pstmt.setString(10, rs1.getString(2));
						}else{
							pstmt.setString(9, null);//city
							pstmt.setString(10, null);
						}
						rs1.close();
						stmt1.close();
						Statement stmt2=con.createStatement();
						ResultSet rs2=stmt2.executeQuery("select terminal,os from mmds_terminal_os t where t.callerphone="+callerPhone.substring(2));
						if(rs2.next()){
							pstmt.setString(11, rs2.getString(1));//terminal
							pstmt.setString(12, rs2.getString(2));//os
						}else{
							pstmt.setString(11, null);//terminal
							pstmt.setString(12, null);//os
						}
						rs2.close();
						stmt2.close();
						pstmt.addBatch();//�����ύ
					}
					
					
					if (i % LEN == 0) {
						log.info("handle---> "+rowNum);
						pstmt.executeBatch();
						DBUtil.release(pstmt, con);
					}
					recordMap=null;//�ͷ�
				}// end if row
				row=null;
			}	//end rowNum
			if (con!=null&&!con.isClosed()) {
				pstmt.executeBatch();
				DBUtil.release(pstmt, con);
			}
			log.info("end handle---->");
		}catch(Exception e){
        	e.printStackTrace();
        }finally{
			DBUtil.release(pstmt, con);
        }
	}
	/**
	 * mtk2.wapdfw.com �͵õ� wapdfw.com ��Ϊ����
	 * @param url
	 * @return
	 */
	private String getFeature(String url){
		String feature = null;
		String regEx ="(\\w+:)?(([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.)?(com.cn|" +
				"com|cn|net|org)(:\\d*)?";
		Pattern p=Pattern.compile(regEx);
		Matcher m=p.matcher(url);
		if(m.find()){
/*			for(int i =0;i<m.groupCount();i++){
				System.out.println(i+"--"+m.group(i));
			}*/
			feature = m.group(2)+m.group(4);
		}else{
			regEx ="((\\d+)\\.(\\d+)\\.(\\d+))\\.(\\d+)";
			p=Pattern.compile(regEx);
			m=p.matcher(url);
			if(m.find()){
				feature = m.group(1);
			}else{
				feature = url;//������ƥ�䶼��ͨ��ʱ���ͽ�ԭ��URLֱ�ӷ�����Ϊ����������
			}
			
		}
		return feature;
		
	}

	/**
	 * �õ���java.util.regex.*������ȡĿ���ַ����е�url
	 * ��������ʽ�ϸ���Jakarta-ORO��һ����
	 * �磺java.util.regex��\w��ʾ���ֺ���ĸ
	 * Jakarta-ORO��[A-Z0-9]��ʾ���ֺ���ĸ
	 * @param targetStr Ŀ���ַ���
	 * @return ����url
	 */
	public String getRegexUrl(String targetStr){
		//www��ͷ��
		String regexWww="www"
			+"\\.\\w+(-\\w+)*?"//.[a-z0-9]
			+"\\.(com|net|cn|org|gov|tv)"//.com��.net��.cn��.org��.gov��.tv
			+"(.cn|\\.hk)?"//�й������
			+"(\\:\\d{1,4})?";//�˿�
		//url��http://�ȿ�ͷ��
		String regexHttp="(http|https|file|ftp)\\:\\//"//http://
		+"\\w+(-\\w+)*"//
		+"\\.\\w+(-\\w+)*?"//.[a-z0-9]
		+"\\.(com|net|cn|org|gov|tv)"//.com��.net��.cn��.org��.gov��.tv
		+"(\\.cn|\\.hk)?"//�й������
		+"(\\:\\d{1,4})?";//�˿�
		
		//urlΪip��
		String regexIp="(http|https|file|ftp)\\:\\//?"//http://
			+"\\d{1,3}"
			+"\\.\\d{1,3}"
			+"\\.\\d{1,3}"
			+"\\.\\d{1,3}"
			+"(\\:\\d{1,4})?";//�˿�
	//	log.info("ԭ����:"+ targetStr);
		StringBuffer sb = new StringBuffer();
		
		boolean http=true;
		 //	 ����Pattern�����ұ���һ���򵥵��������ӦurlΪIp��
        java.util.regex.Pattern pi = java.util.regex.Pattern.compile(regexIp);
        //��Pattern���matcher()��������һ��Matcher����
        Matcher mi = pi.matcher(targetStr);
      //  log.info("Ip������ʽ:"+ mi.pattern());
        while(mi.find()){
        	 if(sb.length()<=0)
        		 sb.append(mi.group());
        	 else
        		 sb.append(","+mi.group());
        }
       // ����Pattern�����ұ���һ���򵥵�������ʽ��ӦurlΪhttp��ͷ��
        java.util.regex.Pattern ph = java.util.regex.Pattern.compile(regexHttp);
        //��Pattern���matcher()��������һ��Matcher����
        Matcher mh = ph.matcher(targetStr);
       // log.info("http������ʽ:"+ mh.pattern());
        while(mh.find()){
        	http=false;
        	 if(sb.length()<=0)
        		 sb.append(mh.group());
        	 else
        		 sb.append(","+mh.group());
        }  
        if(http){//��http��ȡ��urlʱ��www�Ͳ��ڻ�ȡurl�ˣ���������ظ�
//        	����Pattern�����ұ���һ���򵥵�������ʽ��ӦurlΪwww��ͷ��
            java.util.regex.Pattern pw = java.util.regex.Pattern.compile(regexWww);
            //��Pattern���matcher()��������һ��Matcher����
            Matcher mw = pw.matcher(targetStr);
          //  log.info("www������ʽ:"+ mw.pattern());
            while(mw.find()){
            	 if(sb.length()<=0)
            		 sb.append(mw.group());
            	 else
            		 sb.append(","+mw.group());
            }
            
        }
  
     //   log.info("url:"+ sb.toString());
        
        return sb.toString();
	}
	/**
	 * ����ļ�����".ok"��β�ľ͹��˵�,����ʣ�µ��ļ�������ʱ����������,
	 * �õ���û������ļ��б�
	 * @param fileNames
	 * @return
	 */
	private String[] getSortFileNames(){
	
		List<String> noSortFile=new ArrayList<String>();
		String fileName=null;
	//	String ftpconf = CfgUtil.getValue("ftpconf");
		//String ftpconf = "/export/mmds/hamob_data";
		File dir = new File(ftpconf);
		System.out.println("fileDir:"+dir.getName());
		String[] fileNames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				//System.out.println("name:"+name);
				// TODO Auto-generated method stub
				if(!name.endsWith(".ok")&&name.endsWith(".xls")&&name.startsWith("mms_")){//bill_��ͷ,����'.ok'��β��
					
					return true;
				}
				return false;
				
			}

		});
		if(fileNames!=null)
		for(int i=0;i<fileNames.length;i++){
			fileName=fileNames[i];
			if(fileName!=null){
				noSortFile.add(fileName);//ʣ�²���'.ok'��β��
			}
		}
		String[] sortFile=new String[noSortFile.size()];
		Collections.sort(noSortFile);//����
		for(int i=0;i<noSortFile.size();i++){
			sortFile[i]=noSortFile.get(i);
		}
		return sortFile;
	}
	/**
	 * @param string
	 */
	private void createTable(String tableName) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			String sql="create table "+tableName+"(" +
			   "statDate  DATE,"+
			   "callerPhone   NUMBER(19),"+
			   "calledPhone  NUMBER(19),"+
			   "mmsTitle  varchar(128),"+
			   "mmsContent  varchar(2048),"+
			   "url   varchar(1024),"+
			   "domain  varchar(128),"+
			   "feature  varchar(50)," +
			   "city  varchar(50)," +
			   "brand  varchar(50)," +
			   "terminal varchar(50),"+
			   "os varchar(50)"+
			   ")";
			stmt.execute(sql);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, con);
			log.info("create table "+tableName+" done");
		}
		log.info("---->create table:"+tableName);
		
	}
	/**
	 * ���Ѿ�������ļ�,���ļ�������".ok"��β
	 * @param fileName
	 */
	private void modifyFileName(String fileName){
		//String ftpconf = CfgUtil.getValue("ftpconf");//Ŀ¼
		//String ftpconf ="D:ftp/mmds";
		File file=new File(ftpconf+File.separator+fileName);//�ļ�
		
		file.renameTo(new File(ftpconf+File.separator+fileName+".ok"));//����
		
	}
	/**
	 * �����õ���Ҫ������
	 */
	public void doSync() {
		String today = DateUtil.format(new Date(), "MMdd");
		String[] filenames = null;
		String tableName = null;
		filenames = getSortFileNames();//����ļ���,�����Դ����('.ok'��β)�ļ�����,����ʱ����������
		if(filenames!=null&&filenames.length>0){
			for (String fileName : filenames) {
				tableName =fileName.substring(0,8);//mms_0725_5.xml
				String currentDate=fileName.substring(4,8);//0719
				log.info("tableName---->"+tableName);
				if (currentDate.compareTo(today) < 0) {//Ҫ������ļ�����С�ڵ���Żᴦ��
					log.info("����"+fileName.substring(4,8)+"�Ĳ���ԭʼ������ʼ");
					createTable(tableName);
					handle(fileName);//���ļ����ݵ������ݿ�
					modifyFileName(fileName);//������õ��ļ�����
					log.info("����"+fileName.substring(4,8)+"�Ĳ���ԭʼ��������");
				}
			}
			
		}
	}
	public static void main(String args[]){
		new TaskMms().doSync();
	}
}
