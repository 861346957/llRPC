package com.ll.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.constant.ClientConstant;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 14:48
 */
public class StringCustomUtils {
    public static ObjectMapper mapper=new ObjectMapper();
    public static Integer getInteger(Object obj) {
        if(obj==null){
            return null;
        }
        Integer val;
        try {
            if(obj instanceof String){
                val=Integer.parseInt((String)obj);
            }else{
                val=(Integer) obj;
            }
        } catch (Exception e) {
            val=null;
        }
        return val;
    }
    public static   Long getLong(Object obj){
        if(obj==null){
            return null;
        }
        Long val;
        try {
            if(obj instanceof String){
                val= Long.parseLong((String)obj);
            }else{
                val=(Long) obj;
            }
        } catch (Exception e) {
            val=null;
        }
        return val;
    }
    public static String getString(String separator,Object... objects){
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            if(obj==null){
                obj="";
            }
            sb.append(obj).append(separator);
        }
        sb.delete(sb.length()-separator.length(),sb.length());
        return sb.toString();
    }

    public static String getErrorMessage(Throwable t) {
        if (null == t) {
            return "";
        }
        try(StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw)
        ){
            t.printStackTrace(pw);
            return sw.toString();
        }catch (Throwable e){
            return null;
        }
    }
    public static String getClassName(Class clazz){
        String name=clazz.getName();
        if(isEmpty(name)){
            return null;
        }
        int index = name.lastIndexOf(ClientConstant.DEFAULT_CLASS_SEPARATOR);
        if(index==-1){
            return null;
        }
        return getInitialLowercase(name.substring(index+1));

    }
    public static String getInitialLowercase(String str){
        if(isEmpty(str)){
            return null;
        }
        char[] cs=str.toCharArray();
        if(cs[0]>=65 && cs[0]<=90){
            cs[0]+=32;
        }
        return String.valueOf(cs);
    }
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static String getJsonByObject(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
