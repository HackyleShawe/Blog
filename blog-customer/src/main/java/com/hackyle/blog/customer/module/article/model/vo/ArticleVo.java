package com.hackyle.blog.customer.module.article.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArticleVo {

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
     * 文章分类
     */
    String categoryNames;
    /**
     * 文章作者
     */
    String authorNames;
    /**
     * 文章的完整URL
     */
    private String url;
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

    private String createTime;

    private String updateTime;

    /**
     * 评论
     */
    private List<CommentVo> commentVos;

}
