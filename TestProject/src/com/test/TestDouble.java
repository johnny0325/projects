package com.test;

import java.text.DecimalFormat;

public class TestDouble {
	public static void main(String[] args) {
		double value = 123456789123456.789;
		DecimalFormat df = new DecimalFormat("0.00");
		System.out.println(df.format(value));
		 System.out.println(String.format("%.3f", value));
		
	}

}
