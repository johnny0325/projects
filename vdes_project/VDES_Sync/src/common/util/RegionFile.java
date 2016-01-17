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
 * ����regionFile�Ĳ���fileCountsΪҪ�ϲ����ļ�������fileNameΪҪ�ϲ����ļ���
 * �ϲ����ļ����ĸ�ʽ�磺0_text.txt��fileNameΪtext.txt����
 */
public class RegionFile {
	private static Logger log = Logger.getLogger(TaskCheckReport.class);
	
	public void regionFile(int fileCounts, String fileName) {
		log.info("<----��ʼ�ϲ��ļ�");
		String downloadfile = new ConfigureUtil().getValue("downloadfile");

		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(downloadfile,
					fileName)));// �ϲ���fileName
			for (int i = 0; i <= fileCounts; i++) {
				br = new BufferedReader(new FileReader(new File(downloadfile, i
						+ "_" + fileName)));// ��ȡ��0_fileName���ļ�
				String str = "";
				String tempData = br.readLine();
				int j = 1;
				while (tempData != null) {
					str += tempData + "\r\n";
					if (j % 2000 == 0) {// ÿ2000�в���һ��
						bw.write(str);
						str = "";
					}
					j++;
					tempData = br.readLine();
				}
				bw.write(str);// ʣ������ݲ���2000��������в���
				str = "";
				br.close();
			}
			bw.close();
			for (int i = 0; i <= fileCounts; i++) {
				File file = new File(downloadfile, i + "_" + fileName);// ɾ���ļ�
				if (file.exists()) {
					if (file.delete()) {
						log.info("ɾ��downloadfileĿ¼�µ�" + i + "_" + fileName + "�ļ��ɹ���");// ɾ���ļ�
					}
				}
			}
		} catch (Exception e) {
			log.info(e);

		}
		log.info("�ϲ��ļ�����---->");
	}
}
