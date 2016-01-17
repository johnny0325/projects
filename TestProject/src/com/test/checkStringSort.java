package com.test;

import java.util.Arrays;
import java.util.List;


public class checkStringSort {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：ljLin
	 * Mar 21, 2011 4:35:09 PM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] choiceGroupArray = {"f8467fa2-2b54981b-012b-549925ae-0202","f8467fa2-2b54981b-012b-549925ba-020f","f8467fa2-2d71a568-012d-b4e99e2b-0f99"};
		String[] sourceArray = {"f8467fa2-2b54981b-012b-549925ba-020f","f8467fa2-2b54981b-012b-549925bf-0212","f8467fa2-2b54981b-012b-549925ae-0202"};
		int counts = 0;
		
		Arrays.sort(choiceGroupArray);
        Arrays.sort(sourceArray);//将两个数组先进行升序排序
        int length1 = choiceGroupArray.length;
        int length2 = sourceArray.length;
        if(length1 != length2) {
           System.out.println("长度相等");
          
        }else {
            for(int i = 0;i<length2;i++) {
            	System.out.println("choiceGroupArray"+i+" ==>"+choiceGroupArray[i]);
            	System.out.println("sourceArray"+i+" ==>"+sourceArray[i]);
                if(choiceGroupArray[i].equals(sourceArray[i])){
                	++counts;
                	System.out.println("mmmm");
                }
            }
            
            if(counts == sourceArray.length){
            	System.out.println("相等");
            }
        }
        
        String phoneNumber= "8615820270429";
        System.out.println(phoneNumber.substring(2));
		
	}

	
}
