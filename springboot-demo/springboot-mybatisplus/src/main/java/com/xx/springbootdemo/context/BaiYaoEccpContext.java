package com.xx.springbootdemo.context;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 白药电商对账系统上下文
 * </p>
 *
 * @author songjiale
 * @create 2021-11-23 13:41
 */
@Slf4j
public class BaiYaoEccpContext {
    /**
     * KEY_PREFIX：环境key前缀
     */
    public static final String KEY_PREFIX           = "baiyao-eccp-";
    /**
     * ENV: 环境变量
     */
    public static final String ENV                  = KEY_PREFIX + "env";

    /**
     * mock 电商对账平台-业务模块-核心服务
     */
    public static final String MOCK_CHANNEL         = KEY_PREFIX + "mock-business";

    /**
     * 白药支付上下文
     */
    private Map<String,String> contexts = new HashMap<String,String>();

    private static ThreadLocal<BaiYaoEccpContext> LOCAL = new ThreadLocal<>();

    /**
    * @Description: 批量设置参数
    * @Param: args
    * @return: void
    * @Date: 2021/11/23 16:17
    */
    public static void putAll(Map<String,String> args){
        BaiYaoEccpContext bypayContext = BaiYaoEccpContext.getAndSetBypayContext();
        bypayContext.getContext().putAll(args);
    }

    /**
    * @Description: 设置参数
    * @Param: key
    * @Param: value
    * @return: void
    * @Date: 2021/11/23 16:17
    */
    public static void put(String key,String value){
        BaiYaoEccpContext bypayContext = BaiYaoEccpContext.getAndSetBypayContext();
        bypayContext.getContext().put(key,value);
    }

    /**
    * @Description:通过key获取环境参数
    * @return: String
    * @Date: 2021/11/23 16:16
    */
    public static String get(String key){
        BaiYaoEccpContext bypayContext = LOCAL.get();
        if (bypayContext == null){
            throw new IllegalArgumentException("not found context");
        }
        return bypayContext.getContext().get(key);
    }

    /**
    * @Description:获取环境参数
    * @return: Map<String,String>
    * @Date: 2021/11/23 16:16
    */
    public static Map<String,String> get(){
        BaiYaoEccpContext bypayContext = LOCAL.get();
        if (bypayContext == null){
            return new HashMap<>();
        }
        return bypayContext.getContext();
    }

    /**
    * @Description: 获取或者设置上下文
    * @return: BypayContext
    * @Date: 2021/11/23 16:14
    */
    public static BaiYaoEccpContext getAndSetBypayContext(){
        BaiYaoEccpContext bypayContext = LOCAL.get();
        if (bypayContext == null){
            bypayContext = new BaiYaoEccpContext();
            LOCAL.set(bypayContext);
        }else{
            if (bypayContext.getContext() != null && bypayContext.getContext().size() > 0){
                log.info("上下文环境未清空，存在数据！{}",bypayContext.getContext());
            }
        }
        return bypayContext;
    }

    /**
    * @Description: 移除
    * @return: void
    * @Date: 2021/11/23 16:16
    */
    public static void remove(){
        if(LOCAL.get() != null){
            LOCAL.remove();
        }
    }

    /**
    * @Description:获取当前环境
    * @return: Map<String, String>
    * @Date: 2021/11/23 16:23
    */
    private Map<String, String> getContext() {
        return contexts;
    }
}
