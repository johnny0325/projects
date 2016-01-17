package com.bjsxt.io_08;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TestDataStream {

	/**
	 * ����ʹ��������DataInputStream��DataOutputStream��
	 * �Լ�ByteArrayInputStream��ByteArrayOutputStream(�������ڴ�����ֽ�������ж�д)
	 * TestDataStream.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-28 ����05:56:49
	 */
	public static void main(String[] args) {
		//���������ڴ������һ���ֽ����飬Ȼ�󴴽���������Ĺܵ�ByteArrayOutputStream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeDouble(Math.random());
			dos.writeBoolean(true);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			//���ؿɴӴ���������ȡ������������ʣ���ֽ���
			System.out.println(bais.available());
			DataInputStream dis = new DataInputStream(bais);
			//Ҫע��˳����д���ȶ���������������Double,��Boolean����������������ݾ������׵ģ���˵�������ݽṹ������һ������
			System.out.println(dis.readDouble());
			//��ȡһ���ֽڣ���ת���ɲ������ͣ�˭����ת�ģ�DataInputStream����Ҫ�Լ�ת�������⣬�鷳���� 
			System.out.println(dis.readBoolean());
			dis.close();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
