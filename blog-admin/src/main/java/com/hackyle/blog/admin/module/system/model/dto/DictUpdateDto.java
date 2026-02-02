package com.hackyle.blog.admin.module.system.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DictUpdateDto {

    @NotNull(message = "字典ID不能为空")
    private Long id;
    /**
     * 字典名称
     */
    private String name;

    private Boolean status;

    private Boolean deleted;

    /**
     * 字典明细
     */
    @Valid
    List<DictUpdateDetail> details;

    @Data
    public static class DictUpdateDetail {
        /**
         * 字典主表ID
         */
        //@NotNull(message = "字典主表ID不能为空")
        private Long dictId;
        /**
         * 字典值
         */
        //@NotBlank(message = "字典明细值不能为空")
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
}
