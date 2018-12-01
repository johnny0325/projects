package com.genericity;

/**
 * 
 * @ClassName: Generic
 * @Description: ����һ�������࣬�˴�T�������дΪ�����ʶ����������T��E��K��V����ʽ�Ĳ��������ڱ�ʾ���ͣ���ʵ����������ʱ������ָ��T�ľ�������
 * @author johnny.lin
 * @date 2018��2��3�� ����10:22:10
 * @version V1.0
 */
public class Generic<T>
{
	//key�����Ա����������ΪT,T���������ⲿָ��  
    private T key;

    public Generic(T key) { //���͹��췽���β�key������ҲΪT��T���������ⲿָ��
        this.key = key;
    }

    public T getKey(){ //���ͷ���getKey�ķ���ֵ����ΪT��T���������ⲿָ��
        return key;
    }
    
    public static void main(String[] args)
	{
    	//���͵����Ͳ���ֻ���������ͣ������Զ����ࣩ�������Ǽ�����
    	//�����ʵ���������뷺�͵����Ͳ���������ͬ����ΪInteger.
    	Generic<Integer> genericInteger = new Generic<Integer>(123456);

    	//�����ʵ���������뷺�͵����Ͳ���������ͬ����ΪString.
    	Generic<String> genericString = new Generic<String>("key_vlaue");
    	System.out.println("���Ͳ���, key is " + genericInteger.getKey());
    	System.out.println("���Ͳ���, key is " + genericString.getKey());
    	
    	Generic generic = new Generic("111111");
    	Generic generic1 = new Generic(4444);
    	Generic generic2 = new Generic(55.55);
    	Generic generic3 = new Generic(false);
    	
    	System.out.println("���Ͳ���, key is " + generic.getKey());
    	System.out.println("���Ͳ���, key is " + generic1.getKey());
    	System.out.println("���Ͳ���, key is " + generic2.getKey());
    	System.out.println("���Ͳ���, key is " + generic3.getKey());
	}
}
