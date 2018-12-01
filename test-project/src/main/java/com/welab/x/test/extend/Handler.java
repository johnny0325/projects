package com.welab.x.test.extend;

import java.util.List;

import com.welab.x.test.commom.Constant;
import com.welab.x.test.util.FileUtil;
import com.welab.x.test.util.StringUtil;

public class Handler
{
	public static void main(String[] args)
	{
		String fileNames[] = {"not1.txt","not2.txt","not3.txt","not4.txt","not5.txt"};
		StringBuilder sb = new StringBuilder();
		for (String name : fileNames)
		{
			int accountNums = 0;
			List<String> logList = FileUtil.readFileByLines(name);
			if (logList != null && logList.size() > 0)
			{
				for (String log : logList)
				{
					int index = log.indexOf("account:");
					String account = log.substring(index+8, index+19);
					if (!StringUtil.isEmpty(account))
					{
						accountNums++;
						sb.append(account);
						 sb.append(Constant.LINESEPARATOR);
					}
				}
			}
			
			System.out.println("name:"+name+", accountNums:"+accountNums);
		}
		
		String newFileName = Constant.FILEPATH + Constant.FILESEPARATOR + "total.txt" ;
		if (sb != null) {
            FileUtil.fileOutput(newFileName, sb.toString());
        }
	}

}
