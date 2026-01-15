package com.hackyle.blog.admin.module.system.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DictDetailAddDto {
    /**
     * 字典主表ID
     */
    @NotNull(message = "字典主表ID不能为空")
    private Long dictId;

    /**
     * 字典值
     */
    @NotBlank(message = "字典明细值不能为空")
    private String value;
    /**
     * 字典说明
     */
    private String label;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态：0-无效 1-有效
     */
    private Boolean status;

}
