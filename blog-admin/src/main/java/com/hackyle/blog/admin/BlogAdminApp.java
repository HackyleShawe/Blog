package com.hackyle.blog.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BlogAdminApp extends SpringBootServletInitializer {
    /**
     * main() 仍然可用，用于本地开发运行
     * 当放进外部 Tomcat 时，会调用 configure()
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BlogAdminApp.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BlogAdminApp.class, args);
    }
}


