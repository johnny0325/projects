package com.example;

public class YangHui
{
	public static void main(String args[])
	{
		//int k=Integer.parseInt(args[0])+1;
		int k= 5;
		int[][] a=new int[k][k];
		int i,j;
		for(i=0;i<k;i++)
		{
			a[i][i]=1;
			a[i][1]=1;
		}
		//核心代码,i是行,j是列
		for(i=3;i<k;i++)
			for(j=2;j<=i-1;j++)
				a[i][j]=a[i-1][j-1]+a[i-1][j];
		
		//输出杨辉三角
		for(i=1;i<k;i++)
		{
			for(j=1;j<=i;j++)
				System.out.print(a[i][j]+" ");
			System.out.println();
		}	
	}
}