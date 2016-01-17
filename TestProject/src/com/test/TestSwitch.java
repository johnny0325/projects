package com.test;

/**
 * 测试switch(expr1)中的expr1参数类型
 * switch（expr1）中，expr1是一个整数表达式。
 * 因此传递给 switch 和 case 语句的参数应该是 byte、 short、int、char或者enum 。long,string 都不能作用于swtich
 * default:不管参数值有没有匹配得上，default语句都会执行;如果匹配得上，而且后面带有break语句，那么就会跳出了switch语句，
 * 不再执行default语句了。
 * TestSwitch
 * Author：jlLin
 * Aug 23, 2011  7:37:33 PM
 * Copyright 华仝九方科技有限公司
 */

/**
 * switch-case的总结
 * 1、switch-case语句完全可以与if-else语句互转但通常来说switch-case语句执行效率要高。 
 * 2、default就是如果没有符合的case就执行它,default并不是必须的，它可以省略，但不推荐省略。
 * 3、case后的语句可以不用大括号. 
 * 4、switch语句的判断条件可以接受byte、 short、int、char或者enum，不能接受其他类型. 
 * 5、一旦case匹配,就会顺序执行后面的程序代码,而不管后面的case是否匹配,直到遇见break,利用这一特性可以让好几个case执行统一语句.
 */
public class TestSwitch {
	//定义一个枚举类型数据
	enum Signal {GREEN, YELLOW, RED } 
	
	public static void main(String args[]){
		
		TestSwitch ts = new TestSwitch();
		
		char i = 3;
		switch(i){
			case 1:System.out.println("value is 1.");break;
			case 2:System.out.println("value is 2.");
			default:System.out.println("default"); 
		}
		
		//测量break
		ts.testBreak();
		
		ts.example();
	}
	
	/**
	 * 测量使用switch（expr1）时，要注意，case的条件数值范围必须在expr1内，否则出现编译错误
	 * 例如，byte类型的数值范围是:-128-127,假如有一个这样的条件case 225，就会出现错误，因为225超出byte的数值范围
	 * TestSwitch.valueRange()
	 * @param b
	 * @return void
	 * Author：jllin
	 * 2013-4-16 下午10:55:43
	 */
	public static void valueRange(){
		byte a = 11;	
		switch(a){// C
		case 11 : System.out.println(" 11 "); break;
		case 13 : System.out.println(" 13 "); break;// D
		}
	}
	
	/**
	 * 测量使用枚举作为switch的数据类型
	 * TestSwitch.trafficLight()
	 * @return void
	 * Author：jllin
	 * 2013-4-16 下午11:04:29
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
	 * 测试break和default的使用
	 * break的作用:break使得程序在执行完选中的分支后，可以跳出整个switch语句（即跳到switch接的一对｛｝之后），完成switch。
	 * 如果没有这个break,程序将在继续前进到下一分支，直到遇到后面的break或者switch完成。
	 * default:不管参数值有没有匹配得上，default语句都会执行;如果匹配得上，而且后面带有break语句，那么就会跳出了switch语句，
     * 不再执行default语句了。
	 * TestSwitch.testBreak()
	 * @return void
	 * Author：jllin
	 * 2013-4-16 下午11:56:10
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
	 * 考点：非常规的default位置，switch语句是在条件满足那句开始运行直到遇到break为止;
	 * 结果为j=7。i=1,case语句无相符条目，故执行default。default语句无break,故实际执行为:j+=2;j+=1;j+=4，由此得j=7。
	 * 若i=4,则实际执行为：j+=1;j+=4,最终结果为j=5。
	 * 总结：switch表达式的值决定选择哪个case分支，如果找不到相应的分支，就直接从"default" 开始输出。
	 * TestSwitch.example()
	 * @return void
	 * Author：jllin
	 * 2013-4-17 上午12:50:12
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
		
		System.out.println("the result is j = "+j);//结果是7
	}
}
