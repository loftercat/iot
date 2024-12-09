package com.fujica.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.fujica.aspect.http.JsonRequestWrapper;
import com.fujica.aspect.http.RequestHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 日志
 *
 * @author 杨伟
 * @date 2024年04月29日 14:51
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private static final String REQUEST_BEGIN_TIME = "request_begin_time";
    private static final String TRACE_UUID = "traceId";

    @Pointcut("execution(* com.fujica.controller..*.*(..))")
    public void controllerLog() {

    }

    /**
     * 处理请求前执行
     */
    @Before("controllerLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.error("记录请求日志异常，获取ServletRequestAttributes为空");
            return;
        }

        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        //记录请求开始时间
        MDC.put(REQUEST_BEGIN_TIME, Convert.toStr(System.currentTimeMillis()));

        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        String method = request.getMethod();

        //获取请求参数
        String bodyString;
        if (request instanceof JsonRequestWrapper) {
            bodyString = RequestHelper.getBody(request);
        } else {
            bodyString = JSONUtil.toJsonStr(request.getParameterMap());
        }

        log.info("接收请求,请求URL:{},请求方式:{},content-type:{},请求参数:{}", request.getRequestURL(), method, contentType, bodyString);
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "controllerLog()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        //记录本次请求耗时
        String beginTime = MDC.get(REQUEST_BEGIN_TIME);
        long beginTimeLong = Convert.toLong(beginTime);
        long duration = System.currentTimeMillis() - beginTimeLong;

        log.info("响应请求,响应参数:{},耗时:{}毫秒", jsonResult, duration);

        //清除MDC
        MDC.clear();
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "controllerLog()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        log.info("服务端发生异常", e);

        //清除MDC
        MDC.clear();
    }
}
