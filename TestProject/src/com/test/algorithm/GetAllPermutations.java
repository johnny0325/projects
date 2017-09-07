package com.test.algorithm;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class GetAllPermutations {

    public static void main(String[] args) {
        Scanner scan = new Scanner(new BufferedInputStream(System.in));
        String s = scan.next();
        ArrayList<String> list = new ArrayList<>();
        getAllPermutations(list,s.toCharArray(),0,s.length());
        System.out.println("----------非字典序----------");
        for(int i = 0 ; i != list.size() ; i ++){
            System.out.println(list.get(i));
        }
        list.clear();
        System.out.println("----------字典序----------");
        getAllPermutations2(list,s.toCharArray(),0,s.length());
        for(int i = 0 ; i != list.size() ; i ++){
            System.out.println(list.get(i));
        }
        System.out.println(getCountOfAllPermutations(s.toCharArray(),0,s.length()));
        scan.close();
    }

    static int getCountOfAllPermutations(char[] cs,int start,int len){//start为数组序号
        int count = 1;
        int n = start + len ;
        for(int i = start ; i != n ; i ++ ){
            count *= i+1;
        }
        return count;
    }

    //非字典序
    static void getAllPermutations(ArrayList<String> answers,char[] cs,int start,int len){
        if(start == len ){
            answers.add(String.valueOf(cs));
            return;
        }
        for(int i = start ; i != len ; i ++){
            swap(cs,i,start);
            getAllPermutations(answers,cs,start+1,len);
            swap(cs,i,start);
        }
    }
    
    //字典序
    static void getAllPermutations2(ArrayList<String> list, char[] cs, int i, int length) {
        sort(cs);
        permutations(list,cs,i,length);
    }
    
    static void sort(char[] a) {//对字符数组进行快排
        int len = a.length;
        int low = 0,high = len - 1;
        quickSort(a, low, high);
    }

    static void quickSort(char[] a, int l ,int h){
        if(l>=h){
            return;
        }
        int low = l;
        int high = h;
        char k = a[low];
        while(low< high){
            //
            while(high>low&&a[high]>=k){//寻找元素右边比其小的
                high --;
            }
            a[low] = a[high];//进行交换，K指向high
            while(low<high&&a[low]<=k){//寻找元素左边比其大的
                low++;
            }
            a[high] = a[low];//进行交换，K指向low
        }
        a[low] = k;//将K赋给low
        quickSort(a, l, low-1);
        quickSort(a, low+1, h);
    }

    static void permutations(ArrayList<String> answers,char[] cs,int start,int len){//cs为字典序数组
        if(cs==null)
            return;
        while(true)
        {
            answers.add(String.valueOf(cs));
            int j=start+len-2,k=start+len-1;
            while(j>=start && cs[j]>cs[j+1])
                --j;
            if(j<start)
                break;

            while(cs[k]<cs[j])
                --k;

            swap(cs,k,j);

            int a,b;
            for(a=j+1,b=start+len-1;a<b;++a,--b)
            {
                swap(cs,a,b);
            }
        }
    }

    static void swap(char[] cs , int i , int j){
        char t;
        t = cs[i];
        cs[i] = cs[j];
        cs[j] = t;
    }
}
