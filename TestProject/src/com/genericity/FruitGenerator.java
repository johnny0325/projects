package com.genericity;

/**
 * δ���뷺��ʵ��ʱ���뷺����Ķ�����ͬ�����������ʱ���轫���͵�����Ҳһ��ӵ�����
 * ����class FruitGenerator<T> implements Generator<T>{
 * ������������ͣ��磺class FruitGenerator implements Generator<T>���������ᱨ��"Unknown class"
 */
public class FruitGenerator<T> implements Generator<T>
{
	@Override
	public T next()
	{
		return null;
	}

}
