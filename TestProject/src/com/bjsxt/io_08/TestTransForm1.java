package com.bjsxt.io_08;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class TestTransForm1 {

	/**
	 * 测试转换流:InputStreamReader、OutputStreamWriter，把字节流转换成字符流，方便操作
	 * TestTransForm1.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-24 下午03:41:35
	 */
	public static void main(String[] args) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("d:\\char.txt"));
			osw.write("microsoftibmsunapplehp");
			System.out.println(osw.getEncoding());
			osw.close();
			//true表示追加内容到文件里面
			//指定输出编码使用ISO8859_1,它还有一个名字叫latin-1
			osw = new OutputStreamWriter(new FileOutputStream("d:\\char.txt",true),"ISO8859_1");
			osw.write("microsoftibmsunapplehp");
			System.out.println(osw.getEncoding());
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
