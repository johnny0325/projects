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
	 * fileCountsΪ�ָ����ļ�����
	 * ����: rows ΪҪ�ֶ�������һ���ļ��� int ���� 
	 * sourceFilePathΪԴ�ļ�·�� String ���� targetDirectoryPath Ϊ�ļ��ָ���ŵ�Ŀ��Ŀ¼ String ����
	 * ---�ָ����ļ���Ϊ������(��0��ʼ)��'_'��Դ�ļ���,����Դ�ļ���Ϊtest.txt,��ָ���ļ���Ϊ0_test.txt,�Դ�����
	 *
	 */
	
	private static Logger log = Logger.getLogger(TaskCheckReport.class);
	public int fileCount=0;//�ָ����ļ�����
	public void cut(int rows, String sourceFilePath,
			String targetDirectoryPath) {
		log.info("<----�ָ��ļ���ʼ");
		File sourceFile = new File(sourceFilePath);
		File targetFile = new File(targetDirectoryPath);
		if (!sourceFile.exists() || rows <= 0 || sourceFile.isDirectory()) {
			System.out.println("Դ�ļ������ڻ��������˴��������");
			return;
		}
		if (targetFile.exists()) {
			if (!targetFile.isDirectory()) {
				System.out.println("Ŀ���ļ��д���,����һ���ļ���");
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
				if(i%2000==0){//ÿ2000�в���һ��
					bw.write(str);
					str = "";
				}
				//ÿrows��Ϊһ���ļ�
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
			bw.write(str);//ʣ������ݲ���2000��������в���
			bw.close();
			br.close();
			fileCount=s;
		} catch (Exception e) {
		}
		log.info("�ָ��ļ�����---->");
	}

	// ����
	public static void main(String args[]) {
		String uploadfile = new ConfigureUtil().getValue("uploadfile")+"/zhuowang-gzsd-4441.txt";
		String downloadfile = new ConfigureUtil().getValue("downloadfile")+"/zhuowang-gzsd-4441.txt";
		CutFile cf = new CutFile();
		cf.cut(8, uploadfile, downloadfile);
		
	/*	for (int i = 0; i <= 5; i++) {
			File file = new File(downloadfile, i + "_1272333502531.txt");// ɾ���ļ�
			if (file.exists()) {
				if (file.delete()) {
					log.info("ɾ��" + i + "_1272333502531.txt�ļ��ɹ���");// ɾ���ļ�
				}
			}
		}	*/
	}

}

