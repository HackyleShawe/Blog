package com.hackyle.blog.admin.module.article.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class CommentUpdateDto {
    @NotNull(message = "评论ID不能为空")
    private Long id;

    ///**
    // * 可以修改这条评论到其他文章
    // */
    //private Long articleId;

    /**
     * 评论者ID
     */
    private Long commentatorId;
    /**
     * 评论者名称
     */
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
    private String content;

    /**
     * 是否发布评论：0-未审核、不发布，1-审核通过、发布
     */
    private Boolean released;

    /**
     * 0-False-未删除, 1-True-已删除
     */
    private Boolean deleted;
}
