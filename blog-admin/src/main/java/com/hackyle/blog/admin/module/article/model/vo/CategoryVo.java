package com.hackyle.blog.admin.module.article.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 分类编码
     */
    private String code;
    /**
     * 分类描述
     */
    private String description;
    /**
     * 分类颜色
     */
    private String color;
    /**
     * 分类的图标URL
     */
    private String iconUrl;
    /**
     * 上一级分类
     */
    private Long pid;

    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private Long updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 分类下的文章数量
     */
    private Integer articleCount;

}
