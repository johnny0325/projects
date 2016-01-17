package com.bjsxt.io_08;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TestDataStream {

	/**
	 * 测试使用数据流DataInputStream、DataOutputStream，
	 * 以及ByteArrayInputStream、ByteArrayOutputStream(用于在内存里的字节数组进行读写)
	 * TestDataStream.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-28 下午05:56:49
	 */
	public static void main(String[] args) {
		//首先是在内存里分配一个字节数组，然后创建向外输出的管道ByteArrayOutputStream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeDouble(Math.random());
			dos.writeBoolean(true);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			//返回可从此输入流读取（或跳过）的剩余字节数
			System.out.println(bais.available());
			DataInputStream dis = new DataInputStream(bais);
			//要注意顺序：先写的先读出来，所以是先Double,后Boolean，否则读出来的数据就是乱套的，这说明从数据结构看它是一个队列
			System.out.println(dis.readDouble());
			//读取一个字节，并转换成布尔类型，谁帮你转的？DataInputStream，你要自己转换这玩意，麻烦死了 
			System.out.println(dis.readBoolean());
			dis.close();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
