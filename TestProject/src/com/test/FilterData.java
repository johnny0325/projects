package com.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FilterData {

	/**
	 * 功能描述：
	 * @param args
	 * @return void
	 * Author：ljLin
	 * Apr 19, 2011 9:37:48 AM
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String factName = "expdata_已过滤.txt";
		String uploadfile = "C:\\Documents and Settings\\Administrator\\桌面\\新建文件夹";
		String downloadfile = "C:\\Documents and Settings\\Administrator\\桌面\\新建文件夹\\data";
		File srcFile = new File(uploadfile, factName);
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd"); 
		
		File p = new File(downloadfile);
		if(!p.exists()){
			p.mkdirs();
		}
		
		File desFile = new File(downloadfile, factName);
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try{
			br = new BufferedReader(new FileReader(srcFile));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(desFile, false)));
			String line = "";
			int i = 0;
			String doubleDate = "";
			Date lastMMSDate = null;
			Date expMMSDate = null;
			String lastMMSTime = "";
			String expMMSTime = "";
			while ((line = br.readLine()) != null) {
				i++;
				doubleDate = line.split(",")[1];
				if(doubleDate.length()>12){
					expMMSTime = doubleDate.split("#")[0];
					lastMMSTime = doubleDate.split("#")[1];
					
					
					expMMSDate = sf.parse(expMMSTime);
					lastMMSDate = sf.parse(lastMMSTime.split("-")[0]+lastMMSTime.split("-")[1]+lastMMSTime.split("-")[2]);
					System.out.println("lastMMSDate.compareTo(expMMSDate)==>"+lastMMSDate.compareTo(expMMSDate));
					if(lastMMSDate.compareTo(expMMSDate)==1){
						System.out.println("expMMSTime==>"+expMMSTime);
						System.out.println("lastMMSTime==>"+lastMMSTime);
						System.out.println("line"+i+"==>"+line);
						
						bw.write(line);
						bw.write("\r\n");
					}
				}
				
				
			}
			br.close();
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
