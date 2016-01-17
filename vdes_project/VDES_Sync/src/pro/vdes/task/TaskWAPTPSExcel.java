/**
 * TaskWAPTPSExcel.java 2010-3-12 上午11:46:34
 */
package pro.vdes.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import common.util.ConfigureUtil;
import common.util.DBUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class TaskWAPTPSExcel {
	private static Logger log = Logger.getLogger(TaskWAPTPSExcel.class);
	//生成上个月的WAPTPS的库表(bwp_wap_tps_month)记录,和EXCEL文件（广东省WAP月业务量报表(201002).xls）
	//生成时间是每个月的3号到8号都运行本程序；
	public void generateDBExcel(){
		java.util.Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);//取得上个月的变量 c;
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DATE);
		
		System.out.println(year+"--"+month+"--"+day);
		if(day>=3&&day<=8){
			List<Map> list = getData(year,month);
			genarateDB(year,month,list);
			wapReport(year+"",month>9?(month+""):"0"+month);			
		}

	}
	
	public void genarateDB(int year,int month,List<Map> list){
		Connection con = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		
    	DecimalFormat df = new DecimalFormat("#"); 
    	DecimalFormat df2 = new DecimalFormat("#.00"); 
    	DecimalFormat df4 = new DecimalFormat("#.0000"); 
		try {
			con = DBUtil.getConnection("proxool.vdes");
			con.setAutoCommit(false);
			stmt = con.createStatement();
			String delete_sql = "delete from  bpw_wap_tps_month where datepart(year,statDate)="+year+" and datepart(month,statDate)="+month;
			stmt.executeUpdate(delete_sql);

			
			String insert_sql = "insert into bpw_wap_tps_month(statDate,neCode,name,hardCapacity,softCapacity,maxTps,avgTps,avgCount,radio ) values(?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(insert_sql);
			String statDate = year+"-"+((month>9)?(month+""):("0"+month))+"-01";
			for(Map map:list){
				
				
				pstmt.setDate(1,java.sql.Date.valueOf(statDate));
				pstmt.setString(2, map.get("neCode")+"");
				pstmt.setString(3, map.get("name")+"");
				pstmt.setInt(4, Integer.parseInt(map.get("hardCapacity")+""));
				pstmt.setInt(5, Integer.parseInt(map.get("softCapacity")+""));
			
				pstmt.setDouble(6, Double.valueOf((df.format(map.get("maxTps")))));
				pstmt.setDouble(7, Double.valueOf((df.format(map.get("avgTps")))));
				pstmt.setDouble(8, Double.valueOf((df2.format(map.get("avgCount")))));
				pstmt.setString(9, df4.format(map.get("radio")));
				pstmt.addBatch();


			}
			pstmt.executeBatch();
			con.commit();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.release(stmt, con);
		}
		
	}
	
	/**
	 * @param year
	 * @param month
	 * @throws IOException 
	 */
	private void wapReport(String year, String month){
		// TODO Auto-generated method stub
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("广东省WAP月业务量报表("+year+month+")");

	    //Create a row and put some cells in it. Rows are 0 based.
	    HSSFRow row = sheet.createRow((short)0);
	    //Create a cell and put a value in it.
	    HSSFCell cell1 = row.createCell((short)0);
	    HSSFCell cell2 = row.createCell((short)1);
	    HSSFCell cell3 = row.createCell((short)2);
	    HSSFCell cell4 = row.createCell((short)3);	    
	    HSSFCell cell5 = row.createCell((short)4);
	    HSSFCell cell6 = row.createCell((short)5);
	    HSSFCell cell7 = row.createCell((short)6);
	    HSSFCell cell8 = row.createCell((short)7);
	    
	    cell1.setCellValue("时间");
	    cell2.setCellValue("网关名称");
	    cell3.setCellValue("硬件容量");
	    cell4.setCellValue("软件容量");
	    cell5.setCellValue("忙时负荷（峰值）");
	    cell6.setCellValue("忙时负荷（平均值）");
	    cell7.setCellValue("业务请求数");
	    cell8.setCellValue("成功率");
	    
	    HSSFCellStyle cs = wb.createCellStyle();
	    cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
	   

	    List<Map> list = getData(Integer.parseInt(year),Integer.parseInt(month));
	    if(list!=null){
	    	DecimalFormat df = new DecimalFormat("#"); 
	    	DecimalFormat df2 = new DecimalFormat("#.00"); 
	    	DecimalFormat df4 = new DecimalFormat("#.0000"); 
	    	Map map = null;
	    	int size = list.size();
		    for(int i=0;i<size;i++){
		    	map = list.get(i);
		    	HSSFRow row1 = sheet.createRow((short)i+1);
		    	row1.createCell((short)0).setCellValue(Integer.parseInt(year)+"年"+Integer.parseInt(month)+"月");
		    	row1.createCell((short)1).setCellValue(map.get("name")+"");
		    	row1.createCell((short)2).setCellValue(Integer.parseInt(map.get("hardCapacity")+""));
		    	row1.createCell((short)3).setCellValue(Integer.parseInt(map.get("softCapacity")+""));
		    	
		    	row1.createCell((short)4).setCellValue(Double.valueOf((df.format(map.get("maxTps")))));
		    	row1.createCell((short)5).setCellValue(Double.valueOf((df.format(map.get("avgTps")))));
		    	row1.createCell((short)6).setCellValue(Double.valueOf((df2.format(map.get("avgCount")))));
		    	HSSFCell c = row1.createCell((short)7);
		    	c.setCellStyle(cs);
		    	c.setCellValue(Double.valueOf((df4.format(map.get("radio")))));
		    	

		    }
		    HSSFRow row1 = sheet.createRow((short)size+1);
		    row1.createCell((short)0).setCellValue(Integer.parseInt(month)+"月本省汇总");
	    	row1.createCell((short)1).setCellValue("-");
		    row1.createCell((short)2).setCellFormula("SUM(C2:C"+(size+1)+")");  
		    row1.createCell((short)3).setCellFormula("SUM(D2:D"+(size+1)+")"); 
		    row1.createCell((short)4).setCellFormula("SUM(E2:E"+(size+1)+")"); 
		    row1.createCell((short)5).setCellFormula("SUM(F2:F"+(size+1)+")"); 
		    row1.createCell((short)6).setCellFormula("SUM(G2:G"+(size+1)+")"); 
		    HSSFCell c = row1.createCell((short)7);
		    c.setCellStyle(cs);
		    c.setCellFormula("AVERAGE(H2:H"+(size+1)+")"); 
		    
		    Region region = new Region(1, (short)0, size, (short)0); //合并单元格
		    sheet.addMergedRegion(region);



	    }

	    sheet.setColumnWidth((short)(0), (short)(3000));
	    sheet.setColumnWidth((short)(1), (short)(5000));
	    sheet.setColumnWidth((short)(2), (short)(3000));
	    sheet.setColumnWidth((short)(3), (short)(3000));
	    sheet.setColumnWidth((short)(4), (short)(4000));
	    sheet.setColumnWidth((short)(5), (short)(4500));
	    sheet.setColumnWidth((short)(6), (short)(3000));
	    sheet.setColumnWidth((short)(7), (short)(3000));
	    try{
	    	ConfigureUtil cfg = new ConfigureUtil();
	    	 String filePath  = cfg.getValue("downloadfile")+"/tps/";
	    	 File f = new File(filePath);
	    	 if(!f.exists()) f.mkdirs();
	    	 FileOutputStream fileOut = new FileOutputStream(filePath+"广东省WAP月业务量报表("+year+month+").xls");
	 	     wb.write(fileOut);
		     fileOut.close();
	    }catch(IOException e){
	    	log.error(e.getStackTrace());
	    }
	}


	 //wap
	 public List<Map> getData(int year,int month) {
			List list = new ArrayList();
			Connection con = null;
			PreparedStatement pstmt = null;
		    String sql = "select b.neCode,b.name,b.hardCapacity,b.softCapacity,a.maxTps,a.avgTps,a.avgCount,a.radio "+
		    			" from bpw_wap_tps b left join (select neCode, max(tps) as maxTps,avg(tps) as avgTps,avg(count)as avgCount,sum(succCount)/sum(count) as radio from dbo.bpw_wap_ne_month w  where  datepart(year,statDate)="+year+" and datepart(month,statDate)="+month+" group by neCode) a"+ 
		    			" on a.neCode = b.neCode  order by b.neCode asc";

	    	
			try {
				con = DBUtil.getConnection("proxool.vdes");
				pstmt = con.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					Map map = new HashMap();
					map.put("neCode",rs.getString("neCode"));
					map.put("name",rs.getString("name"));
					map.put("hardCapacity",rs.getInt("hardCapacity"));
					map.put("softCapacity",rs.getInt("softCapacity"));
					map.put("maxTps",rs.getDouble("maxTps"));
					map.put("avgTps",rs.getDouble("avgTps"));
					map.put("avgCount",rs.getDouble("avgCount"));
					map.put("radio",rs.getDouble("radio"));
					list.add(map);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBUtil.release(pstmt, con);

			}
			return list.size()==0?null:list;
		}

public static void main(String[] argv){
	new TaskWAPTPSExcel().generateDBExcel();
}
	

}
