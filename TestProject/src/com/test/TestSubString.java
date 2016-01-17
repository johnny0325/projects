package com.test;

public class TestSubString {

	/**
	 * 
	 * TestSubString.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2014-5-5 下午12:10:39
	 */
	public static void main(String[] args) {
		
		/**
		 * public String substring(int beginIndex,int endindex)
		 * 返回一个新字符串，它是此字符串的一个子字符串。该子字符串从指定的 beginIndex处开始， endindex:表示结尾处索引。
		 * 参数：
		 * beginIndex - 开始处的索引（包括）
		 * endindex 结尾处索引（不包括）
		 */
		String workDate = "20140505";
		System.out.println(workDate.substring(4,6));
	}

}
