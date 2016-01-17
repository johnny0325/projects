package com.test;

public class SubClass extends BaseClass {
	static int value = 10;
	public SubClass(){
		System.out.println("create SubClass...");
	}
	/**
	 * π¶ƒ‹√Ë ˆ£∫
	 * @param args
	 * @return void
	 * Author£∫jlLin
	 * Aug 26, 2011 11:26:02 PM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//BaseClass bc = new BaseClass();
		//SubClass sc =(SubClass)bc;
		
		//SubClass sc = new SubClass();
		//BaseClass bc = sc;
		
		BaseClass bc = new SubClass();
		SubClass sc = (SubClass)bc;
	}

}
