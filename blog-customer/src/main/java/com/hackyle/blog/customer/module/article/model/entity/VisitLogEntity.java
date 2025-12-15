package com.hackyle.blog.customer.module.article.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 文章访问日志表
 * @TableName article_visit_log
 */
@Data
@TableName("article_visit_log")
public class VisitLogEntity implements Serializable {

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
