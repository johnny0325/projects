package com.welab.x.test.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.welab.x.test.commom.Constant;

public class Filter
{
	
	public static void main(String[] args)
	{
		Map<String, String> client1Map = new HashMap<>();
		List<String> accountList = FileUtil.readFileByLines("client1.txt");
        if (accountList != null && accountList.size() > 0) {
        	for (String str : accountList)
        	{
        		String arr[] = str.split("\\s+");
        		String cnid = arr[0];
        		String name = arr[1];
        		client1Map.put(cnid, cnid+" "+name);
        	}
        }
        
        Map<String, String> client11Map = new HashMap<>();
		List<String> accountList2 = FileUtil.readFileByLines("client11.txt");
        if (accountList2 != null && accountList2.size() > 0) {
        	for (String str : accountList2)
        	{
        		String arr[] = str.split("\\s+");
        		String cnid = arr[0];
        		String account = arr[1];
        		client11Map.put(cnid, cnid+" "+account);
        	}
        }
        
        int count = 0;
        StringBuffer sb = new StringBuffer();
        if (client1Map != null && client11Map != null)
        {
        	for (String cnid : client1Map.keySet())
        	{
        		if (client11Map.containsKey(cnid))
        		{
        			sb.append(client1Map.get(cnid)+" "+client11Map.get(cnid).split("\\s+")[1]);
        			sb.append(Constant.LINESEPARATOR);
        			count++;
        		}
        	}
        }
        
        accountList2 = FileUtil.readFileByLines("client2.txt");
        if (accountList2 != null && accountList2.size() > 0) {
        	for (String str : accountList2)
        	{
        		String arr[] = str.split("\\s+");
        		String name = arr[0];
        		String cnid = arr[1];
        		cnid = cnid.replace("'", "");
        		String account = arr[2];
        		sb.append(cnid+" "+name+" "+account);
        		sb.append(Constant.LINESEPARATOR);
    			count++;
        	}
        }
        
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
