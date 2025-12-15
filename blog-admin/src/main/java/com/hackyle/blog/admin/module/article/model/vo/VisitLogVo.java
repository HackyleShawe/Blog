package com.hackyle.blog.admin.module.article.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class VisitLogVo {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 用户ID（未登录则为空或0）
     */
    private Long userId;
    /**
     * 访问IP
     */
    private String ip;
    /**
     * IP解析的地理位置（省市区）
     */
    private String ipLocation;
    /**
     * User-Agent
     */
    private String userAgent;
    /**
     * 来源页面URL
     */
    private String refererUrl;
    /**
     * 请求耗时
     */
    private Integer timeUse;
    /**
     * 访问时间
     */
    private Date visitTime;


}
