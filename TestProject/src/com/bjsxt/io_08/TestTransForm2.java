package com.bjsxt.io_08;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestTransForm2 {

	/**
	 * 测试转换流:InputStreamReader、OutputStreamWriter，把字节流转换成字符流，方便操作
	 * TestTransForm2.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-24 下午04:15:28
	 */
	public static void main(String[] args) {
		try {
			InputStreamReader isw = new InputStreamReader(System.in);
			//BufferedReader构造方法中的参数是Reader,它是一个抽象类，抽象类不能被实例化，
			//这里使用InputStreamReader作为参数，说明是父类引用指向子类对象
			//InputStreamReader是Reader的一个子类
			BufferedReader br = new BufferedReader(isw);
			//readLine()是一个阻塞式方法，也叫同步方法，意思就是你不输入，我就不能干别 的事,只有你输入了，我才可以继续执行下面的代码
			String s= br.readLine();
			while(s != null) {
				if (s.equalsIgnoreCase("exit")) {
					break;
				}
				System.out.println(s.toUpperCase());
				s = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
