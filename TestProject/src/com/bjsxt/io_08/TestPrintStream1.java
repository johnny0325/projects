package com.bjsxt.io_08;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class TestPrintStream1 {

	/**
	 * 打印流，PrintWriter和PrintStream，都属于输出流，分别针对于字符和字节
	 * PrintWriter和PrintStream的输出操作不会抛出异常
	 * PrintWriter和PrintStream，有自动flush功能
	 * TestPrintStream1.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-28 下午08:49:17
	 */
	public static void main(String[] args) {
		PrintStream ps = null;
		
		try {
			FileOutputStream fos = new FileOutputStream("E:\\项目源程序\\TestProject\\src\\com\\bjsxt\\io_08\\log.dat");
			ps = new PrintStream(fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (ps != null) {
			System.setOut(ps);//设置输出到文件
		}
		
		int ln = 0;
		for(char c = 0; c <= 60000; c++){//打印字符
			System.out.println(c+" ");
			if(ln++ >= 100){
				System.out.println();
				ln = 0;
			}
		}
	}

}
