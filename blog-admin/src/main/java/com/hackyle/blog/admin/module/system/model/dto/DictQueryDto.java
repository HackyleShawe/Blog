package com.hackyle.blog.admin.module.system.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;

@Data
public class DictQueryDto extends PageQueryBase {
    /**
     * 字典编码
     */
    private String code;
    /**
     * 字典名称
     */
    private String name;

    /**
     * 状态：0-无效 1-有效
     * @see com.hackyle.blog.common.enums.StatusEnum
     */
    private Boolean status;

    /**
     * 0-False-未删除, 1-True-已删除
     */
    private Boolean deleted;

}
