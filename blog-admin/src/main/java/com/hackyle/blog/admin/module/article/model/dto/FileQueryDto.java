package com.hackyle.blog.admin.module.article.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;

@Data
public class FileQueryDto extends PageQueryBase {

    private Long articleId;

    /**
     * 文件地址
     */
    private String fileLink;

    /**
     * 文件连接是否没有文章id
     */
    private Boolean emptyArticle;

    ///**
    // * 起始时间: 年-月-日 时:分:秒
    // */
    //private String startTime;
    ///**
    // * 结束时间: 年-月-日 时:分:秒
    // */
    //private String endTime;
}
