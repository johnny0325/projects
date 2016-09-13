package com.bjsxt.io_08;

import java.io.FileWriter;

public class TestFileWriter {

	/**
	 * TestFileWriter.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-22 下午05:06:29
	 */
	public static void main(String[] args) {
		FileWriter fw = null;
		try {
			fw = new FileWriter("E:\\项目源程序\\TestProject\\src\\com\\bjsxt\\io_08\\unicode.dat");
			for (int c = 0;c < 50000; c++) {
				fw.write(c);
			}
			fw.close();
		} catch (Exception e) {
			System.out.println("文件写入失败");
			System.exit(-1);
		}

	}

}
