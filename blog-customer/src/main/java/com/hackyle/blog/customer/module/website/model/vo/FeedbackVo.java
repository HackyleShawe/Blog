package com.hackyle.blog.customer.module.website.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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

    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Long updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

}
