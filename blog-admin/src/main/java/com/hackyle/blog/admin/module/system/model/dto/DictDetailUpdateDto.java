package com.hackyle.blog.admin.module.system.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DictDetailUpdateDto {
    @NotNull
    private Long id;

    /**
     * 字典值
     */
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

    private Boolean deleted;
}
