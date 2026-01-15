package com.hackyle.blog.admin.module.system.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;

@Data
public class ConfigQueryDto extends PageQueryBase {

    /**
     * 参数名称
     */
    private String configName;
    /**
     * 参数键名
     */
    private String configKey;
    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 状态：0-无效 1-有效
     * @see com.hackyle.blog.common.enums.StatusEnum
     */
    private Boolean status;

    private Boolean deleted;

}
