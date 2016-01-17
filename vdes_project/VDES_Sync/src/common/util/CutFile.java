package common.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import org.apache.log4j.Logger;

import pro.vdes.task.TaskCheckReport;

public class CutFile {
	/**
	 * @author xiaodeng
	 * date 2010-04-26
	 * fileCounts为分割后的文件个数
	 * 参数: rows 为要分多少行在一个文件上 int 类型 
	 * sourceFilePath为源文件路径 String 类型 targetDirectoryPath 为文件分割后存放的目标目录 String 类型
	 * ---分割后的文件名为索引号(从0开始)加'_'加源文件名,例如源文件名为test.txt,则分割后文件名为0_test.txt,以此类推
	 *
	 */
	
	private static Logger log = Logger.getLogger(TaskCheckReport.class);
	public int fileCount=0;//分割后的文件个数
	public void cut(int rows, String sourceFilePath,
			String targetDirectoryPath) {
		log.info("<----分割文件开始");
		File sourceFile = new File(sourceFilePath);
		File targetFile = new File(targetDirectoryPath);
		if (!sourceFile.exists() || rows <= 0 || sourceFile.isDirectory()) {
			System.out.println("源文件不存在或者输入了错误的行数");
			return;
		}
		if (targetFile.exists()) {
			if (!targetFile.isDirectory()) {
				System.out.println("目标文件夹错误,不是一个文件夹");
				return;
			}
		} else {
			targetFile.mkdirs();
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(sourceFile));
			BufferedWriter bw = null;
			String str = "";
			String tempData = br.readLine();
			int i = 1, s = 0;
			bw = new BufferedWriter(new FileWriter(new File(targetFile
					.getAbsolutePath()
					+ "/" + s + "_" + sourceFile.getName())));
			while (tempData != null) {
				str += tempData + "\r\n";
				if(i%2000==0){//每2000行插入一次
					bw.write(str);
					str = "";
				}
				//每rows行为一个文件
				if (i % rows == 0) {
					bw.close();
					s += 1;
					bw = new BufferedWriter(new FileWriter(new File(targetFile
							.getAbsolutePath()
							+ "/" + s + "_" + sourceFile.getName())));	
				}
				i++;
				tempData = br.readLine();
			}
			bw.write(str);//剩余的数据不够2000在这里进行插入
			bw.close();
			br.close();
			fileCount=s;
		} catch (Exception e) {
		}
		log.info("分割文件结束---->");
	}

	// 测试
	public static void main(String args[]) {
		String uploadfile = new ConfigureUtil().getValue("uploadfile")+"/zhuowang-gzsd-4441.txt";
		String downloadfile = new ConfigureUtil().getValue("downloadfile")+"/zhuowang-gzsd-4441.txt";
		CutFile cf = new CutFile();
		cf.cut(8, uploadfile, downloadfile);
		
	/*	for (int i = 0; i <= 5; i++) {
			File file = new File(downloadfile, i + "_1272333502531.txt");// 删除文件
			if (file.exists()) {
				if (file.delete()) {
					log.info("删除" + i + "_1272333502531.txt文件成功！");// 删除文件
				}
			}
		}	*/
	}

}

