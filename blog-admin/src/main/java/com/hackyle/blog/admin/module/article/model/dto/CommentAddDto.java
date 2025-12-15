package com.hackyle.blog.admin.module.article.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentAddDto {
    /**
     * 被评论文章ID
     */
    @NotNull(message = "被评论文章ID不能为空")
    private Long articleId;
    /**
     * 评论者ID
     */
    private Long commentatorId;
    /**
     * 评论者名称
     */
    @NotBlank(message = "评论者名称不能为空")
    private String commentatorName;
    /**
     * 评论人的邮箱
     */
    @Email
    private String commentatorEmail;
    /**
     * 评论者的拓展链接，如个人网站地址
     */
    @URL
    private String commentatorLink;

    /**
     * 评论内容
     */
    @NotBlank
    private String content;
    /**
     * 被回复的评论ID
     */
    private Long replyId;
    /**
     * 被评论者的名称
     */
    private String replyName;
    /**
     * 根级父评论，顶级父评论ID，如果为0表示当前记录没有父评论，也即为顶级评论
     */
    private Long rootId;
    /**
     * 父评论ID，如果为0表示当前记录没有父评论
     */
    private Long pid;
}
