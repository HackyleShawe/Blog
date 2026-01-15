package com.hackyle.blog.admin.module.system.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ConfigUpdateDto {

    @NotNull
    private Long id;

    /**
     * 参数名称
     */
    private String configName;
    /**
     * 参数描述
     */
    private String configDesc;
    /**
     * 参数键值
     */
    private String configValue;

    private Boolean status;

    private Boolean deleted;
}
