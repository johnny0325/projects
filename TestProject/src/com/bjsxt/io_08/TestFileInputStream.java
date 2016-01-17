package com.bjsxt.io_08;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestFileInputStream {

	/**
	 * ����ʹ��FileInputStream��ȡ�ļ�TestFileInputStream.java�����ݣ�����ʾ����
	 * ���ʣ�ִ���������,��ӡ��������Ϊʲô��"?"
	 * ԭ��һ������ռ�����ֽڵ����ݣ���FileInputStream�е�read()������ÿ�ζ�ȡһ���ֽڣ�Ȼ�����ʾ�����ˣ��϶�����������޷���ʾ������
	 * �����ʹ��FileReader��ÿ�ζ�ȡһ���ַ����������ľ��ܹ�������ȡ��
	 * TestFileInputStream.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-22 ����03:20:46
	 */
	public static void main(String[] args) {
		int b = 0;
		FileInputStream fis = null;
		try {
			//�ļ���·��Ҫʹ�þ���·��
			//java����,·���ķָ�������ʹ��������б��"\\" ��Ҳ����ʹ��һ����б��"/"����ʾ,����һ����б����������б�ܻ�����Ҳ����
//			fis = new FileInputStream("E:\\��ĿԴ����\\TestProject\\src\\com\\test\\io\\TestFileInputStream.java");
			fis = new FileInputStream("TestFileInputStream.java");
			
		} catch (FileNotFoundException e) {
			System.out.println("ϵͳ�Ҳ���ָ���ļ�");
			System.exit(-1);
		}
		
		int num = 0;
		try {
			while ((b = fis.read()) != -1) {
				System.out.print((char)b);
				num++;
			}
			fis.close();
			System.out.println();
			System.out.println("����ȡ�� "+num+" ���ֽ�");
		} catch (IOException e) {
			System.out.println("�ļ���ȡ����");
			System.exit(-1);
		}
		
	}

}
