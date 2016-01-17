package com.bjsxt.io_08;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;

public class TestPrintStream3 {

	/**
	 * 测试使用PrintWriter
	 * TestPrintStream3.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-28 下午10:56:38
	 */
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		try {
			FileWriter fw = new FileWriter("E:\\项目源程序\\TestProject\\src\\com\\bjsxt\\io_08\\logfile.log",true);
			PrintWriter log = new PrintWriter(fw);
			while((s = br.readLine()) != null){
				if(s.equalsIgnoreCase("exit")){
					break;
				}
				System.out.println(s.toUpperCase());
				log.println("----------");
				log.println(s.toUpperCase());
			}
			log.println("==="+new Date()+"===");
			log.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
