/**
 * DocFactory.java 2009-7-9 ÏÂÎç05:27:09
 */
package config;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import sample.xmlbean.RootDocument;
import sample.xmlbean.RootDocument.Root.Time;
import sample.xmlbean.RootDocument.Root.Table.Field;

import common.util.ConfigureUtil;
import common.util.RegUtil;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class DocParse {
	
	private RootDocument doc ;
	private String xmlFile;
	private final static String modiReg=".*(alter|drop)\\stable\\s(\\w+)\\s.*";
	public DocParse(String file) throws XmlException, IOException{
		String basePath = new ConfigureUtil().getValue("gatherconf");
		File xmlFile = new File(basePath + "/"+file);
	    doc = RootDocument.Factory.parse(xmlFile);
	    this.xmlFile = file;

	    
	}
	public String getXmlFile(){
		return xmlFile;
	}
	public String getSuffix(){
		return doc.getRoot().getTable().getSuffix();
	}
	public String getSrcDB(){
		return doc.getRoot().getDb().getSrc();
	}
	
	public String getDesDB(){
		return doc.getRoot().getDb().getDes();
	}
	
	public String getTableName(){
		String  prefix = doc.getRoot().getTable().getName();
		String suffix = doc.getRoot().getTable().getSuffix();
		if(suffix==null){
			return prefix;
		}
		return prefix+"_"+suffix;
	}	
	public Field[] getFields(){
		return doc.getRoot().getTable().getFieldArray();
	}
	
	public Time getTime(){
		return doc.getRoot().getTime();
	}
	
	
	public String createSql(){
		StringBuffer sql = new StringBuffer("create table  "+getTableName()+"(id numeric(18,0) identity(1,1) not null,");
		Field[] fields = getFields();
		for(Field f:fields){
				sql.append(f.getStringValue()).append(" ").append(f.getType()).append(",");
		}
		sql.append("constraint pk_"+getTableName()+" primary key (id)");
		sql.append(")");
		return sql.toString();
	}
	public String selectSql(){
		return doc.getRoot().getTable().getSql();	
	}
	public String insertSql(){
		String str="";
		String field="";
		Field[] fields = getFields();
		for(int i=0;i<fields.length;i++){
				field+=","+fields[i].getStringValue();
				str+=",?";				
		}
		str = str.substring(1);
		field = field.substring(1);
		return ("insert into "+getTableName()+"("+field+") values("+str+")");

	}
	public String modiSql(){
		String sql = doc.getRoot().getModi();
		if(sql!=null){
			sql = sql.toLowerCase();
			String[] arr = RegUtil.getGroups(sql, modiReg);
			if(arr!=null&&arr.length==3){
				sql = sql.replace(arr[2], getTableName());
			}
			
		}	
		return sql;
	}



	
}
