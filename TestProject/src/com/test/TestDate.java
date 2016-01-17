package com.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TestDate {
	public static void main(String args[]){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd"); 
		String str1 = "2011-03-30";
		String str2 = "2011-04-05";
		Calendar cal = Calendar.getInstance();
		
		try{
			Date dateEnd = sf.parse(str1);
			Date dateStart = sf.parse(str2);
			
			System.out.println("��������������������>"+daysBetween(dateStart,dateEnd));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * ȡ��ָ������N��������
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.add(Calendar.DAY_OF_MONTH, days);

		return cal.getTime();
	}
	
	/**
	 * ������������֮����������
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int daysBetween(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Math.abs(Integer.parseInt(String.valueOf(between_days)));
	}
}
