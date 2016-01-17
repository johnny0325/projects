package pro.vdes.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import common.util.CfgUtil;
import common.util.DBUtil;
import common.util.DateUtil;

/**
 * 	监测数据文件入库
 * @author DengJianhua
 * Aug 23, 2010 11:04:55 AM
 * TaskIndicatorsvalue.java
 */
public class TaskIndicatorsvalue {
	private static Logger log = Logger.getLogger(TaskIndicatorsvalue.class);
	private static String ftpconf = CfgUtil.getValue("ftpmailconf")+"/mqos_indicatorsValue/";//D:\ftp\VDES\mbw\mail\mqos\mqos_indicatorsValue
	/**
	 * 获取文件名
	 *@author DengJianhua
	 *Aug 23, 2010  11:19:04 AM
	 *@return
	 */
	private String[] getSortFileNames(String date){
		List<String> noSortFile=new ArrayList<String>();
		String fileName=null;
		//String date=DateUtil.getCurrentDateStr("yyyy-MM-dd");
		//String date=today;
		log.info("ftpconf---------->"+ftpconf);
		File dir = new File(ftpconf+date);
		log.info("fileDir:"+dir.getPath());
		String[] fileNames = dir.list(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				if(name.endsWith(".txt")){//
					return true;
				}
				return false;
			}

		});
		
		if(fileNames!=null)
		for(int i=0;i<fileNames.length;i++){
			fileName=fileNames[i];
			if(fileName!=null){
				log.info("fileName------>"+fileName);
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
	 * 将已经处理的文件,在文件名加上".ok"结尾
	 * @param fileName
	 */
	private void modifyFileName(String fileName,String date){
		//String date=DateUtil.getCurrentDateStr("yyyy-MM-dd");
		File file=new File(ftpconf+File.separator+date+File.separator+fileName);//文件
		
		file.renameTo(new File(ftpconf+File.separator+date+File.separator+fileName+".ok"));//改名
		
	}
	/**
	 * 
	 *@author DengJianhua
	 *Aug 23, 2010  11:24:38 AM
	 *@param fileName
	 *@return
	 *@throws FileNotFoundException
	 */
	public BufferedReader getSource(String fileName,String date)
	throws FileNotFoundException {
		// String ftpconf = new ConfigureUtil().getValue("ftpconf");
		//String date=DateUtil.getCurrentDateStr("yyyy-MM-dd");
		File srcFile = new File(ftpconf+File.separator+date, fileName);
		if (!srcFile.exists()) {
			log.info("no file");
			return null;
		}
		InputStreamReader read =null;
	    try {
			read = new InputStreamReader(new FileInputStream(srcFile),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new BufferedReader(read);
	}
	/**
	 * 监测数据入库
	 *@author DengJianhua
	 *Aug 23, 2010  11:23:58 AM
	 *@param fileName
	 */
	public void handle(String fileName,String date) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			BufferedReader br = this.getSource(fileName,date);
			if (br == null)
				return;
			String line = "";
			int i = 0;
			final int LEN = 2000;
			int count=0;
			String insertSql = "insert into SPO_INDICATORSVALUE(INDICATORSNUMBER,INDICATORSUNITS,FVALUE,RECORDINGTIME,EXTFIELD1,EXTFIELD2,EXTFIELD3,ADDUSER,PROVNAME,AREANAME) values(?,?,?,?,?,?,?,?,?,?)";
		
			while ((line = br.readLine()) != null) {
				try {
					// 对数据库连接的优化
					if (con == null || i % LEN == 0) {
						con = DBUtil.getConnection("proxool.vdes");
						pstmt = con.prepareStatement(insertSql);
					}
					++i;
					String[] row = line.split("&");
					//指标体系编号,指标单位，监测结果，监测时间，扩展字段1，扩展字段2，网络类型，手机号码，修改用户， 归属省份， 归属市区名称
					//1012111212,20,41.6250,2010-5-13 16:41:43,n,Webmail打开邮件超时,3,13969318004,,山东,淄博
					if(null!=row&&row.length==11){
						String unit=row[1];
						if("20".equals(unit)||"23".equals(unit)){
							//当指标单位为20或23的时候，指标拨测结果记录在 EXTFIELD1字段中
							if(null!=row[0])
								pstmt.setLong(1, Long.parseLong(row[0]));//INDICATORSNUMBER
							else
								pstmt.setString(1, null);
							if(null!=row[1])
								pstmt.setLong(2, Long.parseLong(row[1]));//INDICATORSUNITS
							else
								pstmt.setString(2, null);
							if(null!=row[2]){
								float fvalue=Float.parseFloat(row[2].trim());
								pstmt.setFloat(3, fvalue);//FVALUE
							}else{
								pstmt.setString(3, null);
							}
							if(null!=row[3])
								pstmt.setTimestamp(4, Timestamp.valueOf(DateUtil.getDateStr(row[3],"yyyy-MM-dd HH:mm:ss")));//RECORDINGTIME
							else
								pstmt.setString(4, null);
							if("20".equals(unit)){
								pstmt.setString(5, "y");//20用y表示，EXTFIELD1
							}
							if("23".equals(unit)){
								pstmt.setString(5, "n");//23用n表示，EXTFIELD1
							}
							pstmt.setString(6, row[5]);//EXTFIELD2
							pstmt.setString(7, row[6]);//EXTFIELD3
							pstmt.setString(8, row[7]);//ADDUSER
							pstmt.setString(9, row[9]);//PROVNAME
							pstmt.setString(10, row[10]);//AREANAME
						}else{
							if(null!=row[0])
								pstmt.setLong(1, Long.parseLong(row[0]));//INDICATORSNUMBER
							else
								pstmt.setString(1, null);
							if(null!=row[1])
								pstmt.setLong(2, Long.parseLong(row[1]));//INDICATORSUNITS
							else
								pstmt.setString(2, null);
							if(null!=row[2]){
								float fvalue=Float.parseFloat(row[2].trim());
								pstmt.setFloat(3, fvalue);//FVALUE
							}else{
								pstmt.setString(3, null);
							}
							if(null!=row[3])
								pstmt.setTimestamp(4, Timestamp.valueOf(DateUtil.getDateStr(row[3],"yyyy-MM-dd HH:mm:ss")));//RECORDINGTIME
							else
								pstmt.setString(4, null);
							pstmt.setString(5, row[4]);//EXTFIELD1
							pstmt.setString(6, row[5]);//EXTFIELD2
							pstmt.setString(7, row[6]);//EXTFIELD3
							pstmt.setString(8, row[7]);//ADDUSER
							pstmt.setString(9, row[9]);//PROVNAME
							pstmt.setString(10, row[10]);//AREANAME
						}
						pstmt.addBatch();
					}else if(row.length==12){
						//log.info("lenght=12:"+line);
						count++;
					}

					if (i % LEN == 0) {
						log.info("------>do with:"+i);
						pstmt.executeBatch();
						DBUtil.release(pstmt, con);
					}
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
				}
			}
			br.close();
			log.info("------->file lenght:" + i);
			log.info("-------->lenght is 12 have :"+count);
			if (con != null && !con.isClosed()) {
				pstmt.executeBatch();
				DBUtil.release(pstmt, con);
			}
			log.info("------->"+fileName + " done");
		} catch (IOException e) {
			// TODO Auto-generated catch block

			log.error(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block

			log.error(e);
			e.printStackTrace();
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					DBUtil.release(pstmt, con);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	/**
	 * 如果监测数据文件中的省份和地市在ProvinceCity表中找不到，则插入
	 *@author DengJianhua
	 *Aug 24, 2010  2:35:17 PM
	 */
	private void setProvinceCity(){
		Connection con = null;
		Statement stmt=null;
		con = DBUtil.getConnection("proxool.vdes");
		try {
			stmt=con.createStatement();
			String sql="insert into ProvinceCity(province,city) "+
					" select  provname,areaname from SPO_INDICATORSVALUE "+
					" where provname not in(select  province from provinceCity) " +
					" and areaname not in(select  city from provinceCity) "+
					" group by provname,areaname";
			stmt.execute(sql);
			DBUtil.release(stmt, con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 外部调用接口
	 *@author DengJianhua
	 *Aug 24, 2010  9:31:18 AM
	 */
	public void doSync(){
		log.info("--------->start to import indicatorsvalue");
		String today=DateUtil.getCurrentDateStr("yyyy-MM-dd");//今天
//		String today="2010-08-17";
		String[] files=getSortFileNames(today);
		for(String file:files){
			log.info("file-------->"+file);
			handle(file,today);
			modifyFileName(file,today);
		}
		String preday=DateUtil.preDay(DateUtil.getCurrentDateStr("yyyyMMdd"), 1, "yyyy-MM-dd");//昨天
//		String preday="2010-08-13";
		String[] prefiles=getSortFileNames(preday);
		for(String file:prefiles){
			log.info("file-------->"+file);
			handle(file,preday);//入库
			modifyFileName(file,preday);//修改文件名
		}
		setProvinceCity();//提取省份和地市
		log.info("--------->end to import indicatorsvalue");
	}

	public static void main(String[] args) {
		TaskIndicatorsvalue t=new TaskIndicatorsvalue();
		t.doSync();
	
		
	}
}
