package org.example;

import java.util.*;

/**
 * @author hanyangyang
 * @since 2025/8/14
 **/
public class FindUniqueNum {
    public static void main(String[] args) {
        int[]  arr= {1,2,4,5,3,4,2,2,5};
        System.out.println(Arrays.toString(findUniqueNum(arr)));
        System.out.println(Arrays.toString(findUniqueByLinkHashMapOrderly(arr)));
        System.out.println(Arrays.toString(findUniqueByDubleSet(arr)));
        System.out.println(Arrays.toString(findUniqueBySortCompare(arr)));
        System.out.println(Arrays.toString(findUniqueByBit(arr)));
        System.out.println(Arrays.toString(findUniqueByPoint(arr)));
    }
    public static int[] findUniqueNum(int[] arr){
        Map<Integer,Integer> map = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            map.put(arr[i],map.getOrDefault(arr[i],0)+1);
        };
        List<Integer> result = new ArrayList<>();
        for (Integer i : map.keySet()) {
            if(map.get(i)==1){
                result.add(i);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int[] findUniqueByLinkHashMapOrderly(int[] arr){
        Map<Integer,Integer> map = new LinkedHashMap<>();
        List<Integer> result = new ArrayList<>();

        for (int i : arr) {
            map.put(i,map.getOrDefault(i,0)+1);
        }
        for (Integer i : map.keySet()) {
            if (map.get(i)==1){
                result.add(i);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int[]  findUniqueByDubleSet(int[] arr){
        Set<Integer> oneceSet = new HashSet<>();
        Set<Integer> multipSet = new HashSet<>();
        for (int i : arr) {
            if (!multipSet.contains(i)){
                if (oneceSet.contains(i)){
                    oneceSet.remove(i);
                    multipSet.add(i);
                }else {
                    oneceSet.add(i);
                }
            }
        }
        return oneceSet.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int[] findUniqueBySortCompare(int[] arr){
        Arrays.sort(arr);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            boolean unique = true;
            if (i!=arr.length-1 && arr[i]== arr[i+1]){
                unique=false;
            }
            if (i>0 && arr[i]==arr[i-1]){
                unique=false;
            }
            if (unique){
                result.add(arr[i]);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 通过异或运算找到唯一一个出现一次的数次（前提，其他数字都是出现两次，或者都是出现偶数次）
     * 算法依据 两个相同的数字异或的结果为0
     * @param arr
     * @return
     */
    public static int findUnqiueNum(int[] arr){
        int result=0;
        for (int i = 0; i < arr.length; i++) {
            result^=arr[i];
        }
        return result;
    }

    /**
     * 通过两位来表示出现的次数
     * 00 表示没有出现过
     * 01 出现一次
     * 10 出现多次
     *
     * bitSet中从index0 开始算作低位
     * @param arr
     * @return
     */
    public static int[] findUniqueByBit(int[] arr){
        BitSet bitSet = new BitSet(100);
        for (int i : arr) {
            int startIndex = i*2; // 获取低位下标
            boolean firstBit = bitSet.get(startIndex);
            boolean secondBit = bitSet.get(startIndex + 1);

            if (!firstBit && !secondBit){
                // 首次出现 低位1 高位0
                bitSet.set(startIndex,true);
            }else if (firstBit && !secondBit){
                bitSet.set(startIndex,false);
                bitSet.set(startIndex+1,true);
            }
        }
        List<Integer> result = new ArrayList<>();
        for (int i : arr) {
            int bitIndex = i*2;
            if (bitSet.get(bitIndex) && !bitSet.get(bitIndex+1)){
                result.add(i);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();

    }

    /**
     * 双指针法（前提是数组有序）
     * 慢指针用于标注当前判断的元素，快指针用于向前移动找到不一样的数据
     * 当快指针 移动到 和慢指针不同的元素的时候，那么快指针的下标减去慢指针的下标就是慢指针对应元素的出现次数
     * 如果为1 则可以提取出来
     * @param arr
     * @return
     */
    public static int[] findUniqueByPoint(int[] arr){
        Arrays.sort(arr);
        int i=0;
        int j=1;
        List<Integer> result = new ArrayList<>();
        while(j<arr.length){
            if (arr[i] != arr[j]) {
                int count = j-i;
                if (count==1){
                    result.add(arr[i]);
                }
                i=j;
            }
            j++;
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

}
