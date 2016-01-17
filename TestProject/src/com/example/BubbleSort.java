package com.example;

import java.util.Arrays;


/**
 * 思想:它是交换式排序算法的一种.将小的值"浮"到上面,将大的值"沉"到底部的一种排序方法.
 * n个元素的排序将进行n-1轮循环,在每一轮排序中相邻的元素进行比较,如果左边的小于或等于右边的,
 * 将保持原位置不变,如果左边的大于右边的,将这两个右边的元素的位置交换.
 * BubbleSort
 * Author：jllin
 * 2013-4-17  下午10:57:34
 */
public class BubbleSort {

	/**
	 * BubbleSort.main()
	 * @param args
	 * @return void
	 * Author：jllin
	 * 2013-4-17 下午10:24:10
	 */
	public static void main(String[] args) {
		// 定义一个整形数组
		int[] nums = new int[]{19,23,2,8,24};
		
		//定义一个临时变量
		int temp = 0;
		
		//对数组进行冒泡排序
		
		/**************************************第一种方法**********************************************/
		//外控制
		for(int i = nums.length-1; i>1; i--){
			//内控制哪些数据进行比较
			//两两数据进行大小比较，如果前一个数大于后一个数，则交换位置，让大的数放在后面
			for(int j = 0;j < i; j++){
				if(nums[j]>nums[j+1]){
					temp = nums[j];
					nums[j] = nums[j+1];
					nums[j+1] = temp;
				}
			}
		}
		
		/**************************************第二种方法**********************************************/
		//n个元素的数组进行n-1轮排序
		for(int k=0;k<nums.length-1;k++){
			//因为每一轮循环将确定一个数组元素的位置,
			//所以每一轮的比较次数将会递减
			for(int n=0;n<nums.length-k-1;n++){
				if(nums[n]>nums[n+1]){
					temp = nums[n];
					nums[n] = nums[n+1];
					nums[n+1] = temp;
				}
			}
		}
		
		//遍历排序后的数据
		for(int k : nums){
			System.out.print(k+",");
		}
		
		//快速打印数组数据
		System.out.println(Arrays.toString(nums));
	}
	
	
	
}
