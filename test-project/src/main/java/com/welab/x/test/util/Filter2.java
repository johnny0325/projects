package com.welab.x.test.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.welab.x.test.commom.Constant;

public class Filter2
{
	
	public static void main(String[] args)
	{
		Map<String, String> client1Map = new HashMap<>();
		List<String> accountList = FileUtil.readFileByLines("client3.txt");
        if (accountList != null && accountList.size() > 0) {
        	for (String str : accountList)
        	{
        		String arr[] = str.split("\\s+");
        		String cnid = arr[0];
        		String name = arr[1];
        		String account = arr[2];
        		client1Map.put(account, cnid);
        	}
        }
        
        int count = 0;
        StringBuffer sb = new StringBuffer();
        Map<String, String> client11Map = new HashMap<>();
		List<String> accountList2 = FileUtil.readFileByLines("client4.txt");
        if (accountList2 != null && accountList2.size() > 0) {
        	for (String account : accountList2)
        	{
        		
        		if (client1Map.containsKey(account))
        		{
        			sb.append("'"+client1Map.get(account));
        			sb.append(Constant.LINESEPARATOR);
        			count++;
        		}
        	}
        }
        
        
//        if (client1Map != null && client11Map != null)
//        {
//        	for (String account : client11Map.keySet())
//        	{
//        		if (client1Map.containsKey(account))
//        		{
//        			sb.append(account+" "+client1Map.get(account));
//        			sb.append(Constant.LINESEPARATOR);
//        			count++;
//        		}
//        	}
//        }
        
       
        
        System.out.println(sb.toString());
        System.out.println("count ==>"+count);
        
        String path = Constant.FILEPATH + Constant.FILESEPARATOR +"output";
        File storePath = new File(path); 
        if (!storePath.exists())
        {
        	storePath.mkdirs();
        }
        
        String newFileName = path+ Constant.FILESEPARATOR + "filter1.txt";
        System.out.println("newFileName ==>" + newFileName+", lineCount:"+count);
        if (sb != null) {
            FileUtil.fileOutput(newFileName, sb.toString());
            sb = null;
        }
	}
	
	
}
