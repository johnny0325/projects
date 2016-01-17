package com.example;

import org.omg.CORBA.PRIVATE_MEMBER;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class RenMingBi {
	
	private static final char[] data = new char[]{'��','Ҽ','��','��','��','��','½','��','��','��'};
	
	private static final char[] units = new char[]{'Ԫ','ʰ','��','Ǫ','��','ʰ','��','Ǫ','��'};
	
	/**
	 * RenMingBi.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-5-21 ����06:38:12
	 */
	public static void main(String[] args) {
		System.out.println(convert(2600));

	}
	
	public static String convert(int money) { 
		StringBuffer sb = new StringBuffer();
		int unit = 0;
		
		while(money != 0) {
			sb.insert(0, units[unit++]);
			int number = money%10;
			sb.insert(0, data[number]);
			money/=10;
		}
		return sb.toString();
	}
}
