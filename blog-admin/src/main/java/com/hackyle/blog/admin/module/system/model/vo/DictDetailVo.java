package com.hackyle.blog.admin.module.system.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 数据字典详情
 */
@Data
public class DictDetailVo  implements Serializable {
    private Long id;
    /**
     * 字典id
     */
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
     * 排序
     */
    private Integer sort;
    /**
     * 状态：0-无效 1-有效
     */
    private Boolean status;

    /**
     * 0-False-未删除, 1-True-已删除
     */
    private Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;


}
