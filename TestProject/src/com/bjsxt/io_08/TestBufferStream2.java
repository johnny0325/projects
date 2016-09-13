package com.bjsxt.io_08;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TestBufferStream2 {

	/**
	 * TestBufferStream2.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-22 下午06:09:55
	 */
	public static void main(String[] args) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("E:\\项目源程序\\TestProject\\src\\com\\bjsxt\\io_08\\dat.txt"));
			BufferedReader br = new BufferedReader(new FileReader("E:\\项目源程序\\TestProject\\src\\com\\bjsxt\\io_08\\dat.txt"));
			String s = null;
			for (int i=0; i<100; i++) {
				s = String.valueOf(Math.random());
				bw.write(s);
				bw.newLine();
			}
			bw.flush();
			while ((s = br.readLine()) != null) {
				System.out.println(s);
			}
			bw.close();
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("找不到文件");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
