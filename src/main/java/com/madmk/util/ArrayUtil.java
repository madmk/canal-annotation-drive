package com.madmk.util;

/**
 * @author madmk
 * @date 2020/9/4 13:19
 * @description: 数组处理
 */
public class ArrayUtil {

    public static void main(String[] args) {

    }

    public static boolean exist(Object[] arr,Object a){
        if(arr==null||arr.length==0){
            return false;
        }
        if(a==null){
            for (Object o : arr) {
                if(a==o){
                    return true;
                }
            }
            return false;
        }
        for (Object o : arr) {
            if(a.equals(o)){
                return true;
            }
        }
        return false;
    }
}
