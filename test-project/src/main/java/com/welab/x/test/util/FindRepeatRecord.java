package com.welab.x.test.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindRepeatRecord
{

	public static void main(String[] args)
	{
		Map<String, String> client1Map = new HashMap<>();
		Set<String> set = new HashSet<>();
		List<String> accountList = FileUtil.readFileByLines("client3.txt");
        if (accountList != null && accountList.size() > 0) {
        	int count = 0;
        	int countLess3 = 0;
        	for (String str : accountList)
        	{
        		String arr[] = str.split("\\s+");
        		if (arr.length < 3)
        		{
        			countLess3++;
        		}
        		String cnid = arr[0];
//        		String name = arr[2];
//        		String account = arr[1];
        		
        		if (!client1Map.containsKey(cnid))
        		{
        			client1Map.put(cnid, cnid);
        		}
        		else {
        			set.add(cnid);
        			
        			count++;
        		}
//        		System.out.println(""+cnid);
        	}
        	
        	for(String cnid : set)
        	{
        		System.out.println(""+cnid);
        	}
        	
        	System.out.println("count==>"+count+", countLess3:"+countLess3);
        }

	}

}
