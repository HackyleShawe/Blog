package com.hackyle.blog.common.base;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EntityBase {

    /**
     * 0-False-未删除, 1-True-已删除
     */
    private Boolean deleted;

    private Long createBy;

    private LocalDateTime createTime;

    private Long updateBy;

    private LocalDateTime updateTime;

}
