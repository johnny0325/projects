package com.test;

public class TestString2 {
	private static String str = "abc";   
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str1 = "a";   
        String str2 = "bc";   
        String combo = str1 + str2;   
        System.out.println(str == combo);   //false
        System.out.println(str == combo.intern());   //true

        System.out.println( "-----------------------------------------");
        String a = "abc";
        String b = "abc";
        String ab = "ab";
        String c = ab + "c";
        String d = "ab"+"c";

        System.out.println( a==b );
        System.out.println( a==c );
        System.out.println( a==d ); 
	}

}
