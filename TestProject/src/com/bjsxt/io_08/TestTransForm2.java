package com.bjsxt.io_08;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestTransForm2 {

	/**
	 * ����ת����:InputStreamReader��OutputStreamWriter�����ֽ���ת�����ַ������������
	 * TestTransForm2.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-7-24 ����04:15:28
	 */
	public static void main(String[] args) {
		try {
			InputStreamReader isw = new InputStreamReader(System.in);
			//BufferedReader���췽���еĲ�����Reader,����һ�������࣬�����಻�ܱ�ʵ������
			//����ʹ��InputStreamReader��Ϊ������˵���Ǹ�������ָ���������
			//InputStreamReader��Reader��һ������
			BufferedReader br = new BufferedReader(isw);
			//readLine()��һ������ʽ������Ҳ��ͬ����������˼�����㲻���룬�ҾͲ��ܸɱ� ����,ֻ���������ˣ��Ҳſ��Լ���ִ������Ĵ���
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
