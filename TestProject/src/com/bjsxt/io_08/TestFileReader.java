package com.bjsxt.io_08;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestFileReader {

	/**
	 * 测试使用FileReader、FileWriter
	 * TestFileReader.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-22 下午03:45:21
	 */
	public static void main(String[] args) {
		int b = 0;
		FileReader fr = null;
		try {
			fr = new FileReader("E:/项目源程序/TestProject/src/com/bjsxt/io_)8/TestFileInputStream.java");
		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
			System.exit(-1);
		}
		
		int num = 0;
		try {
			while ((b = fr.read()) != -1) {
				System.out.print((char)b);
				num++;
			}
			fr.close();
			System.out.println("共读取了 "+num+" 个字符");
		} catch (IOException e) {
			System.out.println("读取文件错误");
			System.exit(-1);
		}
		

	}

}
