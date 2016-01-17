package common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.log4j.Logger;

import pro.vdes.task.TaskCheckReport;
/**
 * 
 * @author xiaodeng
 * date 2010-04-27
 * 方法regionFile的参数fileCounts为要合并的文件个数，fileName为要合并的文件名
 * 合并的文件名的格式如：0_text.txt，fileName为text.txt部分
 */
public class RegionFile {
	private static Logger log = Logger.getLogger(TaskCheckReport.class);
	
	public void regionFile(int fileCounts, String fileName) {
		log.info("<----开始合并文件");
		String downloadfile = new ConfigureUtil().getValue("downloadfile");

		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(downloadfile,
					fileName)));// 合并到fileName
			for (int i = 0; i <= fileCounts; i++) {
				br = new BufferedReader(new FileReader(new File(downloadfile, i
						+ "_" + fileName)));// 读取如0_fileName的文件
				String str = "";
				String tempData = br.readLine();
				int j = 1;
				while (tempData != null) {
					str += tempData + "\r\n";
					if (j % 2000 == 0) {// 每2000行插入一次
						bw.write(str);
						str = "";
					}
					j++;
					tempData = br.readLine();
				}
				bw.write(str);// 剩余的数据不够2000在这里进行插入
				str = "";
				br.close();
			}
			bw.close();
			for (int i = 0; i <= fileCounts; i++) {
				File file = new File(downloadfile, i + "_" + fileName);// 删除文件
				if (file.exists()) {
					if (file.delete()) {
						log.info("删除downloadfile目录下的" + i + "_" + fileName + "文件成功！");// 删除文件
					}
				}
			}
		} catch (Exception e) {
			log.info(e);

		}
		log.info("合并文件结束---->");
	}
}
