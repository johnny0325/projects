package com.practise.my.java.framework.freemarker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.practise.my.constant.Constants;
import com.practise.my.constant.enums.EncodingEnum;
import com.practise.my.util.DatabaseMetaDataUtil;
import com.practise.my.util.FileUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestCreateDoc {
	private static final Logger logger = LoggerFactory.getLogger(TestCreateDoc.class);
	
	private Configuration cfg;
	private Writer out = null;
	
	Map<String,Table> tableMap = new HashMap<String,Table>();
	List<Table> tableList = new ArrayList<Table>();
	List<String> tableOrderList = new ArrayList<String>();
	
	public TestCreateDoc(){
		init();
	}

	public void init() {
		cfg = new Configuration();
	}
	
	public void loadTableInfos(){
		DatabaseMetaDataUtil databaseMetaDataUtil = null;
		try {
			logger.info("获取数据库表信息开始.");
			databaseMetaDataUtil = new DatabaseMetaDataUtil();
//			databaseMetaDataUtil.printAllSchemas();//PAYDTEST
//			databaseMetaDataUtil.printDataBaseInformations();
			//获取所有表名
			tableList = databaseMetaDataUtil.getAllTableList("PAYDTEST");
//			tableList = databaseMetaDataUtil.getTableInfo("PAYDTEST","CUSTACCOUNTINFO");
			int n=0;
			for(Table table:tableList ){//ORA-01000
				logger.info("开始获取数据库表["+table.getTableName()+"]信息.");
				//获取表主键
				String pk = databaseMetaDataUtil.getAllPrimaryKeys("PAYDTEST", table.getTableName());//ORA-01000
				//获取表索引
				List[] idxs = databaseMetaDataUtil.getIndexInfo("PAYDTEST", table.getTableName(),pk);//ORA-00604
				//获取表外键
				List<String> fk = databaseMetaDataUtil.getAllImportedKeys("PAYDTEST", table.getTableName());
				//获取表列
				List<Column> columns = databaseMetaDataUtil.getTableColumns("PAYDTEST", table.getTableName());
				table.setPk(pk==null?"":pk);
				table.setFkList(fk);
				table.setComIndexList(idxs[0]);
				table.setUniIndexList(idxs[1]);
				table.setColumnList(columns);
				tableMap.put(table.getTableName(), table);
				logger.info("获取数据库表["+table.getTableName()+"]信息完成.");
				if(n++>50){//ORA-01000 ORA-00604
					databaseMetaDataUtil.colseCon();
					databaseMetaDataUtil.getDatabaseMetaData();
					n=0;
				}
			}
			logger.info("获取数据库表信息结束.");
		} catch (Exception e) {
			logger.info("获取数据库表信息异常:"+e.getMessage());
		} finally{
			if(databaseMetaDataUtil != null){
				databaseMetaDataUtil.colseCon();
			}
		}
	}
	
	public void loadTableOrderList(){
		try {
			String line = null;
			List<String> lines = org.apache.commons.io.FileUtils.readLines(new File("paydtest_tableList3_20140415_new.txt"),EncodingEnum.UTF8.getEncoding());
			for (int i = 0; i < lines.size(); i++) {
				line = lines.get(i);
				if(!StringUtils.isEmpty(line) && line.contains("(")){
					tableOrderList.add(line);
					System.out.println("order:"+line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 按库表分域调整数据库表的顺序
	 * 
	 * @Date 2014-4-14下午6:18:17
	 * @return void
	 */
	public void adjustTableList(){
		List<Table> tables = new ArrayList<Table>();  
		String tableName = "";
		String enName;
		String tmpEnName;
		Table table = null;
		for (int i = 0; i < tableOrderList.size(); i++) {
			tableName = tableOrderList.get(i);
			enName = tableName.substring(tableName.indexOf("("));
			tmpEnName = enName.substring(1, enName.length()-1).toUpperCase();
//			logger.info(tmpEnName);
			table = tableMap.get(tmpEnName);
			if(table == null){
				logger.info("表["+tmpEnName+"]在数据库不存在!");
			}else{
				table.setTableName(tableName);
				tables.add(table);
				tableMap.remove(tmpEnName);
			}
		}
		
		for (Map.Entry<String, Table> entry:tableMap.entrySet()) {
			table = entry.getValue();
			table.setTableName("("+table.getTableName()+")");
			tables.add(table);
			logger.info("表["+table.getTableName()+"]无序,放到最后.");
		}
		tableList = tables;
		
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < tableList.size(); i++) {
			content.append(tableList.get(i).getTableName()).append(Constants.RET);
		}
		FileUtils.writeFile("paydtest_tableList3_20140415_new2.txt", content.toString(), false);
		logger.info("表["+tableList.size()+"]顺序调整完成.");
	}
	/**
	 * 按库表分域调整数据库表的顺序
	 * 
	 * @Date 2014-4-14下午6:18:17
	 * @return void
	 */
	public void adjustTableList2(){
		List<String> tables = new ArrayList<String>();  
		String tableName = "";
		String enName;
		String tmpEnName;
		Table table = null;
		for (int i = 0; i < tableOrderList.size(); i++) {
			tableName = tableOrderList.get(i);
			enName = tableName.substring(tableName.indexOf("("));
			tmpEnName = enName.substring(1, enName.length()-1).toUpperCase();
			table = tableMap.get(tmpEnName);
			if(table == null){
				logger.info("表["+tmpEnName+"]在数据库不存在!");
			}else{
				tableName = table.getRemark()+"("+table.getTableName()+")";
				tableMap.remove(tmpEnName);
			}
			tables.add(tableName);
		}
		
		for (Map.Entry<String, Table> entry:tableMap.entrySet()) {
			table = entry.getValue();
			tableName = table.getRemark()+"("+table.getTableName()+")";
			tables.add(tableName);
			logger.info("表["+table.getTableName()+"]无序,放到最后.");
		}
		
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < tables.size(); i++) {
			content.append(tables.get(i)).append(Constants.RET);
		}
		FileUtils.writeFile("D:/worknote/最近/雁联/广州药交所项目/paydtest_tableList3_20140415_new.txt", content.toString(), false);
		logger.info("表["+tableList.size()+"]顺序调整完成.");
	}
	
	public void process() {
		try {
			Map map = new HashMap();
			map.put("tableList", tableList);
//			map.put("tableList", createTableList());
			
			/*
			 * tableTample.xml是在word文档中制作好模板，然后另存为.xml格式而生成的
			 * 生成后，在这个xml文件中使用freemarker的标签定义要输出数据的地方
			 */
			Template t = cfg.getTemplate("template/tableTample.xml");
			File file = new File("D:/test_tableListTample.xml");
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			t.process(map, out);
			logger.info("数据库文档生成完成!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(null != out){
				try {
					out.close();
					out= null;
				} catch (IOException e) {
				}
			}
		}
	}
	
	public List<Table> createTableList(){
		List<Table> list = new ArrayList<Table>();
		for (int i = 0; i < 10; i++) {
			Table table = new Table();
			table.setTableName("TableName"+i);
			table.setPk("pk"+i);
//			table.setUniIndex("uniIndex"+i);
//			table.setComIndex("comIndex"+i);
//			table.setFk("fk"+i);
			table.setColumnList(createColumnList());
			list.add(table);
		}
		return list;
	}
	
	public List createColumnList(){
		List<Column> list = new ArrayList<Column>();
		for (int i = 0; i < 10; i++) {
			Column column = new Column();
			column.setCnName("cnName"+i);
			column.setEnName("enName"+i);
			column.setType("type"+i);
			column.setNullAble("nullAble"+i);
			column.setComment("commnent"+i);
			list.add(column);
		}
		return list;
	}
	
	public void test(){
		loadTableInfos();//获取表元数据信息
		loadTableOrderList();//加载表顺序(分域)
		adjustTableList();//调整表顺序
		process();//生成xml(word格式)
	}
	
	public static void main(String[] args) {
//		new TestCreateDoc().process();
		new TestCreateDoc().test();
	}
}
