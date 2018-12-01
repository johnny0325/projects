package com.welab.x.test.util;

import java.io.File;
import java.util.List;

import com.welab.x.test.commom.Constant;

public class FindCnid
{

	public static void main(String[] args)
	{
		String fileName2 = System.getProperty("fileName", "olduser_applications2.properties");
		List<String> accountList = FileUtil.readFileByLines(fileName2);
		
		int lineCount = 0;
		StringBuilder sb = new StringBuilder();
		for(String str : accountList)
		{
			String arr[] = str.split(",");
			if (arr.length == 3) {
				String cnid = arr[0];
	    		String account = arr[1];
	    		String name = arr[2];
	    		lineCount++;
				sb.append(cnid);
				sb.append(Constant.LINESEPARATOR);
				System.out.println(lineCount);
			}
		}
		
		String path = Constant.FILEPATH + Constant.FILESEPARATOR +"output";
        File storePath = new File(path); 
        if (!storePath.exists())
        {
        	storePath.mkdirs();
        }
        
        String newFileName = path+ Constant.FILESEPARATOR + "cnid.txt";
        System.out.println("newFileName ==>" + newFileName+", lineCount:"+lineCount);
        if (sb != null) {
            FileUtil.fileOutput(newFileName, sb.toString());
            sb = null;
        }

	}

}
