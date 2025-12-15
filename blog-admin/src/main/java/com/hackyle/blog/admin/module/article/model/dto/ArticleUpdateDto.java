package com.hackyle.blog.admin.module.article.model.dto;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class ArticleUpdateDto {
    @NotNull(message = "文章id不能为空")
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
    ///**
    // * 文章的链接路径，端生成path=category-code+id混淆串，重复保存、修改时需要携带path
    // */
    //private String path; 文章链接不允许修改
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
    /**
     * 是否删除
     */
    private Boolean deleted;

    /**
     * 作者
     */
    private String authorIds;
    /**
     * 分类，可以为空
     */
    private String categoryIds;

    //************************************************************//
    /**
     * 文章中的图片链接
     */
    @JsonIgnore
    private Set<String> imgUrls;
}
