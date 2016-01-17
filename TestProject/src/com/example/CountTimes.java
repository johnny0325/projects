package com.example;

import java.util.HashMap;
import java.util.Map;

/**
 * ��Ŀ������һ���ַ����������˿ո�ȱ����ţ�Ҫ���������ִ���������ĸ�͸���ĸ���ֵĴ����� 
 * ��ʾ��ASCII��A~Z��Ӧ����65-90��a~z��Ӧ����97-122
 * CountTimes
 * Author��jlLin
 * Aug 29, 2011  11:57:22 AM
 * Copyright ���ھŷ��Ƽ����޹�˾
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
        System.out.println("����������ĸ��"+maxAppearChar);  
        System.out.println("���ִ���:"+maxAppearTimes);  
    }  
}
