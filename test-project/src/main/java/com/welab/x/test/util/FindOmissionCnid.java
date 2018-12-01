package com.welab.x.test.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.welab.x.test.commom.Constant;

/**
 * 
 * @ClassName: FindOmissionCnid
 * @Description: 根据两个文件中的cnid，查出有哪些cnid需要重跑
 * @author johnny.lin
 * @date 2018年6月11日 上午11:44:58
 * @version V1.0
 */
public class FindOmissionCnid
{
	public static void main(String[] args)
	{
		String fileName = System.getProperty("fileName", "olduser_applications.properties");
		List<String> accountList = FileUtil.readFileByLines(fileName);
		
		String fileName2 = System.getProperty("fileName", "olduser_applications2.properties");
		List<String> accountList2 = FileUtil.readFileByLines(fileName2);
		
		int lineCount = 0;
		StringBuilder sb = new StringBuilder();
		Map<String, String> client1Map = new HashMap<>();
		for(String cnid : accountList)
		{
			if(client1Map.containsKey(cnid)) {
				System.out.println(cnid);
			}
			client1Map.put(cnid, cnid);
			if (!accountList2.contains(cnid))
			{
				//System.out.println(cnid);
				lineCount++;
				sb.append(cnid);
				sb.append(Constant.LINESEPARATOR);
				System.out.println(lineCount);
			}
		}
		
		System.out.println(client1Map.size());
		String path = Constant.FILEPATH + Constant.FILESEPARATOR +"output";
        File storePath = new File(path); 
        if (!storePath.exists())
        {
        	storePath.mkdirs();
        }
        
        String newFileName = path+ Constant.FILESEPARATOR + "cnid.properties";
        System.out.println("newFileName ==>" + newFileName+", lineCount:"+lineCount);
        if (sb != null) {
            FileUtil.fileOutput(newFileName, sb.toString());
            sb = null;
        }
	}

}
