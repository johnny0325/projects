package com.practise.my.java.framework.freemarker;

public class Column {
	
	String cnName="";
	String enName="";
	String type="";
	String nullAble="";
	String comment="";
	
	int columnSize=0;
	int decimalDigits=0;
	
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getType() {
		if("VARCHAR2".equals(type) || "NUMBER".equals(type)){
			if(decimalDigits > 0){
				type = type+"("+columnSize+","+decimalDigits+")";
			}else{
				type = type+"("+columnSize+")";
			}
		}
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNullAble() {
		if("YES".equals(nullAble)){
			nullAble = "是";
		}else{
			nullAble = "否";
		}
		return nullAble;
	}
	public void setNullAble(String nullAble) {
		this.nullAble = nullAble;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getColumnSize() {
		return columnSize;
	}
	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}
	public int getDecimalDigits() {
		return decimalDigits;
	}
	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

}
