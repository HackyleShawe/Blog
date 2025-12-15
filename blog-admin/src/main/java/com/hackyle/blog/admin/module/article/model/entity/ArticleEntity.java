package com.hackyle.blog.admin.module.article.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章主体
 */
@Data
@TableName("article")
public class ArticleEntity extends EntityBase implements Serializable {

    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 总结概要
     */
    private String summary;
    /**
     * 文章关键字，直接用于meta标签，SEO
     */
    private String keywords;
    /**
     * 文章的链接路径，文章的URL为：https://domain.com/path
     */
    private String path;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 封面图URL
     */
    private String coverImg;
    /**
     * 是否发布：0-草稿 1-发布
     */
    private Boolean released;
    /**
     * 是否可以评论：0-不可评论 1-可以评论
     */
    private Boolean commented;

}
