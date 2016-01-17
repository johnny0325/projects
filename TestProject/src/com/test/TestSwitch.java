package com.test;

/**
 * ����switch(expr1)�е�expr1��������
 * switch��expr1���У�expr1��һ���������ʽ��
 * ��˴��ݸ� switch �� case ���Ĳ���Ӧ���� byte�� short��int��char����enum ��long,string ������������swtich
 * default:���ܲ���ֵ��û��ƥ����ϣ�default��䶼��ִ��;���ƥ����ϣ����Һ������break��䣬��ô�ͻ�������switch��䣬
 * ����ִ��default����ˡ�
 * TestSwitch
 * Author��jlLin
 * Aug 23, 2011  7:37:33 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */

/**
 * switch-case���ܽ�
 * 1��switch-case�����ȫ������if-else��以ת����ͨ����˵��switch-case���ִ��Ч��Ҫ�ߡ� 
 * 2��default�������û�з��ϵ�case��ִ����,default�����Ǳ���ģ�������ʡ�ԣ������Ƽ�ʡ�ԡ�
 * 3��case��������Բ��ô�����. 
 * 4��switch�����ж��������Խ���byte�� short��int��char����enum�����ܽ�����������. 
 * 5��һ��caseƥ��,�ͻ�˳��ִ�к���ĳ������,�����ܺ����case�Ƿ�ƥ��,ֱ������break,������һ���Կ����úü���caseִ��ͳһ���.
 */
public class TestSwitch {
	//����һ��ö����������
	enum Signal {GREEN, YELLOW, RED } 
	
	public static void main(String args[]){
		
		TestSwitch ts = new TestSwitch();
		
		char i = 3;
		switch(i){
			case 1:System.out.println("value is 1.");break;
			case 2:System.out.println("value is 2.");
			default:System.out.println("default"); 
		}
		
		//����break
		ts.testBreak();
		
		ts.example();
	}
	
	/**
	 * ����ʹ��switch��expr1��ʱ��Ҫע�⣬case��������ֵ��Χ������expr1�ڣ�������ֱ������
	 * ���磬byte���͵���ֵ��Χ��:-128-127,������һ������������case 225���ͻ���ִ�����Ϊ225����byte����ֵ��Χ
	 * TestSwitch.valueRange()
	 * @param b
	 * @return void
	 * Author��jllin
	 * 2013-4-16 ����10:55:43
	 */
	public static void valueRange(){
		byte a = 11;	
		switch(a){// C
		case 11 : System.out.println(" 11 "); break;
		case 13 : System.out.println(" 13 "); break;// D
		}
	}
	
	/**
	 * ����ʹ��ö����Ϊswitch����������
	 * TestSwitch.trafficLight()
	 * @return void
	 * Author��jllin
	 * 2013-4-16 ����11:04:29
	 */
	public static void trafficLight(){
		Signal color = Signal.RED;
		switch(color){
			case RED:color = Signal.GREEN;break;
			case YELLOW:color = Signal.RED;break;
			case GREEN:color = Signal.YELLOW;break;
		}
	}
	
	/**
	 * ����break��default��ʹ��
	 * break������:breakʹ�ó�����ִ����ѡ�еķ�֧�󣬿�����������switch��䣨������switch�ӵ�һ�ԣ���֮�󣩣����switch��
	 * ���û�����break,�����ڼ���ǰ������һ��֧��ֱ�����������break����switch��ɡ�
	 * default:���ܲ���ֵ��û��ƥ����ϣ�default��䶼��ִ��;���ƥ����ϣ����Һ������break��䣬��ô�ͻ�������switch��䣬
     * ����ִ��default����ˡ�
	 * TestSwitch.testBreak()
	 * @return void
	 * Author��jllin
	 * 2013-4-16 ����11:56:10
	 */
	public static void testBreak(){
		int a= 4;
		switch (a) {
		case 1:System.out.println("this is 1.");
			   break;
		case 2:System.out.println("this is 2.");
		   break;
		case 3:System.out.println("this is 3.");
		   break;
		default:System.out.println("this is default.");
		}
	}
	
	/**
	 * ���㣺�ǳ����defaultλ�ã�switch����������������Ǿ俪ʼ����ֱ������breakΪֹ;
	 * ���Ϊj=7��i=1,case����������Ŀ����ִ��default��default�����break,��ʵ��ִ��Ϊ:j+=2;j+=1;j+=4���ɴ˵�j=7��
	 * ��i=4,��ʵ��ִ��Ϊ��j+=1;j+=4,���ս��Ϊj=5��
	 * �ܽ᣺switch���ʽ��ֵ����ѡ���ĸ�case��֧������Ҳ�����Ӧ�ķ�֧����ֱ�Ӵ�"default" ��ʼ�����
	 * TestSwitch.example()
	 * @return void
	 * Author��jllin
	 * 2013-4-17 ����12:50:12
	 */
	public static void example(){
		int i = 1;
		int j = 0;
		switch(i){
			case 2:j+=6;
			default:j+=2;
			case 4:j+=1;
			case 0:j+=4;
		}
		
		System.out.println("the result is j = "+j);//�����7
	}
}
