package pro.vdes.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import common.util.CfgUtil;
import common.util.DBUtil;
import common.util.DateUtil;

import pro.vdes.task.TaskBill;

public class MainSync_nx {
	private static Logger log = Logger.getLogger(TaskBill.class);

	private static String ftpconf = CfgUtil.getValue("ftpconf") + "/mbw/wap";


	/**
	 * 如果文件是以".ok"结尾的就过滤掉,并将剩下的文件按日期时间升序排序, 得到还没处理的文件列表
	 * 
	 * @param fileNames
	 * @return
	 */
	private String[] getSortFileNames() {

		List<String> noSortFile = new ArrayList<String>();
		String fileName = null;
		// String ftpconf = CfgUtil.getValue("ftpconf");
		// String ftpconf = "/export/mmds/hamob_data";
		File dir = new File(ftpconf);
		System.out.println("fileDir:" + dir.getName());
	//	final HashSet hs = findEndFile(); 
		String[] fileNames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				
				// TODO Auto-generated method stub
				if (name.startsWith("bill_") && !name.endsWith(".ok")
						//&& name.endsWith(".txt")
						&& name.endsWith("nx.txt")//专门处理深圳,江门，广州的
						//&& hs.contains(name.substring(5, 11))
						&& (name.substring(0, 9).compareTo("bill_0801") > 0)
						) {// bill_开头,不以'.ok'结尾的

					return true;
				}
				return false;

			}

		});
		if (fileNames != null)
			for (int i = 0; i < fileNames.length; i++) {
				fileName = fileNames[i];
				if (fileName != null) {
					noSortFile.add(fileName);// 剩下不以'.ok'结尾的
				}
			}
		String[] sortFile = new String[noSortFile.size()];
		Collections.sort(noSortFile);// 排序

		for (int i = 0; i < noSortFile.size(); i++) {
			sortFile[i] = noSortFile.get(i);
		}
		return sortFile;
	}
	/**
	 * 找_end结尾的,以表示这天这个小时的数据传输完了
	 * @return
	 */
	private HashSet findEndFile() {
		File dir = new File(ftpconf);
		String[] fileNames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith("_end")) {// 072616_end
					return true;
				}
				return false;

			}

		});
		HashSet hs = new HashSet();
		//String currentDate = DateUtil.preDay(DateUtil.getCurrentDateStr("yyyyMMdd"), 3);//前三天的
		//currentDate = currentDate.substring(4);
		System.out.println("--->find end");
		for (int i = 0; i < fileNames.length; i++) {
			//if((fileNames[i].substring(0, 4).compareTo(currentDate) >= 0)){
				hs.add(fileNames[i].substring(0, fileNames[i].indexOf("_")));// 取072612.end的日期小时
				
			//}
			
		}
		return hs;
	}

	/**
	 * 将已经处理的文件,在文件名加上".ok"结尾
	 * 
	 * @param fileName
	 */
	private void modifyFileName(String fileName) {
		File file = new File(ftpconf + File.separator + fileName);// 文件
		
		file.renameTo(new File(ftpconf + File.separator + "done_data"+File.separator+fileName + ".ok"));// 改名,并放到done_data文件夹下
		//file.renameTo(new File(ftpconf + File.separator + fileName + ".ok"));// 改名

	}
	
	/**
	 * 删除实时监控数据
	 */
	private void deleteReal(){
		Connection con=null;
		Statement stmt=null;
		ResultSet rs=null;
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			String sql="select count(1) from mmds_realtime";
			rs=stmt.executeQuery(sql);
			int sum=0;
			if(rs.next()){
				sum=rs.getInt(1);
			}
			rs.close();
			stmt.close();
			
			if(sum-5000>0){
				int cha=sum-5000;
				String delete="delete from mmds_realtime  "+
				" where rowid in ( "+
				" select * from "+ 
					"(select rowid from mmds_realtime order by statdate asc) "+
				" where rownum <="+cha+" )";
				stmt = con.createStatement();
				stmt.execute(delete);
			}
			DBUtil.release( stmt, con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.release(rs, stmt, con);
		}
		
		
	}
	/**
	 * 创建行为数据表
	 * @ahthor DengJianhua
	 * 2010-7-21下午02:55:25
	 * @param tableName
	 */
	private void createBehaviorTable(String tableName) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		
		String sql = 
			"CREATE TABLE "+tableName+"("+
					
					" wapDate DATE,"+//交集的时候用来放wap的时间
					" callerPhone numeric(19) ,"+
					" calledPhone numeric(19) ,"+
					" domain varchar(256) ,"+
					" URL varchar(1500) ,"+
					" type int,"+
					" city varchar(50),"+
					" brand varchar(50) ,"+
					" terminal varchar(50) ,"+
					" os varchar(50),"+
					" statDate DATE,"+
					" feature varchar(50)"+
				")";
		
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, con);
		}
		log.info(tableName);
		
	}
	/**
	 * 将话单原始数据跟病毒库中的已发布病毒匹配,如果匹配就将这条记录插进实时监控表和行为数据表,并将话单的状态改为已匹配
	 * @param currentDate
	 */
	private void matchVirus(String currentDate){
		Connection con=null;
		Statement stmts=null;
		
		ResultSet rsSum=null;
		String table="BILL_"+currentDate;//wap表
		String tableName="mbw_behavior_"+currentDate;//行为数据表
		
		String sumSql="select count(1) from "+table+" b "+
		"left join  mbw_wap_filter f on(f.feature=b.feature) "+
		"where f.status=1 and b.status=1";//匹配到的
		int sum=0;//BILL表总量
		int start=0;//分页的开始
		int end=0;//分页的结束
		con = DBUtil.getConnection("proxool.mmds");
		try {
			stmts = con.createStatement();
			rsSum=stmts.executeQuery(sumSql);
			if(rsSum.next()){
				sum=rsSum.getInt(1);
			}
			int fen=(int)(Math.ceil((double)sum/2000));//分页的次数
			log.info("分页总数:"+fen);
			if (con!=null&&!con.isClosed()) {//
				DBUtil.release(rsSum,stmts, con);
			
			}
			if(fen>0){
				createBehaviorTable(tableName);//建行为数据表
			}
			for(int k=1;k<=fen;k++){
				log.info("第"+k+"分页");
				boolean flag=false;
				int page=fen<=3?fen:3;
				if(k<=page){
					flag=true;
				}
				start=(k-1)*2000+1;
				end=k*2000;
				String sql="SELECT * FROM (SELECT A.*, ROWNUM RN FROM (" +
				"select b.callerphone,b.statdate,b.domain,b.URL,b.feature,f.name_cn,f.id,b.city,b.brand,b.terminal,b.os from "+table+" b "+
				"left join  mbw_wap_filter f on(f.feature=b.feature) "+
				"where f.status=1 and b.status=1 order by statdate desc"+//
				") A ) WHERE RN BETWEEN "+start+" AND  "+end+"";
				
				con = DBUtil.getConnection("proxool.mmds");
				Statement stmt = null;
				ResultSet rs=null;
				stmt=con.createStatement();
				rs=stmt.executeQuery(sql);
				PreparedStatement pstmtReal=null;
				String insertSql="insert into mmds_realtime(callerphone,VIRUSNAME,STATDATE,ID) values(?,?,?,?)";//实时监控
				pstmtReal = con.prepareStatement(insertSql);
				
				String insertBehavior="insert into "+tableName
				+" (wapDate,callerPhone,domain,URL,type,statDate,feature,city,brand,terminal,os) " //,terminal,os
				+"values(?,?,?,?,?,?,?,?,?,?,?)";//行为特征库
				PreparedStatement pstmtBehavior=null;
				pstmtBehavior = con.prepareStatement(insertBehavior);
				while(rs.next()){
					//System.out.println("callerphone------->"+rs.getLong("callerphone"));
					//实时数据
					if(flag){//实时表只差6000条一下
						pstmtReal.setLong(1, rs.getLong(1));//callerphone
						pstmtReal.setString(2, rs.getString(6));//name
						pstmtReal.setTimestamp(3, rs.getTimestamp(2));//statdate
						pstmtReal.setString(4, rs.getString(7));//virus id
						pstmtReal.addBatch();
					}
					
					//行为数据
					pstmtBehavior.setTimestamp(1,rs.getTimestamp("statdate"));
					pstmtBehavior.setLong(2, rs.getLong("callerphone"));//callerPhone
					pstmtBehavior.setString(3, rs.getString("domain"));//domain
					if(null!=rs.getString("URL"))
						pstmtBehavior.setString(4, rs.getString("URL"));//URL
					else
						pstmtBehavior.setString(4, "");
					pstmtBehavior.setInt(5, 3);//type
					pstmtBehavior.setTimestamp(6, rs.getTimestamp("statdate"));//statDate
					pstmtBehavior.setString(7, rs.getString("feature")); //feature
					pstmtBehavior.setString(8, rs.getString("city")); //feature
					pstmtBehavior.setString(9, rs.getString("brand")); //feature
					pstmtBehavior.setString(10, rs.getString("terminal")); //feature
					pstmtBehavior.setString(11, rs.getString("os")); //feature
					pstmtBehavior.addBatch();
				}
				pstmtReal.executeBatch();
				pstmtBehavior.executeBatch();
				DBUtil.releaseStatement(pstmtReal);
				DBUtil.releaseStatement(pstmtBehavior);
				DBUtil.release(rs,stmt, con);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(!con.isClosed()){
					con.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	/**
	 * 外界调用的主要方法；
	 * 
	 */
	public void doSync() {
		String today = DateUtil.format(new Date(), "MMdd");
		String[] filenames = null;
		String tableName = null;
		String currentDate=null;
		filenames = getSortFileNames();// 获得文件名,并将以处理的('.ok'结尾)文件过滤,而且时间升序排序
		if (filenames != null && filenames.length > 0) {
			for (String fileName : filenames) {
				tableName = getTableName(fileName);
				currentDate = fileName.substring(5, 9);
				// if (currentDate.compareTo(today) < 0) {//要处理的文件日期小于当天才会处理
				createTable(tableName);
				handle(fileName);// 将文件内容导入数据库
				modifyFileName(fileName);// 将处理好的文件改名
				//matchVirus(currentDate);//最新的中毒
				deleteReal();//清洗实时数据
				// }
			}// end for 
		}//end if
		
	}

	private void setCity(String tableName) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		String sql1 = "update  "
				+ tableName
				+ "  set city =("
				+ "select city from dbo.BPW_HLR where code=substring(cast(callerphone as varchar(30)),3,7)"
				+ ")";
		String sql2 = "update  " + tableName
				+ " set city='其它' where city is null";
		try {
			con = DBUtil.getConnection("proxool.mmds");
			stmt = con.createStatement();
			stmt.execute(sql1);
			stmt.execute(sql2);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DBUtil.release(stmt, con);
		}
		log.info("setCity");

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

			String sql = "create table " + tableName + "("
					+ "statDate         DATE," 
					+ "callerPhone      NUMBER(13),"
					+ "url   varchar(1500)," 
					+ "domain    varchar(128),"
					+ "feature  varchar(50)," 
					+ "status  INTEGER," 
					+" city varchar(50),"
					+" brand varchar(50) ,"
					+" terminal varchar(50) ,"
					+" os varchar(50)"
					+")";
			stmt.execute(sql);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			System.out.println("---------");
			DBUtil.release(stmt, con);
			System.out.println("------rtr---");
		}
		// log.info("创建表:"+tableName);

	}

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
			String insertSql = "insert into "
					+ getTableName(fileName)
					+ "(statDate,callerPhone,url,domain,feature,status,city,brand,terminal,os) values(?,?,?,?,?,?,?,?,?,?)";
			String domain = "";
			String currentDate = fileName.substring(5, 9);
			String tableName="mbw_behavior_"+currentDate;//行为数据表
			createBehaviorTable(tableName);//建行为数据表
			PreparedStatement pstmtBehavior=null;
			PreparedStatement pstmtReal=null;
			while ((line = br.readLine()) != null) {
				try {

					// 对数据库连接的优化
					if (con == null || i % LEN == 0) {
						con = DBUtil.getConnection("proxool.mmds");
						pstmt = con.prepareStatement(insertSql);
						
						String inSql="insert into mmds_realtime(callerphone,VIRUSNAME,STATDATE,ID) values(?,?,?,?)";//实时监控
						pstmtReal = con.prepareStatement(inSql);
						
						String insertBehavior="insert into "+tableName
						+" (wapDate,callerPhone,domain,URL,type,statDate,feature,city,brand,terminal,os) " //,terminal,os
						+"values(?,?,?,?,?,?,?,?,?,?,?)";//行为特征库
						
						pstmtBehavior = con.prepareStatement(insertBehavior);
					}
					++i;
					String[] row = line.split(",");
					// 2010-06-20
					// 23:55:08.967,8615818492859+,http://lrc.ttpod.com/q?title=%E4%BD%95%E5%BF%85%E5%9C%A8%E4%B8%80%E8%B5%B7%20www.2651.cn&artist=%E5%BC%A0%E6%9D%B0%20www.2651.cn&raw=2&v=v3.7.0.2010042712&uid=dfgdihacaaiedce&mid=2000da64&f=f1&s=s030&imsi=460028182259225&mediatype=mp3&duration=274&bitrate=129&srate=44100,TTPodHttpQhk1.1
					if (row.length == 4 || row.length==5) {
						if (row[0].equals("NULL") || row[1].equals("NULL")
								|| row[2].equals("NULL")) {
							// log.warn("这个话单被过滤："+line);
						} else if (!row[2].startsWith("/")
								&& !row[2].startsWith("http:/?")
								&& !row[2].startsWith("http://&")) {// 以http:/?
							// 或者http://&开头的都不考虑。
							domain = row[2].replace("http://", "");
							if (domain.indexOf("/") != -1)
								domain = domain.substring(0, domain
										.indexOf("/"));
							else
								domain = "";
							pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(row[0]));
							String callerPhone = row[1];
							if (callerPhone != null) {
								callerPhone = callerPhone.replace("+", "");
								callerPhone = callerPhone.trim();
								if (callerPhone.length() != 13)
									continue;
								// callerPhone=callerPhone.substring(0,
								// callerPhone.length()-1);
							}

							pstmt.setLong(2, Long.parseLong(callerPhone));
							pstmt.setString(3, row[2]);
							pstmt.setString(4, domain);
							String feature=getFeature(domain);//特征
							pstmt.setString(5, feature);

							String code=callerPhone.substring(2,9);
							Statement stmt1=con.createStatement();//b.callerphone,b.statdate,b.domain,b.URL,b.feature,f.name_cn,f.id,b.city
							ResultSet rs1=stmt1.executeQuery("select city,brand from BPW_HLR b where b.code="+code);
							String city=null;
							String brand=null;
							if(rs1.next()){
								city=rs1.getString(1)+"市";
								brand=rs1.getString(2);
								pstmt.setString(7, rs1.getString(1)+"市");//city
								pstmt.setString(8, rs1.getString(2));
							}else{
								pstmt.setString(7, null);//city
								pstmt.setString(8, null);
							}
							rs1.close();
							stmt1.close();
							//
							Statement stmt2=con.createStatement();
							ResultSet rs2=stmt2.executeQuery("select terminal,os from mmds_terminal_os t where t.callerphone="+callerPhone.substring(2));
							String terminal=null;
							String os=null;
							
							if(rs2.next()){
								terminal=rs2.getString(1);
								os=rs2.getString(2);
								pstmt.setString(9, rs2.getString(1));//terminal
								pstmt.setString(10, rs2.getString(2));//os
							}else{
								pstmt.setString(9, null);//terminal
								pstmt.setString(10, null);//os
							}
							rs2.close();
							stmt2.close();
							//
							stmt1=con.createStatement();
							rs1=stmt1.executeQuery("select name_cn,id from mbw_wap_filter where feature='"+feature+"' and status=1");
							
							if(rs1.next()){
								pstmt.setInt(6, 1);//匹配到,状态为1
								//实时数据
								pstmtReal.setLong(1, Long.parseLong(callerPhone));//callerphone
								pstmtReal.setString(2, rs1.getString(1));//name
								pstmtReal.setTimestamp(3, java.sql.Timestamp.valueOf(row[0]));//statdate
								pstmtReal.setString(4, rs1.getString(2));//virus id
								pstmtReal.addBatch();
							
								//行为数据
								pstmtBehavior.setTimestamp(1,java.sql.Timestamp.valueOf(row[0]));
								pstmtBehavior.setLong(2, Long.parseLong(callerPhone));//callerPhone
								pstmtBehavior.setString(3,domain);//domain
								pstmtBehavior.setString(4,row[2]);
								pstmtBehavior.setInt(5, 3);//type
								pstmtBehavior.setTimestamp(6,java.sql.Timestamp.valueOf(row[0]));//statDate
								pstmtBehavior.setString(7,feature); //feature
								pstmtBehavior.setString(8, city); //feature
								pstmtBehavior.setString(9, brand); //feature
								pstmtBehavior.setString(10, terminal); //feature
								pstmtBehavior.setString(11, os); //feature
								pstmtBehavior.addBatch();
							}else{
								pstmt.setInt(6, 0);//没匹配到,状态为0
							}
							pstmt.addBatch();//???
							rs1.close();
							stmt1.close();
							

						}
					}

					if (i % LEN == 0) {
						System.out.println(i);
						pstmtReal.executeBatch();
						pstmtBehavior.executeBatch();
						DBUtil.releaseStatement(pstmtReal);
						DBUtil.releaseStatement(pstmtBehavior);
						pstmt.executeBatch();
						DBUtil.release(pstmt, con);
					}

				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
				}
			}
			br.close();
			log.info("文件长度-->" + i);
			if (con != null && !con.isClosed()) {
				pstmtReal.executeBatch();
				pstmtBehavior.executeBatch();
				DBUtil.releaseStatement(pstmtReal);
				DBUtil.releaseStatement(pstmtBehavior);
				pstmt.executeBatch();
			}
			log.info(fileName + "文件处理完毕!");
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
	 * mtk2.wapdfw.com 就得到 wapdfw.com 做为特征
	 * 
	 * @param url
	 * @return
	 */
	private String getFeature(String url) {
		String feature = null;
		String regEx = "(\\w+:)?(([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.)?(com.cn|"
				+ "com|cn|net|org)(:\\d*)?";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(url);
		if (m.find()) {
			/*
			 * for(int i =0;i<m.groupCount();i++){
			 * System.out.println(i+"--"+m.group(i)); }
			 */
			feature = m.group(2) + m.group(4);
		} else {
			regEx = "((\\d+)\\.(\\d+)\\.(\\d+))\\.(\\d+)";
			p = Pattern.compile(regEx);
			m = p.matcher(url);
			if (m.find()) {
				feature = m.group(1);
			} else {
				feature = url;// 当两种匹配都不通过时，就将原来URL直接返回做为特征域名。
			}

		}
		return feature;

	}

	/**
	 * @param fileName
	 * @return
	 */
	private String getTableName(String fileName) {
		// TODO Auto-generated method stub

		return "bill_" + fileName.substring(5, 9);// bill_062801_dg.txt
		// 得到'bill_0628'表名
	}

	public BufferedReader getSource(String fileName)
			throws FileNotFoundException {
		// String ftpconf = new ConfigureUtil().getValue("ftpconf");
		File srcFile = new File(ftpconf, fileName);
		if (!srcFile.exists()) {
			log.info("no file");
			return null;
		}
		return new BufferedReader(new FileReader(srcFile));
	}

	public static void main(String[] argv) {
		System.out.println("MainSync_bill start----");
		Timer vdesTimer = new Timer();
		
		//wap话单原始数据入数据库
		final MainSync_nx taskBill = new MainSync_nx();
		TimerTask taskBillRun = new TimerTask() {
			public void run() {
				taskBill.doSync();
			}
		};
		vdesTimer.schedule(taskBillRun, 1000,1 * 60 * 1000);//10分钟做一次
		//MainSync_bill t=new MainSync_bill();
		//t.doSync();
		//t.matchVirus("0728");
		//t.deleteReal();
		
	}
}
