package com.hackyle.blog.customer.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 请求拦截器
 */
@Component
public class TranceIdInterceptor implements HandlerInterceptor {
    /** 定义tranceId的字段名，必须与xml文件中定义的名字一直 */
    private static final String TRACE_ID = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //向slf4j的MDC上下文容器中放置以一个K-V键值对，key必须与xml文件中定义的名字一直
        MDC.put(TRACE_ID, UUID.randomUUID().toString().replaceAll("-", ""));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
