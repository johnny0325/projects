package com.bjsxt.io_08;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestFileReader {

	/**
	 * ����ʹ��FileReader��FileWriter
	 * TestFileReader.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-22 ����03:45:21
	 */
	public static void main(String[] args) {
		int b = 0;
		FileReader fr = null;
		try {
			fr = new FileReader("E:/��ĿԴ����/TestProject/src/com/bjsxt/io_)8/TestFileInputStream.java");
		} catch (FileNotFoundException e) {
			System.out.println("�Ҳ���ָ���ļ�");
			System.exit(-1);
		}
		
		int num = 0;
		try {
			while ((b = fr.read()) != -1) {
				System.out.print((char)b);
				num++;
			}
			fr.close();
			System.out.println("����ȡ�� "+num+" ���ַ�");
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�����");
			System.exit(-1);
		}
		

	}

}
