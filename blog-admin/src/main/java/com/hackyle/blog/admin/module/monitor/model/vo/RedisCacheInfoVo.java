package com.hackyle.blog.admin.module.monitor.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Properties;

@Data
public class RedisCacheInfoVo {
    private Properties info;
    private Object dbSize;
    private List<CommonStatusVo> commandStats;

    @Data
    public static class CommonStatusVo {
        private String name;
        private String value;
    }
}
