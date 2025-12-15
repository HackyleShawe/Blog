package com.hackyle.blog.customer.config;

import com.hackyle.blog.customer.interceptor.TranceIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    /**
     * tranceId拦截器
     */
    @Autowired
    private TranceIdInterceptor tranceIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //traceId访问拦截器
        registry.addInterceptor(tranceIdInterceptor)
            //拦截这些请求走MvcInterceptor中的逻辑
            .addPathPatterns("/**")
            //不需要走MvcInterceptor中的逻辑
            .excludePathPatterns(
                    "/css/**", //排除调静态资源
                    "/img/**",
                    "/js/**",
                    "/plugin/**",
                    "/error"
            );

    }

}
