package com.bjsxt.basegrammar_02;

public class TestBreak2 {

	/**
	 * ѭ��������
	 * ���101~200�ڵ�����
	 * ��������ֻ�ܱ�1������������������
	 * Ҳ����˵������ĳ��������ֻҪ��2����֮����һ������������������ô���Ͳ���������
	 * ��������������������㷨�Ļ���ԭ��
	 * TestBreak2.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-8-4 ����02:20:36
	 */
	public static void main(String[] args) {
		for(int i=101; i<200; i+=2) {//����Ϊʲô��i+=2?��Ϊż���϶���������
			boolean f = true;
			for(int j=2; j<i; j++) {
				if(i % j == 0){
					f = false;
					break;//�����ڲ��forѭ��
				}
			}
			if(!f) {
				continue;
			}
			System.out.println(" "+i);//�������������i��ֵ��ӡ����
		}
	}

}
