package com.hackyle.blog.customer.module.article.model.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Long articleId;

    private Long categoryId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类简称
     */
    private String code;

    /**
     * 分类描述
     */
    private String description;
}
