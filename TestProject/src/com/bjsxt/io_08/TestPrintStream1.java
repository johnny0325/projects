package com.bjsxt.io_08;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class TestPrintStream1 {

	/**
	 * ��ӡ����PrintWriter��PrintStream����������������ֱ�������ַ����ֽ�
	 * PrintWriter��PrintStream��������������׳��쳣
	 * PrintWriter��PrintStream�����Զ�flush����
	 * TestPrintStream1.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-28 ����08:49:17
	 */
	public static void main(String[] args) {
		PrintStream ps = null;
		
		try {
			FileOutputStream fos = new FileOutputStream("E:\\��ĿԴ����\\TestProject\\src\\com\\bjsxt\\io_08\\log.dat");
			ps = new PrintStream(fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (ps != null) {
			System.setOut(ps);//����������ļ�
		}
		
		int ln = 0;
		for(char c = 0; c <= 60000; c++){//��ӡ�ַ�
			System.out.println(c+" ");
			if(ln++ >= 100){
				System.out.println();
				ln = 0;
			}
		}
	}

}
