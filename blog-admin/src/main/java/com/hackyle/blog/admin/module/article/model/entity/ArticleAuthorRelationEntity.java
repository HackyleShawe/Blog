package com.hackyle.blog.admin.module.article.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("article_author_relation")
public class ArticleAuthorRelationEntity {
    private Long articleId;

    private Long authorId;

}
