package com.example.projectweb.core.context;

import cn.hutool.core.collection.CollUtil;
import com.example.projectweb.core.RequestConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hanyangyang
 * @since 2023/8/8
 */
public class RequestContext {
    private final static ThreadLocal<Map<String,String>> REQUEST_CONTEXT = new ThreadLocal<>();


    public static void setRequestId(String requestId){
        Map<String, String> map = REQUEST_CONTEXT.get();
        if (CollUtil.isEmpty(map)){
            map = new HashMap<>();
            REQUEST_CONTEXT.set(map);
        }
        map.put(RequestConstant.REQUEST_ID,requestId);
    }

    public static String getRequestId(){
        Map<String, String> map = REQUEST_CONTEXT.get();
        if (CollUtil.isEmpty(map) || !map.containsKey(RequestConstant.REQUEST_ID)){
           return "";
        }
        return map.get(RequestConstant.REQUEST_ID);
    }

    public static void remove(){
        REQUEST_CONTEXT.remove();
    }


}
