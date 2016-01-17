package com.bjsxt.io_08;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestFileInputStream {

	/**
	 * 测试使用FileInputStream读取文件TestFileInputStream.java的内容，并显示出来
	 * 疑问：执行这个程序,打印出来数据为什么有"?"
	 * 原因：一个中文占两个字节的数据，而FileInputStream中的read()方法是每次读取一个字节，然后就显示出来了，肯定会出现中文无法显示的问题
	 * 解决：使用FileReader，每次读取一个字符，这样中文就能够正常读取了
	 * TestFileInputStream.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-22 下午03:20:46
	 */
	public static void main(String[] args) {
		int b = 0;
		FileInputStream fis = null;
		try {
			//文件的路径要使用绝对路径
			//java里面,路径的分隔符可以使用两个反斜杠"\\" ，也可以使用一个正斜杠"/"来表示,或者一个正斜杠与两个反斜杠混着用也可以
//			fis = new FileInputStream("E:\\项目源程序\\TestProject\\src\\com\\test\\io\\TestFileInputStream.java");
			fis = new FileInputStream("TestFileInputStream.java");
			
		} catch (FileNotFoundException e) {
			System.out.println("系统找不到指定文件");
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
			System.out.println("共读取了 "+num+" 个字节");
		} catch (IOException e) {
			System.out.println("文件读取错误");
			System.exit(-1);
		}
		
	}

}
