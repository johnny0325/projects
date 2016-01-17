package com.bjsxt.io_08;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestFileOutputStream {

	/**
	 * TestFileOutputStream.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-22 ����04:50:40
	 */
	public static void main(String[] args) {
		int b = 0;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			fis = new FileInputStream("E:\\��ĿԴ����\\TestProject\\src\\com\\bjsxt\\io_08\\TestFileInputStream.java");
			fos = new FileOutputStream("E:\\��ĿԴ����\\TestProject\\src\\com\\bjsxt\\io_08\\CopyTestFileInputStream.java");
			while ((b = fis.read()) != -1) {
				fos.write(b);
			}
			fis.close();
			fos.close();
		} catch (FileNotFoundException e) {
			System.out.println("�Ҳ���ָ���ļ�");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("�ļ����Ƴ���");
			System.exit(-1);
		}
		System.out.println("�ļ��Ѹ���");
	}

}
