package com.hackyle.blog.admin.infrastructure.filter;

import com.hackyle.blog.admin.infrastructure.holder.AuthedContextHolder;
import com.hackyle.blog.admin.module.auth.model.dto.UserDetailsDto;
import com.hackyle.blog.admin.module.auth.service.JwtService;
import com.hackyle.blog.common.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 访问拦截器
 */
@Slf4j
@Component
public class AccessInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    /** 定义tranceId的字段名，必须与xml文件中定义的名字一直 */
    private static final String TRACE_ID = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        //注意：不经过此拦截器的请求，日志中没有traceId
        //向slf4j的MDC上下文容器中放置以一个K-V键值对，key必须与xml文件中定义的名字一直
        MDC.put(TRACE_ID, UUID.randomUUID().toString().replaceAll("-", ""));

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        UserDetailsDto userDetails = jwtService.getUserDetails(request);
        if (userDetails == null) {
            throw new AuthenticationException("登录态已失效，请重新登录");

        } else {
            //刷新token
            String refreshToken = jwtService.refreshToken(request);
            response.setHeader("Authorization", refreshToken);

            //设置用户信息到上下文
            AuthedContextHolder.setUserDetailsDto(userDetails);

            //拦截器放行
            return true;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
