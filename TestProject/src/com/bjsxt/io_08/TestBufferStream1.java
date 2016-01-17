package com.bjsxt.io_08;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class TestBufferStream1 {

	/**
	 * ���Ի�����BufferedInputStream��BufferedOutputStream
	 * TestBufferStream1.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-22 ����05:32:06
	 */
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("E:\\��ĿԴ����\\TestProject\\src\\com\\bjsxt\\io_08\\TestFileInputStream.java");
			BufferedInputStream bis = new BufferedInputStream(fis);
			System.out.println(bis.read());
			System.out.println(bis.read());
			bis.mark(100);	//��100�е�λ����һ����־
			int c = 0;
			for (int i=0;i<10 && (c=bis.read()) != -1;i++) {
				System.out.print((char)c+" ");
			}
			System.out.println();
			bis.reset();	//���»ص���־�ĵط�
			for (int i=0;i<10 && (c=bis.read()) != -1;i++) {
				System.out.print((char)c+" ");
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
