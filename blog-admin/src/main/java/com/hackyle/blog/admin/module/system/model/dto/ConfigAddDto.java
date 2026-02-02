package com.hackyle.blog.admin.module.system.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConfigAddDto {

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数描述
     */
    private String configDesc;
    /**
     * 参数键名
     */
    @NotBlank
    private String configKey;
    /**
     * 参数键值
     */
    @NotBlank
    private String configValue;

    private Boolean status;

}
