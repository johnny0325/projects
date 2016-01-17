package com.practise.my.constant.enums;

public enum EncodingEnum {
	GBK("GBK"),
	UTF8("UTF-8"),
	GB2312("GB2312"),
	ISO88591("ISO-8859-1");
	
	String encoding;
	private EncodingEnum(String encoding){
		this.encoding = encoding;
	}
	
	public String getEncoding(){
		return this.encoding;
	}
}
