package com.test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestString {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "abc";   
        String str1 = "abc";   
        String str2 = new String("abc");   
        System.out.println(str == str1);   //true
        System.out.println(str1 == "abc");   //true
        System.out.println(str2 == "abc");   //false
        System.out.println(str1 == str2);    //false
        System.out.println(str1.equals(str2));   //true
        System.out.println(str1 == str2.intern());   //true
        System.out.println(str2 == str2.intern());   //false
        System.out.println(str1.hashCode() == str2.hashCode());   //true
        
        try{
        	/*SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date date = sf.parse("2010-7-28 00:00:00");
            String time = sf.format(date);
            System.out.println(time);  
            System.out.println(Timestamp.valueOf(time));   */
            
        	/*中国移动拥有号码段为:139,138,137,136,135,134,159,158,157(3G),151,150,188(3G),187(3G);13个号段*/

        	String regEx = "^1(([3][456789])|([5][01789])|([8][78]))[0-9]{8}$";
        	String phone = "15360819220";
        	Pattern p = Pattern.compile(regEx);
    		Matcher m = p.matcher(phone);
    		if (m.find()) {
    			System.out.println("phone==>"+phone);
    		}else{
    			System.out.println("手机号码格式不正确");
    		}
        }catch(Exception e){
        	e.printStackTrace();
        }
        int a=1;
        int b=1;
        if(a==b){
        	System.out.println("ddddddd");
        }
	}

}
