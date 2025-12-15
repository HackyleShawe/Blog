package com.hackyle.blog.admin.module.article.model.dto;

import lombok.Data;

@Data
public class ArticleAuthorRelationDto {
    private Long articleId;

    private Long authorId;

    /**
     * 笔名
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
}
