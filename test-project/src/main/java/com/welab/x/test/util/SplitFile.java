package com.welab.x.test.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.welab.x.test.commom.Constant;

public class SplitFile
{
	private List<String> accountList;
	private static Map<String, String> fileNameMap;

	static
	{
		fileNameMap = new HashMap<String, String>();
		fileNameMap.put("1", "oldcustomerlist43.txt");
		fileNameMap.put("2", "oldcustomerlist44.txt");
		fileNameMap.put("3", "oldcustomerlist45.txt");
		fileNameMap.put("4", "oldcustomerlist52.txt");
		fileNameMap.put("5", "oldcustomerlist53.txt");
	}

	public void excute(int fileNums)
	{
		if (fileNums < 1 || fileNums >5)
		{
			return ;
		}
		
		final String fileName = System.getProperty("fileName", "olduser_applications.txt");
        accountList = FileUtil.readFileByLines(fileName);
        if (this.accountList != null && this.accountList.size() > 0) {
            final int totalCount = this.accountList.size();
            System.out.println("SplitFile totalCount ==>" + totalCount);
            final int fileMul = totalCount / fileNums;
            final int lastFileCount = totalCount - fileMul * (fileNums - 1);
            String newFileName = "";
            StringBuilder sb = null;
            int lineSize = 0;
            for (int i = 0; i < fileNums; ++i) {
                if (i == fileNums - 1) {
                    lineSize = fileMul * i + lastFileCount;
                }
                else {
                    lineSize = fileMul * (i + 1);
                }
                
                int lineCount = 0;
                for (int j = i * fileMul; j < lineSize; ++j) {
                    final String account = this.accountList.get(j);
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    sb.append(account);
                    lineCount++;
                    if (j != lineSize - 1) {
                        sb.append(Constant.LINESEPARATOR);
                    }
                }
                
                String path = Constant.FILEPATH + Constant.FILESEPARATOR +"output";
                File storePath = new File(path); 
                if (!storePath.exists())
                {
                	storePath.mkdirs();
                }
                
                newFileName = path+ Constant.FILESEPARATOR + fileNameMap.get(String.valueOf(i+1));
                System.out.println("newFileName ==>" + newFileName+", lineCount:"+lineCount);
                if (sb != null) {
                    FileUtil.fileOutput(newFileName, sb.toString());
                    sb = null;
                }
            }
        }
	}
	
	public static void main(String[] args)
	{
		new SplitFile().excute(2);
	}
}
