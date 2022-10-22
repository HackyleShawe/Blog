package com.hackyle.blog.business.common.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 从YML配置文件中读取配置数据
 * 方案1：@ConfigurationProperties(prefix = "minio")，指定属性及其getter/setter
 * 方案2：直接在属性上加注解：@Value("minio.endpoint")
 */
@Configuration
public class MinioConfig {
    /** 外网MinIO URL */
    @Value("${minio.endpoint}")
    private String endpoint;

    /** 内网MinIO URL */
    @Value("${minio.endpointIn}")
    private String endpointIn;

    /** MinIO用户名 */
    @Value("${minio.accessKey}")
    private String accessKey;

    /** MinIO密码 */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 把MinioClient交给Spring管理
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(endpoint).
                credentials(accessKey, secretKey).build();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpointIn() {
        return endpointIn;
    }

    public void setEndpointIn(String endpointIn) {
        this.endpointIn = endpointIn;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
