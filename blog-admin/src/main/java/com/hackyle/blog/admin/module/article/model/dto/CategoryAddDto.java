package com.hackyle.blog.admin.module.article.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class CategoryAddDto {

    /**
     * 分类名称
     * NOTBlank：不能为 Null 且 trim() 之后 size > 0，必须有实际字符
     */
    @NotBlank(message = "分类名称不能为空")
    @NotEmpty(message = "分类名称不能为空")
    @Length(min = 1, max = 32, message = "分类名称长度必须在1-32之间")
    private String name;
    /**
     * 分类编码
     */
    @NotBlank(message = "分类名称不能为空")
    @Length(min = 1, max = 16, message = "分类编码长度必须在1-16之间")
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

}
