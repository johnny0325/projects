package com.example;

import java.util.Arrays;


/**
 * ˼��:���ǽ���ʽ�����㷨��һ��.��С��ֵ"��"������,�����ֵ"��"���ײ���һ�����򷽷�.
 * n��Ԫ�ص����򽫽���n-1��ѭ��,��ÿһ�����������ڵ�Ԫ�ؽ��бȽ�,�����ߵ�С�ڻ�����ұߵ�,
 * ������ԭλ�ò���,�����ߵĴ����ұߵ�,���������ұߵ�Ԫ�ص�λ�ý���.
 * BubbleSort
 * Author��jllin
 * 2013-4-17  ����10:57:34
 */
public class BubbleSort {

	/**
	 * BubbleSort.main()
	 * @param args
	 * @return void
	 * Author��jllin
	 * 2013-4-17 ����10:24:10
	 */
	public static void main(String[] args) {
		// ����һ����������
		int[] nums = new int[]{19,23,2,8,24};
		
		//����һ����ʱ����
		int temp = 0;
		
		//���������ð������
		
		/**************************************��һ�ַ���**********************************************/
		//�����
		for(int i = nums.length-1; i>1; i--){
			//�ڿ�����Щ���ݽ��бȽ�
			//�������ݽ��д�С�Ƚϣ����ǰһ�������ں�һ�������򽻻�λ�ã��ô�������ں���
			for(int j = 0;j < i; j++){
				if(nums[j]>nums[j+1]){
					temp = nums[j];
					nums[j] = nums[j+1];
					nums[j+1] = temp;
				}
			}
		}
		
		/**************************************�ڶ��ַ���**********************************************/
		//n��Ԫ�ص��������n-1������
		for(int k=0;k<nums.length-1;k++){
			//��Ϊÿһ��ѭ����ȷ��һ������Ԫ�ص�λ��,
			//����ÿһ�ֵıȽϴ�������ݼ�
			for(int n=0;n<nums.length-k-1;n++){
				if(nums[n]>nums[n+1]){
					temp = nums[n];
					nums[n] = nums[n+1];
					nums[n+1] = temp;
				}
			}
		}
		
		//��������������
		for(int k : nums){
			System.out.print(k+",");
		}
		
		//���ٴ�ӡ��������
		System.out.println(Arrays.toString(nums));
	}
	
	
	
}
