package com.hackyle.blog.admin.module.website.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;

import java.io.Serializable;

/**
 * 参数配置
 * @TableName website_feedback
 */
@Data
@TableName("website_feedback")
public class WebsiteFeedbackEntity extends EntityBase implements Serializable {

    private Long id;
    /**
     * 留言者称呼
     */
    private String name;
    /**
     * 留言者邮箱
     */
    private String email;
    /**
     * 留言者电话
     */
    private String phone;
    /**
     * 留言者的链接（个人主页、博客页）
     */
    private String link;
    /**
     * 访问IP
     */
    private String ip;
    /**
     * IP解析的地理位置（国省市区）
     */
    private String ipLocation;
    /**
     * User-Agent
     */
    private String userAgent;
    /**
     * 留言内容
     */
    private String content;

    /**
     * 状态：0-无效 1-有效
     * @see com.hackyle.blog.common.enums.StatusEnum
     */
    private Boolean status;
}
