package com.hackyle.blog.admin.module.article.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("article_category_relation")
public class ArticleCategoryRelationEntity {
    private Long articleId;

    private Long categoryId;

}
