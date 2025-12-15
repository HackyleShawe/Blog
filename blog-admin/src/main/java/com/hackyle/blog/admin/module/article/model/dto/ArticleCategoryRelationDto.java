package com.hackyle.blog.admin.module.article.model.dto;

import lombok.Data;

@Data
public class ArticleCategoryRelationDto {
    private Long articleId;

    private Long categoryId;

    /**
     * 分类名称
     */
    private String name;
    /**
     * 分类编码
     */
    private String code;
}
