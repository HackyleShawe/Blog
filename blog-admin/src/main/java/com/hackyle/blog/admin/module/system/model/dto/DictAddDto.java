package com.hackyle.blog.admin.module.system.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class DictAddDto {
    /**
     * 字典编码
     */
    @NotBlank(message = "字典编码不能为空")
    private String code;
    /**
     * 字典名称
     */
    private String name;
    /**
     * 状态：0-无效 1-有效
     */
    private Boolean status;

    /**
     * 字典明细
     */
    @Valid
    List<DictAddDetail> details;

    @Data
    public static class DictAddDetail {
        /**
         * 字典主表ID
         */
        //@NotNull(message = "字典主表ID不能为空")
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
}
