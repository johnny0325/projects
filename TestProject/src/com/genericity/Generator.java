package com.genericity;

/**
 * 
 * @ClassName: Generator
 * @Description: 定义一个泛型接口
 * @author johnny.lin
 * @date 2018年2月3日 下午10:30:09
 * @version V1.0
 */
public interface Generator<T>
{
	public T next();
}
