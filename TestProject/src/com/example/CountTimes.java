package com.example;

import java.util.HashMap;
import java.util.Map;

/**
 * 题目：给你一个字符串，包含了空格等标点符号，要你计算出出现次数最多的字母和该字母出现的次数。 
 * 提示：ASCII中A~Z对应的是65-90，a~z对应的是97-122
 * CountTimes
 * Author：jlLin
 * Aug 29, 2011  11:57:22 AM
 * Copyright 华仝九方科技有限公司
 */
public class CountTimes {
	public static void main(String[] args) {      
        Map<String,Long> charTimesMap = new HashMap<String,Long>();  
          
        String str = "hello wolrd wlllkdsfhksadfls?sdfls sdf.pqyutgvAAAxzsdfs lsdfj,ljsfd  ajfdsak sfksjdfisfsdkfj lsdfjsidf jsafdalsjfs sfskdfjs";   
        for (char each : str.toCharArray()) {  
            if ((each >= 65 && each <= 90) || (each >= 97 && each <= 122)) {  
                String charStr = String.valueOf(each);  
                if (charTimesMap.containsKey(charStr)) {  
                    Long num = charTimesMap.get(charStr).longValue() + 1;  
                    charTimesMap.put(charStr, num);  
                } else {  
                    charTimesMap.put(charStr, 1L);  
                }  
            }  
        }  
          
        String maxAppearChar = null;  
        Long maxAppearTimes = 0L;  
        for (Map.Entry<String, Long> charAppear : charTimesMap.entrySet()) {  
            if (charAppear.getValue() > maxAppearTimes) {  
                maxAppearChar = charAppear.getKey();  
                maxAppearTimes = charAppear.getValue();  
            }  
        }  
        System.out.println("出现最多的字母："+maxAppearChar);  
        System.out.println("出现次数:"+maxAppearTimes);  
    }  
}
