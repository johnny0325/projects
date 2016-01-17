/**
 * TaskTXT.java 2010-5-26 下午04:44:02
 */
package pro.vdes.task;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import common.util.ConfigureUtil;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class TaskMBWSMS {
	private static Logger log = Logger.getLogger(TaskMBWSMS.class);
	/**
	 * 外界调用的主要方法
	 *
	 */
	public void doSync(){
		log.info("sms　检测");
		//加载各个文件所对于的数据到库中；
		String ftpconf = new ConfigureUtil().getValue("ftpconf")+"/mbw/sms";
		File dir = new File(ftpconf);
		final Set<String> set = getFileName();
		ArrayList<String> filenames = getAllFileName(dir,null);
		for (String filename : filenames) {
			if(!set.contains(filename)){
				log.info(filename);
				save(filename);
			}
			
		}

		
	}
	private ArrayList<String> getAllFileName(File dir,ArrayList v){
		if(v==null) v = new ArrayList();
		if(dir.isDirectory()){
			File[] fl = dir.listFiles();
			for(File f:fl){
				getAllFileName(f,v);
			}
		}else if(dir.getName().startsWith("SMS_")&&dir.getName().toLowerCase().endsWith("zip")){//文件名形如：SMS_0626.zip
			v.add(dir.getName());
		}
		return v;
		
	}
	/**
	 * 
	 * @return 得到FTP的文件名；
 	 */
	private Set<String> getFileName(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Set<String> set = new HashSet<String>();
		try {
			conn = DBUtil.getConnection("proxool.vdes");
			String sql = "select filename from MBW_SMSUploadFile where type='ftp' ";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()){
				set.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.release(rs,pstmt, conn);
		}
		return set;
		
	}
	/**
	 * 将FTP过来的SMS_0528_1.xls保存到主表中MBW_SMSUploadFile
	 *
	 */

	private void save(String fileName){
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBUtil.getConnection("proxool.vdes");
			String sql = "insert into MBW_SMSUploadFile(statDate,status,filename,factname,type) values(getdate(),0,?,?,'ftp')";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,fileName);
			pstmt.setString(2,fileName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.release(pstmt, conn);
		}
		
	}

	public static void main(String[] argv) {
		TaskMBWSMS tt = new TaskMBWSMS();
		tt.doSync();
	}
}
