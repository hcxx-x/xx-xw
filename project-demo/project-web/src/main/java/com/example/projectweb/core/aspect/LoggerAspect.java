package com.example.projectweb.core.aspect;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.example.core.context.RequestContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义切面类: 打印 controller 参数和执行情况
 *
 * @Auther: 李恒
 * @Date: 2021/14/01 12:19
 */
@Component
@Aspect
public class LoggerAspect {

    Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    /**
     * 切点为controller
     */
    @Pointcut("execution(public * com.*.*.biz.controller..*.*(..))")
    public void webLog() {
        //只简单定义一个切面
    }


    @Before("webLog()")
    public void before(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String requestId = request.getHeader("requestId");
        if (StrUtil.isBlank(requestId)){
            requestId= IdUtil.fastUUID();
        }
        MDC.put("requestId",requestId);
        RequestContext.setRequestId(requestId);
        logger.info("开始{}请求", request.getRequestURI());
        JSONObject params = JSONUtil.parseObj(request.getParameterMap());
        if (request.getMethod().equalsIgnoreCase("post")) {
            Object[] args = joinPoint.getArgs();
            // 过滤出不能被序列化的参数
            List<Object> allowSeriaArgList = Arrays.stream(args)
                    .filter(p -> !(p instanceof HttpServletResponse ||
                            p instanceof HttpServletRequest ||
                            p instanceof MultipartFile))
                    .collect(Collectors.toList());
            params.put("requestBody", allowSeriaArgList);
        }
        if (params != null) {
            logger.info("请求参数：{}", JSON.toJSONString(params));
        }

    }

    @Around("webLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object retVal = joinPoint.proceed(args);
        stopWatch.stop();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return retVal;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        logger.info("完成{}请求,耗时：{} 秒", request.getRequestURI(), stopWatch.getTotalTimeSeconds());
        clearContext();
        return retVal;
    }

    @AfterThrowing(throwing = "ex", pointcut = "webLog()")
    public void afterThrowing(Throwable ex) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        if (ex instanceof Exception){
            logger.warn("请求{}出现业务异常,异常原因：{}", request.getRequestURI(), ex.getMessage());
        }else{
            logger.error("请求{}出现异常", request.getRequestURI(), ex);
        }
        clearContext();
        //ex.printStackTrace();
    }

    /**
     * 清理上下文信息
     */
    private void clearContext(){
        RequestContext.remove();
        MDC.clear();
    }

}
