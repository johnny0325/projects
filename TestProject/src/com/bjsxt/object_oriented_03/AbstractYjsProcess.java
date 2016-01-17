package com.bjsxt.object_oriented_03;

/**
 * 定义抽象类AbstractYjsProcess，并实现接口IYjsCommonProcess，
 * 同时只实现了deal()方法，另外一个方法不实现
 * 为什么只实现了deal()方法？因为这个是公共的方法，而另外一个是个性的方法，具体的类有不同的实现内容
 * AbstractYjsProcess
 * Author：jllin
 * 2013-8-16  上午10:26:55
 */
public abstract class AbstractYjsProcess implements IYjsCommonProcess {

	@Override
	public void deal(Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	

}
