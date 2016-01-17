package com.bjsxt.io_08;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class TestPrintStream2 {

	/**
	 * 测试PrintStream类的使用
	 * TestPrintStream2.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-28 下午09:44:41
	 */
	public static void main(String[] args) {
		
		String fileName = null;
		try {
			BufferedReader brReader = new BufferedReader(new InputStreamReader(System.in));
			while((fileName = brReader.readLine()) != null){
				list(fileName, System.out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void list(String fileName,PrintStream ps){
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String s = null;
			while((s = br.readLine()) != null){
				ps.println(s);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("无法读取文件");
		}
	}

}
