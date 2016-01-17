package com.test.thread;

/**
 * ��Ŀ���������߳�ID�ֱ���A��B��C,���ö��߱��ʵ�֣�����Ļ��ѭ����ӡ10��ABCABC��
 * �����������߳�ִ�еĲ�ȷ���ԣ�Ҫ��֤��������������������ƺö��̵߳�ͬ����
 * �߳�ͬ�������ֻ���������(1)ʹ��synchronized�ؼ��֣�����ͬ��������ʵ��ͬ�� (2)ʹ���̵߳ĳ��÷���wait,notify,notifyAll
 * XunleiInterReviewMultithread
 * Author��jlLin
 * Dec 4, 2011  2:42:41 PM
 * Copyright ���ھŷ��Ƽ����޹�˾
 */
public class XunleiInterReviewMultithread {

	/**
	 * ����������
	 * @param args
	 * @return void
	 * Author��jlLin
	 * Dec 4, 2011 2:07:49 PM
	 */
	public static void main(String[] args) {
		XunleiLock lock = new XunleiLock();
		new Thread(new XunleiPrinter("A",lock)).start();
		new Thread(new XunleiPrinter("B",lock)).start();
		new Thread(new XunleiPrinter("C",lock)).start();
		
	}

}

//�ⷨһ
class XunleiPrinter implements Runnable{
	private String name = "";
	private XunleiLock lock;
	private int count = 10;
	
	public XunleiPrinter(String name,XunleiLock lock){
		this.name = name;
		this.lock = lock;
	}

	public void run(){
		while(count > 0){System.out.println("count/lock'name/this'name==>"+count+" "+lock.name+" "+this.name);
			synchronized(lock){
				if(lock.name.equalsIgnoreCase(this.name)){//�����̵߳�������
					System.out.println(name);
					count--;
					
					if(this.name.equals("A")){
						lock.setName("B");
					}else if(this.name.equals("B")){
						lock.setName("C");
					}else if(this.name.equals("C")){
						lock.setName("A");
					}
				}
			}
		}
	}
	
}

class XunleiLock {
	public String name = "A";
	
	public void setName(String name){
		this.name = name;System.out.println("lock setName==>"+name);
	}
	
	public String getName(){
		return name;
	}
}
