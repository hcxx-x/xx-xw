/**
 * 
 */
package com.xx.springbootdemo.config;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;

import com.xx.springbootdemo.context.BaiYaoEccpContext;
import com.xx.springbootdemo.context.HttpRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author lidongfu
 * @title LogInterceptor.java
 * @date 2021年3月24日
 * 
 *       为系统入口方法添加日志定制输出
 * 
 */
@Slf4j
public class LogInterceptor extends AbstractInterceptor {

	public static final String RID = "rid";
	public static final String IP = "ip";  
	public static final String MOCK = "mock";
	public static final String CIP = "cip"; //目前只关心客户端IP
	public static final String METHOD_NAME_EN = "METHOD_NAME_EN";
	public static final String METHOD_NAME_CH = "METHOD_NAME_CH";


	@Override
	public Object doIntercept(MethodInvocation invocation) throws Throwable {

		String uuid = MDC.get(RID);
		String serverIp = MDC.get(IP);
		String cip = MDC.get(CIP);

		try {

			if (CharSequenceUtil.isEmpty(uuid)) {
				MDC.put(RID, generateRequestID()); 
			}
			
			if (CharSequenceUtil.isEmpty(serverIp)) {
				MDC.put(IP, generateServerIP());
			}
			
			if (CharSequenceUtil.isEmpty(cip)) {
				MDC.put(CIP, generateHttpClientIP());
			}
			
			return invocation.proceed();

		} finally {
			MDC.clear();
			BaiYaoEccpContext.remove();
		}
	}
	
	public static String generateRequestID() {
		return RandomUtil.randomStringUpper(16);
	}
	
	public static String generateServerIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "";
		}
	}
	
	public static String generateHttpClientIP() {
		return HttpRequestContext.getRemoteAddr();
	}

}
