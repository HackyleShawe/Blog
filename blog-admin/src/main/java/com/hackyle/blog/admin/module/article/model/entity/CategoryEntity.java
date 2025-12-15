package com.hackyle.blog.admin.module.article.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;

import java.io.Serializable;

/**
 * 文章分类
 * @TableName article_category
 */
@Data
@TableName("article_category")
public class CategoryEntity extends EntityBase implements Serializable {

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
    /**
     * 状态：0-无效 1-有效
     */
    private Boolean status;

}
