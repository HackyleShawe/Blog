package com.hackyle.blog.customer.module.article.model.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CommentAddDto {
    /**
     * 被评论文章ID
     */
    @NonNull
    private Long articleId;
    /**
     * 评论者ID
     */
    private Long commentatorId;
    /**
     * 评论者名称
     */
    @NotBlank
    private String commentatorName;
    /**
     * 评论人的邮箱
     */
    @NotBlank
    @Email
    private String commentatorEmail;
    /**
     * 评论者的拓展链接，如个人网站地址
     */
    private String commentatorLink;
    /**
     * 评论者的ip地址
     */
    private String commentatorIp;
    /**
     * 评论内容
     */
    @NotBlank
    private String content;

    /**
     * 根级父评论，顶级父评论ID，如果为0表示当前记录没有父评论，也即为顶级评论
     */
    private Long rootId;
    /**
     * 父评论ID，如果为0表示当前记录没有父评论
     * 被回复的评论ID
     */
    private Long pid;

    /**
     * 被评论者的名称
     * 被回复者是谁
     */
    private String pname;
}
