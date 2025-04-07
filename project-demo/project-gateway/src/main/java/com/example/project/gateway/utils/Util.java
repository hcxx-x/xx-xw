package com.example.project.gateway.utils;

import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.project.gateway.constant.ProjectConstants;
import com.example.project.gateway.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;


import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * @Description 通用方法工具类
 * @author
 *
 */

@Slf4j
public final class Util {

	/**
	 * 基包路径
	 */
	private final static String YNBY_BASE_PATH = "com.ynby";
	/**
	 * 缩进
	 */
	private final static String RETRACT = "\t";

	/**
	 * 换行
	 */
	private final static String LINE_BREAK = "\n";

	private Util() {
	}

	/**
	 * 生成UUID
	 * 
	 * @return UUID
	 */
	public static String randomUUID() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replace("-", "");
		return uuid.toUpperCase();
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return StringUtils.isEmpty(str);
	}

	public static boolean isNotEmpty(String str) {
		return !StringUtils.isEmpty(str);
	}
	
	/**
	 * 判断是否全部为空
	 */
    public static boolean isAllEmpty(final String... strValue) {
        for (final String str : strValue) {
            if (isNotEmpty(str)) {
                return false;
            }
        }
        return true;
    }

	/**
	 * 按字节长度对字符串补位
	 * 
	 * @param value
	 * @param charset
	 * @param length
	 * @param c
	 * @param left
	 * @return String
	 */
	public static String pad(String value, String charset, int length, char c, boolean left) {
		String result = value;

		try {
			value = trim(value);
			result = truncate(value, charset, length, left);
			byte[] bytes = result.getBytes(charset);
			if (bytes.length < length) {
				StringBuilder sb = new StringBuilder(length);
				int num = length - bytes.length;
				while (num-- > 0) {
					sb.append(c);
				}
				if (left) {
					sb.append(result);
				} else {
					sb.insert(0, result);
				}
				result = sb.toString();
			}
		} catch (Exception e) {
			log.error(value + "|" + charset + "|" + length + "|" + c + "|" + left, e);
		}

		return result;
	}

	/**
	 * 按字节长度对字符串补位
	 * 
	 * @param value
	 * @param length
	 * @param c
	 * @param left
	 * @return String
	 */
	public static String pad(String value, int length, char c, boolean left) {
		return pad(value, ProjectConstants.DEFAULT_CHARSET, length, c, left);
	}

	/**
	 * 按字节长度截取字符串
	 * 
	 * @param value
	 * @param charset
	 * @param length
	 * @param left
	 * @return String
	 */
	public static String truncate(String value, String charset, int length, boolean left) {
		try {
			value = trim(value);
			byte[] bytes = value.getBytes(charset);
			if (bytes.length > length) {
				byte[] temp = new byte[length];
				if (left) {
					System.arraycopy(bytes, bytes.length - length, temp, 0, length);
				} else {
					System.arraycopy(bytes, 0, temp, 0, length);
				}
				value = new String(temp, charset);
			}
		} catch (Exception e) {
			log.error(value + "|" + charset + "|" + length + "|" + left, e);
		}

		return value;
	}

	public static JSONObject parseHttpRequest(HttpServletRequest request) throws Exception {
		StringBuilder sb = null;
		try (ServletInputStream inputStream = request.getInputStream();
			 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));) {
			String line = null;
			sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			log.error("解析上送原始数据异常 ", e);
			throw e;
		}
		return JSON.parseObject(sb.toString());
	}

	/**
	 * 异常格式化为字符串
	 */
	public static String exception2String(Throwable t) {
		StringBuilder sb = new StringBuilder();

		sb.append(t.toString());
		sb.append("\n");

		StackTraceElement[] stes = t.getStackTrace();
		for (StackTraceElement ste : stes) {
			sb.append(ste.toString());
			sb.append("\n");
		}
		return sb.toString();
	}


	/**
	 * 获取异常堆栈信息(仅获取当前系统包下的异常)
	 *
	 * @param t 异常
	 * @return
	 */
	public static String throwableMessage(Throwable t) {
		StringBuilder sb = new StringBuilder();
		sb.append(t.toString()).append(LINE_BREAK);
		for (Throwable throwable : t.getSuppressed()) {
			sb.append(throwableMessage(throwable, RETRACT)).append(LINE_BREAK);
		}
		sb.append(stackTraceStr("", t.getStackTrace(), YNBY_BASE_PATH));
		return sb.toString();
	}
	/**
	 * 获取异常堆栈信息
	 *
	 * @param t 异常
	 * @param retract 缩进
	 * @return
	 */
	public static String throwableMessage(Throwable t, String retract) {
		final StringBuilder sb = new StringBuilder();
		sb.append(retract).append(t.getClass().getName()).append(LINE_BREAK);
		sb.append(Arrays.stream(t.getMessage().split(LINE_BREAK)).filter(msg->msg.contains(YNBY_BASE_PATH)).map(msg->retract + msg).collect(Collectors.joining(LINE_BREAK))).append(LINE_BREAK);
		sb.append(stackTraceStr(retract, t.getStackTrace(), YNBY_BASE_PATH));

		for (Throwable throwable : t.getSuppressed()) {
			sb.append(throwableMessage(throwable, retract));
		}
		return sb.toString();
	}



	/**
	 * 抽取StackTrace中包含basePath的信息并格式化
	 *
	 * @param retract
	 * @param stackTraceElements
	 * @param basePath
	 * @return
	 */
	private static String stackTraceStr(String retract, StackTraceElement[] stackTraceElements, String basePath) {
		if(Objects.isNull(stackTraceElements) || stackTraceElements.length == 0){
			return "";
		}
		final String stackTaceInfo = Arrays.stream(stackTraceElements).map(stack -> RETRACT + retract + stack.toString()).filter(msg -> msg.contains(basePath)).collect(Collectors.joining(LINE_BREAK));
		if(StrUtil.isBlank(stackTaceInfo)){
			return "";
		}
		return retract + "Stack trace:" + LINE_BREAK + stackTaceInfo;
	}


	/**
	 * trim字符串,兼容null值
	 */
	public static String trim(String value) {
		return value == null ? "" : value.trim();
	}

	/**
	 * 数字类型格式化为字符串 format eg: ########0.00
	 */
	public static String number2String(BigDecimal value, String format) {
		String result = null;

		try {
			DecimalFormat df = new DecimalFormat(format);
			result = df.format(value);
		} catch (Exception e) {
			log.error(value + " | " + format, e);
		}

		return result;
	}
	
	/**
	 * 
	 * @param params 数据
	 * @return 返回QueryString格式的数据
	 */
	private static final String[] SIGN_IGNORES= {"sign", "refund_detail_list"};  //SIGN,退款明细不参与签名
	public static String createQueryString(Map<String, Object> params) {
		List<String> keys = new ArrayList<>(params.keySet());
		Collections.sort(keys);
		StringBuilder sb = new StringBuilder(128);
		for (String key : keys) {
			if (Arrays.asList(SIGN_IGNORES).contains(key)) {
				continue;
			}
			String value = null;
			
			Object obj = params.get(key);
			if (obj instanceof String) {
				value = String.valueOf(obj);
			} else {
				value = JSON.toJSONString(obj);
			}
			
			if (!Objects.isNull(value) && value.length() > 0) {
				if (sb.length() != 0) {
					sb.append("&");
				}
				sb.append(key).append("=").append(value);
			}
		}
		return sb.toString();
	}
	
	public static String getRealIP(HttpServletRequest request) {
	    String ip = null;
		//X-Forwarded-For：Squid 服务代理
		String ipAddresses = request.getHeader("X-Forwarded-For");
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//Proxy-Client-IP：apache 服务代理
			ipAddresses = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//WL-Proxy-Client-IP：weblogic 服务代理
			ipAddresses = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//HTTP_CLIENT_IP：有些代理服务器
			ipAddresses = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			//X-Real-IP：nginx服务代理
			ipAddresses = request.getHeader("X-Real-IP");
		}
		//有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
		if (ipAddresses != null && ipAddresses.length() != 0) {
			ip = ipAddresses.split(",")[0];
		}
		//还是不能获取到，最后再通过request.getRemoteAddr();获取
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
			ip = request.getRemoteAddr();
		}
	    return ip;
	}

	/**
	 * response直接响应json 对象
	 *
	 * @param httpServletResponse
	 * @param response
	 * @throws IOException
	 */
	public static void writeResponse(HttpServletResponse httpServletResponse, Response<?> response) throws IOException {
		httpServletResponse.setContentType("application/json");
		httpServletResponse.setCharacterEncoding("UTF-8");
		try(PrintWriter out = httpServletResponse.getWriter()) {
			out.print(JSON.toJSONString(response));
			out.flush();
		}
	}

	/**
	 * 可能的超长字符串日志打印
	 *
	 * @param log
	 * @param str 不定长字符串(可能极大，超长会被截断省略超长部分)
	 * @return
	 */
	public static String mayLargeLogPrintStr(Logger log, String str){
		if(!log.isDebugEnabled() && Objects.nonNull(str) && str.length() > 5120){
			return str.substring(0, 5120) + "... ..." + str.substring(str.length() - 1);
		}
		return str;
	}
}
