package com.genericity;

/**
 * 
 * @ClassName: GenericMethodExample
 * @Description: ���ͷ���������
 * @author johnny.lin
 * @date 2018��2��4�� ����8:35:43
 * @version V1.0
 */
public class GenericMethodExample
{
	/**
	 * ���ͷ����Ļ�������
	 * @param tClass ����ķ���ʵ��
	 * @return T ����ֵΪT����
	 * ˵����
	 *     1��public�뷵��ֵ�м��<T>�ǳ���Ҫ���������Ϊ�����˷���Ϊ���ͷ�����
	 *     2��ֻ��������<T>�ķ������Ƿ��ͷ������������е�ʹ���˷��͵ĳ�Ա���������Ƿ��ͷ�����
	 *     3��<T>�����÷�����ʹ�÷�������T����ʱ�ſ����ڷ�����ʹ�÷�������T��
	 *     4���뷺����Ķ���һ�����˴�T�������дΪ�����ʶ����������T��E��K��V����ʽ�Ĳ��������ڱ�ʾ���͡�
	 */
	public <T> T genericMethod(Class<T> tClass) throws InstantiationException, IllegalAccessException
	{
		T instance = tClass.newInstance();
        return instance;
	}
	
	public static void main(String[] args) throws Exception
	{
		GenericMethodExample methodExample = new GenericMethodExample();
		TestObj obj = (TestObj) methodExample.genericMethod(Class.forName("com.genericity.TestObj"));
		System.out.println(obj.showValue());
	}
}
