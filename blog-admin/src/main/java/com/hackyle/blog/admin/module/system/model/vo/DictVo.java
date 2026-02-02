package com.hackyle.blog.admin.module.system.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class DictVo implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * 字典编码
     */
    private String code;
    /**
     * 字典名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 状态：0-无效 1-有效
     */
    private Boolean status;

    /**
     * 0-False-未删除, 1-True-已删除
     */
    private Boolean deleted;

    /**
     * 字典明细
     */
    List<DictDetailVo> details;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
