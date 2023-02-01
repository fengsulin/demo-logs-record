package com.example.log.utils;

/**
 * 异常工具类
 */
public class ThrowableUtil {

    /**
     * 转换异常信息为字符串
     * @param exceptionName 异常名称
     * @param exceptionMessage 异常信息
     * @param elements 堆栈信息
     * @return
     */
    public static String stackTraceToString(String exceptionName,String exceptionMessage,StackTraceElement[] elements){
        StringBuffer buffer = new StringBuffer();
        for (StackTraceElement stet : elements){
            buffer.append(stet+"\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n" + buffer.toString();
        return message;
    }
}
