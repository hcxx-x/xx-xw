/**
 * 
 */
package com.xx.springbootdemo.context;


import com.xx.springbootdemo.util.BaiYaoUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lidongfu
 *
 */
public class HttpRequestContext {
	/**
	 * 获取HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		try{
			return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		}
		catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 获取远程地址
	 */
	public static String getRemoteAddr() {
		HttpServletRequest request = getRequest();
		return request != null ? BaiYaoUtil.getRealIP(request) : "";
	}
}
