package com.hackyle.blog.admin.module.system.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DictDetailQueryDto extends PageQueryBase {
    /**
     * 字典id
     */
    @NotNull
    private Long dictId;
    /**
     * 字典值
     */
    private String value;
    /**
     * 字典说明
     */
    private String label;
    /**
     * 状态：0-无效 1-有效
     */
    private Boolean status;

    private Boolean deleted;

}
