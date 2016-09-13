package com.bjsxt.basegrammar_02;

public class TestConvert {

	/**
	 * 基本数据类型转换
	 * boolean类型不可以转换为其他的数据类型。
	 * 整形、字符型、浮点型的数据在混合运算中相互转换，转换时遵循以下原则：
	 * 1、容量小的类型自动转换为容量大的数据类型；数据类型按容量大小(指表示的数的大小,比如float表示的数比long要多得多)排序为：
	 *    byte,short,char->int->long->float->double;
	 *    byte,short,char之间不会互相转换，他们三者在计算时首先会转换为int类型。
	 *  2、容量大的数据类型转换为容量小的数据类型时，要加上强制转换符，但可能造成精度降低或溢出，使用时要格外注意。
	 *  3、有多种类型的数据混合运算时，系统首先自动的将所有数据转换成容量最大的那一种数据类型，然后再进行计算。
	 *  4、小数常量（如1.2），默认为double
	 *  5、整数常量（如123），默认为int
	 * TestConvert.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-7-31 下午05:15:20
	 */
	public static void main(String[] args) {
		int i1 = 123,i2 = 456;
		double d1 = (i1+i2)*1.2;	//系统将转换为double型运算
		float f1 = (float)((i1+i2)*1.2);	//需要加强制转换符
		
		//这里比较特殊，1和2都是int型，你可以直接把int型的数直接赋值给byte、short或者char型，只要你不超出它的表数范围都可以
		//什么意思呢，比如byte能表示的最大正数是127，你不能写byte=129,这不行
		byte b1 = 1,b2 = 2;	
		byte b3 = (byte)(b1+b2);	//系统将转换为int型运算，需要加强制转换符(直接赋值 可以把int赋给byte，但是做运算了，int就要强制转换为byte)
		
		double d2 = 1e200;//1e200,表示1*10的200次方
		//会产生溢出,原因：浮点数在计算机内部它是用一种特殊的形式表示，它专门中间存了一个小数点，来计算小数点后面有几位，
		//所以你直接把double后四位斩掉，你是转换不过来的,因此double转换成float会出问题，会是Infinity，会说是无穷大
		float f2 = (float)d2;	
		System.out.println(f2);
		 
		float f3 = 1.23f;//必须加f
		long l1 = 123;
		long l2 = 30000000000L;//必须加l或者L
		float f = l1+l2+f3;	//系统转转换为float型计算
		long l = (long)f;	//强制转换会舍去小数部分(不是四舍五入)
	}

}
