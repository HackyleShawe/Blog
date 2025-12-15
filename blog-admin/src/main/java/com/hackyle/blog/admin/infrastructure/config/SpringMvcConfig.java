package com.hackyle.blog.admin.infrastructure.config;

import com.hackyle.blog.admin.infrastructure.filter.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AccessInterceptor accessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //请求访问拦截器
        registry.addInterceptor(accessInterceptor)
        //拦截这些请求走MvcInterceptor中的逻辑
        .addPathPatterns("/**")
        //不需要走MvcInterceptor中的逻辑
        //只有注册、登录的接口不需要token，其他的接口全部都要
        .excludePathPatterns(
                "/auth/login",
                "/auth/getCaptcha",
                "/error"
        );
    }

}
