package org.example.sorts;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 冒泡排序
 * @author hanyangyang
 * @since 2025/8/2
 **/
@Slf4j
public class BubbulSort {

    public static void main(String[] args) {
        int[] arr = {10,4,56,42,342,34,5,33,564,87};
        for (int i = 0; i < arr.length ; i++) {
            // 对于已经有序的数组，该条件可以使得在内层循环一次后直接跳出循环
            boolean sorted = true;
            for (int j = 0; j < arr.length-i-1; j++) {
                if (arr[j]>arr[j+1]){
                    int tem = arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=tem;
                    // 发生交换后说明是无序的
                    sorted = false;
                }
            }
            if (sorted){
                break;
            }
        }
        System.out.println(Arrays.toString(arr));
    }
}
