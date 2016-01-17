package com.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestDateFormat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(format.format(cal.getTime()));
		
		String statDate = "2010-07-27";
		System.out.println(statDate.substring(0,4)+statDate.substring(5,7)+statDate.substring(8,10));
	}

}
