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
 * 将Excel的彩信数据导入到数据库
 * @author DengJianhua
 * Jul 19, 2010 11:33:28 AM
 * TaskMms.java
 */
public class TaskMms 	{
	private static Logger log = Logger.getLogger(TaskMms.class);
	
	
	private static String ftpconf = CfgUtil.getValue("ftpconf")+"/mbw/mms/";//D:\ftp\vdes\mbw\mms
	/**
	 * 将Excel数据插入到数据库中
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
	    	//读取excel内容
			InputStream in = new FileInputStream(filePath);
			//System.out.println(in);
	    	HSSFWorkbook workbook=new HSSFWorkbook(in);
	    	
			String[] columns = {"sendTime","callerPhone","calledPhone","mmsTitle","mmsContent"};//Excel的列
			
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
							pstmt.setString(6, url);//提取url
						}else{
							pstmt.setString(6, "");//提取url
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
							pstmt.setString(9, rs1.getString(1)+"市");//city
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
						pstmt.addBatch();//批量提交
					}
					
					
					if (i % LEN == 0) {
						log.info("handle---> "+rowNum);
						pstmt.executeBatch();
						DBUtil.release(pstmt, con);
					}
					recordMap=null;//释放
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
	 * mtk2.wapdfw.com 就得到 wapdfw.com 做为特征
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
				feature = url;//当两种匹配都不通过时，就将原来URL直接返回做为特征域名。
			}
			
		}
		return feature;
		
	}

	/**
	 * 用的是java.util.regex.*类库的提取目标字符串中的url
	 * 在正则表达式上跟的Jakarta-ORO不一样的
	 * 如：java.util.regex用\w表示数字和字母
	 * Jakarta-ORO用[A-Z0-9]表示数字和字母
	 * @param targetStr 目标字符串
	 * @return 返回url
	 */
	public String getRegexUrl(String targetStr){
		//www开头的
		String regexWww="www"
			+"\\.\\w+(-\\w+)*?"//.[a-z0-9]
			+"\\.(com|net|cn|org|gov|tv)"//.com或.net或.cn或.org或.gov或.tv
			+"(.cn|\\.hk)?"//中国或香港
			+"(\\:\\d{1,4})?";//端口
		//url是http://等开头的
		String regexHttp="(http|https|file|ftp)\\:\\//"//http://
		+"\\w+(-\\w+)*"//
		+"\\.\\w+(-\\w+)*?"//.[a-z0-9]
		+"\\.(com|net|cn|org|gov|tv)"//.com或.net或.cn或.org或.gov或.tv
		+"(\\.cn|\\.hk)?"//中国或香港
		+"(\\:\\d{1,4})?";//端口
		
		//url为ip的
		String regexIp="(http|https|file|ftp)\\:\\//?"//http://
			+"\\d{1,3}"
			+"\\.\\d{1,3}"
			+"\\.\\d{1,3}"
			+"\\.\\d{1,3}"
			+"(\\:\\d{1,4})?";//端口
	//	log.info("原内容:"+ targetStr);
		StringBuffer sb = new StringBuffer();
		
		boolean http=true;
		 //	 生成Pattern对象并且编译一个简单的正则表达对应url为Ip的
        java.util.regex.Pattern pi = java.util.regex.Pattern.compile(regexIp);
        //用Pattern类的matcher()方法生成一个Matcher对象
        Matcher mi = pi.matcher(targetStr);
      //  log.info("Ip正则表达式:"+ mi.pattern());
        while(mi.find()){
        	 if(sb.length()<=0)
        		 sb.append(mi.group());
        	 else
        		 sb.append(","+mi.group());
        }
       // 生成Pattern对象并且编译一个简单的正则表达式对应url为http开头的
        java.util.regex.Pattern ph = java.util.regex.Pattern.compile(regexHttp);
        //用Pattern类的matcher()方法生成一个Matcher对象
        Matcher mh = ph.matcher(targetStr);
       // log.info("http正则表达式:"+ mh.pattern());
        while(mh.find()){
        	http=false;
        	 if(sb.length()<=0)
        		 sb.append(mh.group());
        	 else
        		 sb.append(","+mh.group());
        }  
        if(http){//当http获取有url时，www就不在获取url了，否则会有重复
//        	生成Pattern对象并且编译一个简单的正则表达式对应url为www开头的
            java.util.regex.Pattern pw = java.util.regex.Pattern.compile(regexWww);
            //用Pattern类的matcher()方法生成一个Matcher对象
            Matcher mw = pw.matcher(targetStr);
          //  log.info("www正则表达式:"+ mw.pattern());
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
	 * 如果文件是以".ok"结尾的就过滤掉,并将剩下的文件按日期时间升序排序,
	 * 得到还没处理的文件列表
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
				if(!name.endsWith(".ok")&&name.endsWith(".xls")&&name.startsWith("mms_")){//bill_开头,不以'.ok'结尾的
					
					return true;
				}
				return false;
				
			}

		});
		if(fileNames!=null)
		for(int i=0;i<fileNames.length;i++){
			fileName=fileNames[i];
			if(fileName!=null){
				noSortFile.add(fileName);//剩下不以'.ok'结尾的
			}
		}
		String[] sortFile=new String[noSortFile.size()];
		Collections.sort(noSortFile);//排序
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
	 * 将已经处理的文件,在文件名加上".ok"结尾
	 * @param fileName
	 */
	private void modifyFileName(String fileName){
		//String ftpconf = CfgUtil.getValue("ftpconf");//目录
		//String ftpconf ="D:ftp/mmds";
		File file=new File(ftpconf+File.separator+fileName);//文件
		
		file.renameTo(new File(ftpconf+File.separator+fileName+".ok"));//改名
		
	}
	/**
	 * 外界调用的主要方法；
	 */
	public void doSync() {
		String today = DateUtil.format(new Date(), "MMdd");
		String[] filenames = null;
		String tableName = null;
		filenames = getSortFileNames();//获得文件名,并将以处理的('.ok'结尾)文件过滤,而且时间升序排序
		if(filenames!=null&&filenames.length>0){
			for (String fileName : filenames) {
				tableName =fileName.substring(0,8);//mms_0725_5.xml
				String currentDate=fileName.substring(4,8);//0719
				log.info("tableName---->"+tableName);
				if (currentDate.compareTo(today) < 0) {//要处理的文件日期小于当天才会处理
					log.info("处理"+fileName.substring(4,8)+"的彩信原始话单开始");
					createTable(tableName);
					handle(fileName);//将文件内容导入数据库
					modifyFileName(fileName);//将处理好的文件改名
					log.info("处理"+fileName.substring(4,8)+"的彩信原始话单结束");
				}
			}
			
		}
	}
	public static void main(String args[]){
		new TaskMms().doSync();
	}
}
