package com.xx.springbootdemo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.MimeHeaders;
import org.slf4j.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class BaiYaoUtil {
    private BaiYaoUtil() {
    }

    /**
     * 获取客户端的真实IP地址。
     * @param request HttpServletRequest对象，包含了客户端请求的所有信息。
     * @return 如果通过HTTP头部字段找到了有效的IP地址，则返回第一个有效的IP地址；如果所有头部字段都无效，则回退到使用远程地址。
     */
    public static String getRealIP(HttpServletRequest request) {
        // 定义可能包含客户端真实IP的HTTP头部字段数组
        String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP"};
        
        // 遍历所有可能的头部字段
        for (String header : headers) {
            // 获取当前头部字段的值
            String ipHeaderValue = request.getHeader(header);
            
            // 检查头部字段值是否有效（非空且不为"unknown"）
            if (ipHeaderValue != null && ipHeaderValue.length() != 0 && !"unknown".equalsIgnoreCase(ipHeaderValue)) {
                // 返回第一个有效的IP地址，如果有多个IP地址则取第一个
                return ipHeaderValue.split(",")[0];
            }
        }
        
        // 如果所有头部字段都无效，则回退到使用远程地址
        return request.getRemoteAddr();
    }

    /**
     * 检查日志级别并截断过长的字符串。
     * 如果日志级别不是调试模式且字符串长度超过5120字符，则截取前5120个字符和最后一个字符，并在中间添加省略号。
     * 否则返回原字符串。
     *
     * @param log Logger对象，用于检查日志级别
     * @param str 需要处理的字符串
     * @return 处理后的字符串或原字符串
     */
    public static Object mayLargeLogPrintStr(Logger log, String str) {
        // 检查日志级别是否为调试模式，并且字符串不为空且长度大于5120
        if(!log.isDebugEnabled() && Objects.nonNull(str) && str.length() > 5120){
            // 截取前5120个字符和最后一个字符，并在中间添加省略号
            return str.substring(0, 5120) + "... ..." + str.substring(str.length() - 1);
        }
        // 如果条件不满足，返回原字符串
        return str;
    }


    /**
     * 修改请求头信息
     *
     * @param headerMap 包含要修改的请求头的键值对
     * @param request   需要修改的ServletRequest对象
     */
    public static void modifyHeaders(Map<String, String> headerMap, ServletRequest request) {
        // 如果headerMap为空或没有元素，直接返回
        if (headerMap == null || headerMap.isEmpty())  {
            return;
        }

        try {
            // 安全获取CoyoteRequest对象
            Object coyoteRequest = getCoyoteRequest(unwrapRequest(request));
            if (coyoteRequest == null) {
                // 记录警告日志，无法获取CoyoteRequest对象
                log.warn(" 无法获取CoyoteRequest对象，请求头修改失败");
                return;
            }

            // 获取MimeHeaders对象
            MimeHeaders headers = getMimeHeaders(coyoteRequest);
            if (headers != null) {
                // 遍历headerMap并修改请求头
                headerMap.forEach((key,  value) -> {
                    // 移除旧的请求头
                    headers.removeHeader(key);
                    // 添加新的请求头
                    headers.addValue(key).setString(value);
                });
            }
        } catch (Exception e) {
            // 处理异常情况
            handleException(e, headerMap);
        }
    }

    /**
     * 解包ServletRequest。
     * @param request 需要解包的ServletRequest。
     * @return 如果request是ServletRequestWrapper的实例，则递归调用unwrapRequest方法，获取被包装的原始请求；否则返回未被包装的请求。
     */
    private static ServletRequest unwrapRequest(ServletRequest request) {
        // 如果请求是ServletRequestWrapper的实例
        if (request instanceof ServletRequestWrapper) {
            // 递归调用unwrapRequest方法，获取被包装的原始请求
            return unwrapRequest(((ServletRequestWrapper) request).getRequest());
        }
        // 返回未被包装的请求
        return request;
    }

    /**
     * 获取CoyoteRequest对象
     * @param request ServletRequest对象
     * @return CoyoteRequest对象，如果获取失败则返回null
     */
    private static Object getCoyoteRequest(ServletRequest request) {
        try {
            // 获取request对象的"request"字段
            Field requestField = request.getClass().getDeclaredField("request");
            // 设置该字段为可访问
            requestField.setAccessible(true);
            // 获取requestField字段的值，即connectorRequest对象
            Object connectorRequest = requestField.get(request);

            // 获取connectorRequest对象的"coyoteRequest"字段
            Field coyoteRequestField = connectorRequest.getClass().getDeclaredField("coyoteRequest");
            // 设置该字段为可访问
            coyoteRequestField.setAccessible(true);
            // 获取并返回coyoteRequestField字段的值，即CoyoteRequest对象
            return coyoteRequestField.get(connectorRequest);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 捕获异常并记录错误日志
            log.error(" 获取CoyoteRequest失败", e);
            // 返回null表示获取失败
            return null;
        }
    }

    /**
     * 获取coyoteRequest对象的"headers"字段，并将其转换为MimeHeaders类型。
     * @param coyoteRequest 需要获取"headers"字段的对象。
     * @return 如果成功获取并转换，返回MimeHeaders类型的对象；否则记录错误日志并返回null。
     */
    private static MimeHeaders getMimeHeaders(Object coyoteRequest) {
        try {
            // 获取coyoteRequest对象的"headers"字段
            Field headersField = coyoteRequest.getClass().getDeclaredField("headers");
            // 设置该字段为可访问
            headersField.setAccessible(true);
            // 返回该字段的值，并将其转换为MimeHeaders类型
            return (MimeHeaders) headersField.get(coyoteRequest);
        } catch (Exception e) {
            // 如果发生异常，记录错误日志并返回null
            log.error(" 获取MimeHeaders失败", e);
            return null;
        }
    }

    // 统一异常处理
    private static void handleException(Exception e, Map<String, String> headerMap) {
        log.error(" 修改请求头失败：{}", e.getMessage(),  e);
        log.debug(" 尝试修改的请求头内容：{}", headerMap.entrySet().stream()
                .map(entry -> entry.getKey()  + "=" + entry.getValue())
                .collect(Collectors.joining(",  ")));
    }
}
