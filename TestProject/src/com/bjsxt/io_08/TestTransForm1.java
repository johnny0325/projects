package com.bjsxt.io_08;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class TestTransForm1 {

	/**
	 * ����ת����:InputStreamReader��OutputStreamWriter�����ֽ���ת�����ַ������������
	 * TestTransForm1.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-24 ����03:41:35
	 */
	public static void main(String[] args) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("d:\\char.txt"));
			osw.write("microsoftibmsunapplehp");
			System.out.println(osw.getEncoding());
			osw.close();
			//true��ʾ׷�����ݵ��ļ�����
			//ָ���������ʹ��ISO8859_1,������һ�����ֽ�latin-1
			osw = new OutputStreamWriter(new FileOutputStream("d:\\char.txt",true),"ISO8859_1");
			osw.write("microsoftibmsunapplehp");
			System.out.println(osw.getEncoding());
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
