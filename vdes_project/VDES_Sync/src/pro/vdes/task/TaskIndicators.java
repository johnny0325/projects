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
import java.sql.ResultSet;
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

public class TaskIndicators {

	private static Logger log = Logger.getLogger(TaskIndicators.class);
	private static String ftpconf = CfgUtil.getValue("ftpmailconf")+"/mqos_indicators/";//D:\ftp\VDES\mbw\mail\mqos\mqos_indicators
	/**
	 * 获取文件名
	 *@author DengJianhua
	 *Aug 24, 2010  11:19:04 AM
	 *@return
	 */
	private String[] getSortFileNames(){
		List<String> noSortFile=new ArrayList<String>();
		String fileName=null;
		String date=DateUtil.getCurrentDateStr("yyyy-MM");
		File dir = new File(ftpconf+date);
		log.info("fileDir:"+dir.getName());
		String[] fileNames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if(name.endsWith(".txt")){//txt结尾的
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
	 * 将已经处理的文件,在文件名加上".ok"结尾
	 *@author DengJianhua
	 *Aug 24, 2010  4:17:26 PM
	 *@param fileName
	 */
	private void modifyFileName(String fileName){
		String date=DateUtil.getCurrentDateStr("yyyy-MM");
		File file=new File(ftpconf+File.separator+date+File.separator+fileName);//文件
		
		file.renameTo(new File(ftpconf+File.separator+date+File.separator+fileName+".ok"));//改名
		
	}
	/**
	 * 根据文件名获取文件
	 *@author DengJianhua
	 *Aug 24, 2010  11:24:38 AM
	 *@param fileName
	 *@return
	 *@throws FileNotFoundException
	 */
	public BufferedReader getSource(String fileName)
	throws FileNotFoundException {
		String date=DateUtil.getCurrentDateStr("yyyy-MM");
		File srcFile = new File(ftpconf+File.separator+date, fileName);
		if (!srcFile.exists()) {
			log.info("no file");
			return null;
		}
		InputStreamReader read =null;
	    try {
			read = new InputStreamReader(new FileInputStream(srcFile),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return new BufferedReader(read);
	}
	/**
	 * 维度数据入库
	 *@author DengJianhua
	 *Aug 24, 2010  11:23:58 AM
	 *@param fileName
	 */
	public void handle(String fileName) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			BufferedReader br = this.getSource(fileName);
			if (br == null)
				return;
			String line = "";
			int i = 0;
			final int LEN = 2000;
			String insertSql = "insert into SPO_INDICATORS( " +
								"INDICATORSNUMBER,ADDTIME,ACTIONCODE,INDICATORSNAME,INDICATORSUNITS,INDICATORSEXPLAIN,"+
								"ACQUISITIONMODE,ALGORITHM,INDICATORSLEVEL,INDICATORSCONCERN,INDICATORSASSESSMENT,INDICATORSNOTES,"+
								"CATEGORIES1,CATEGORIESNAME1,CATEGORIES2,CATEGORIESNAME2,CATEGORIES3,CATEGORIESNAME3,"+
								"CATEGORIES4,CATEGORIESNAME4,CATEGORIES5,CATEGORIESNAME5,CATEGORIES6,CATEGORIESNAME6,"+
								"CATEGORIES7,CATEGORIESNAME7,CATEGORIES8,CATEGORIESNAME8) " +
								"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
			while ((line = br.readLine()) != null) {
				try {
					// 对数据库连接的优化
					if (con == null || i % LEN == 0) {
						con = DBUtil.getConnection("proxool.vdes");
						pstmt = con.prepareStatement(insertSql);
					}
					++i;
					String[] row = line.split("&");
					//记录添加时间，指标编号, 操作码，指标名称，指标单位，指标含义解释，
					//获取方式，算法，指标所属层次，指标关注度，考核指标，备注，
					//1级指标体系分类编号，1级指标体系分类含义解释，2级指标体系分类编号，2级指标体系分类含义解释，3级指标体系分类编号，3级指标体系分类含义解释，
					//4级指标体系分类编号，4级指标体系分类含义解释，5级指标体系分类编号，5级指标体系分类含义解释，6级指标体系分类编号，6级指标体系分类含义解释，
					//7级指标体系分类编号，7级指标体系分类含义解释，8级指标体系分类编号，8级指标体系分类含义解释
					//2010-4-21 10:32:56,1012101110131110,1,每秒写信/取信量,3,,
					//1,1,8,,,,
					//10,手机邮箱业务质量,1012,WEBMAIL服务质量,101210,系统性能,
					//10121011,系统处理效率,1012101110,设备处理效率,101210111013,MS服务器集群处理效率,
					//10121011101311,单台MS服务器2
					if(null!=row&&row.length>=9){
						if(null!=row[1])
							pstmt.setLong(1, Long.parseLong(row[1]));//INDICATORSNUMBER 
						else
							pstmt.setString(1, null);
						if(null!=row[0])
							pstmt.setTimestamp(2, Timestamp.valueOf(DateUtil.getDateStr(row[0],"yyyy-MM-dd HH:mm:ss")));//ADDTIME
						else
							pstmt.setString(2, null);
						if(null!=row[2])
							pstmt.setLong(3, Long.parseLong(row[2]));//ACTIONCODE
						else
							pstmt.setString(3, null);
						pstmt.setString(4, row[3]);//INDICATORSNAME
						if(null!=row[4])
							pstmt.setLong(5, Long.parseLong(row[4]));//INDICATORSUNITS
						else
							pstmt.setString(5, null);
						pstmt.setString(6, row[5]);//INDICATORSEXPLAIN
						if(null!=row[6])
							pstmt.setLong(7, Long.parseLong(row[6]));//ACQUISITIONMODE
						else 
							pstmt.setString(7, null);
						if(null!=row[7])
							pstmt.setLong(8, Long.parseLong(row[7]));//ALGORITHM
						else
							pstmt.setString(8, null);
						pstmt.setInt(9, Integer.parseInt(row[8]));//INDICATORSLEVEL 该字段的值为INDICATORSNUMBER字段值的长度除以2
						if(row.length>=10&&null!=row[9])
							pstmt.setString(10,row[9]);//INDICATORSCONCERN
						else
							pstmt.setString(10,null);//INDICATORSCONCERN
						if(row.length>=11&&null!=row[10])
							pstmt.setString(11,row[10]);//INDICATORSASSESSMENT
						else
							pstmt.setString(11,null);//INDICATORSASSESSMENT
						if(row.length>=12&&null!=row[11])
							pstmt.setString(12, row[11]);//INDICATORSNOTES
						else
							pstmt.setString(12,null);//INDICATORSASSESSMENT
						
						if(row.length>=14&&null!=row[12])
							pstmt.setLong(13, Long.parseLong(row[12]));//CATEGORIES1
						else
							pstmt.setString(13,null );
						if(row.length>=14&&null!=row[13])
							pstmt.setString(14, row[13]);//CATEGORIESNAME1
						else
							pstmt.setString(14, null);
						
						if(row.length>=16&&null!=row[14])
							pstmt.setLong(15, Long.parseLong(row[14]));//CATEGORIES2
						else
							pstmt.setString(15,null );
						if(row.length>=16&&null!=row[15])
							pstmt.setString(16, row[15]);//CATEGORIESNAME2
						else 
							pstmt.setString(16,null);
						
						if(row.length>=18&&null!=row[16])
							pstmt.setLong(17, Long.parseLong(row[16]));//CATEGORIES3
						else
							pstmt.setString(17,null );
						if(row.length>=18&&null!=row[17])
							pstmt.setString(18, row[17]);//CATEGORIESNAME3
						else
							pstmt.setString(18, null);
						
						if(row.length>=20&&null!=row[18])
							pstmt.setLong(19, Long.parseLong(row[18]));//CATEGORIES4
						else
							pstmt.setString(19,null );
						if(row.length>=20&&null!=row[19])
							pstmt.setString(20, row[19]);//CATEGORIESNAME4
						else
							pstmt.setString(20, null);
						
						if(row.length>=22&&null!=row[20])
							pstmt.setLong(21, Long.parseLong(row[20]));//CATEGORIES5
						else
							pstmt.setString(21,null );
						if(row.length>=22&&null!=row[21])
							pstmt.setString(22, row[21]);//CATEGORIESNAME5
						else
							pstmt.setString(22, null);
						
						if(row.length>=24&&null!=row[22])
							pstmt.setLong(23, Long.parseLong(row[22]));//CATEGORIES6
						else
							pstmt.setString(23,null );
						if(row.length>=24&&null!=row[23])
							pstmt.setString(24, row[23]);//CATEGORIESNAME6
						else
							pstmt.setString(24, null);
						
						if(row.length>=26&&null!=row[24])
							pstmt.setLong(25, Long.parseLong(row[24]));//CATEGORIES7
						else
							pstmt.setString(25,null );
						if(row.length>=26&&null!=row[25])
							pstmt.setString(26, row[25]);//CATEGORIESNAME7
						else
							pstmt.setString(26, null);
						
						if(row.length>=28&&null!=row[26])
							pstmt.setLong(27, Long.parseLong(row[26]));//CATEGORIES8
						else
							pstmt.setString(27,null );
						if(row.length>=28&&null!=row[27])
							pstmt.setString(28, row[27]);//CATEGORIESNAME8
						else
							pstmt.setString(28, null);
						pstmt.addBatch();
					}else{
						log.info("--------->lenght:"+row.length);
						log.info("--------------->data:"+line);
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
			if (con != null && !con.isClosed()) {
				pstmt.executeBatch();
				DBUtil.release(pstmt, con);
			}
			log.info("------->"+fileName + " done");
		} catch (IOException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					DBUtil.release(pstmt, con);
				}
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}

	}
	/**
	 * 外部调用接口
	 *@author DengJianhua
	 *Aug 24, 2010  9:31:18 AM
	 */
	public void doSync(){
		log.info("--------->start to import indicators");
		String[] files=getSortFileNames();
		for(String file:files){
			log.info("file-------->"+file);
			handle(file);
			modifyFileName(file);
		}
		log.info("--------->end to import indicators");
	}

	public static void main(String[] args) {
		TaskIndicators t=new TaskIndicators();
		t.doSync();
	}
}
