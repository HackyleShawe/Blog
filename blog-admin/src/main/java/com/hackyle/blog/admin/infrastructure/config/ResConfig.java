package com.hackyle.blog.admin.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "res")
@Data
public class ResConfig {
    /**
     * 资源文件（图片、视频等）存放路径
     */
    private String storagePath;

    /**
     * 资源主域名
     */
    private String domain;

    /**
     * 图片水印的文本
     */
    private String waterMarkText;


}
