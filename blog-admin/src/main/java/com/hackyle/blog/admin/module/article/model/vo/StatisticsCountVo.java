package com.hackyle.blog.admin.module.article.model.vo;

import lombok.Data;

@Data
public class StatisticsCountVo {
    private int articleCount;

    private int authorCount;

    private int categoryCount;

    private int commentCount;

}
