package com.genericity;

/**
 * 
 * @ClassName: Generator
 * @Description: ����һ�����ͽӿ�
 * @author johnny.lin
 * @date 2018��2��3�� ����10:30:09
 * @version V1.0
 */
public interface Generator<T>
{
	public T next();
}
