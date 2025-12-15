package com.hackyle.blog.admin.module.article.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryUpdateDto {
    @NotNull
    private Long id;

    /**
     * 分类名称
     * NOTBlank：不能为 Null 且 trim() 之后 size > 0，必须有实际字符
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;
    /*
     * 分类编码可以被更新，文章链接中的path取决于当时分类的code
     */
    @NotBlank(message = "分类名称不能为空")
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

}
