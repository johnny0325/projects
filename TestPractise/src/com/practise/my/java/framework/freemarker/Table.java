package com.practise.my.java.framework.freemarker;

import java.util.List;

public class Table {
	String tableName="";
	String pk="";
	List<String> uniIndexList;
	List<String> comIndexList;
	List<String> fkList;
	
	List<Column> columnList;
	
	String remark="";
	String comIndexs="";
	String uniIndexs="";
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<String> getUniIndexList() {
		return uniIndexList;
	}
	public void setUniIndexList(List<String> uniIndexList) {
		this.uniIndexList = uniIndexList;
	}
	public List<String> getComIndexList() {
		return comIndexList;
	}
	public void setComIndexList(List<String> comIndexList) {
		this.comIndexList = comIndexList;
	}
	public List<String> getFkList() {
		return fkList;
	}
	public void setFkList(List<String> fkList) {
		this.fkList = fkList;
	}
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getComIndexs() {
		return comIndexs;
	}
	public void setComIndexs(String comIndexs) {
		this.comIndexs = comIndexs;
	}
	public String getUniIndexs() {
		return uniIndexs;
	}
	public void setUniIndexs(String uniIndexs) {
		this.uniIndexs = uniIndexs;
	}
	public List<Column> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}
}
