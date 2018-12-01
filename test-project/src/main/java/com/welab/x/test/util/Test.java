package com.welab.x.test.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.welab.x.test.commom.Constant;

public class Test
{
	private static List<String> accountList;
	private static Map<String, String> fileNameMap = new HashMap<>();
	static StringBuilder sb = new StringBuilder();
	
	public static void main(String[] args)
	{
		final String fileName = System.getProperty("fileName", "olduser_applications.properties");
        accountList = FileUtil.readFileByLines(fileName);
        int lineCount = 0;
        if (accountList != null && accountList.size() > 0) {
        	for (String cnid : accountList) {
        		if (!fileNameMap.containsKey(cnid))
        		{
        			fileNameMap.put(cnid, cnid);
        		}
        		else {
        			System.out.println(cnid);
        			lineCount ++;
        			sb.append(cnid);
        			sb.append(Constant.LINESEPARATOR);
        		}
        	}
        }
        
        String path = Constant.FILEPATH + Constant.FILESEPARATOR +"output";
        File storePath = new File(path); 
        if (!storePath.exists())
        {
        	storePath.mkdirs();
        }
        
        String newFileName = path+ Constant.FILESEPARATOR + "repeat.txt";
        System.out.println("newFileName ==>" + newFileName+", lineCount:"+lineCount);
        if (sb != null) {
            FileUtil.fileOutput(newFileName, sb.toString());
            sb = null;
        }
        
	}

}
