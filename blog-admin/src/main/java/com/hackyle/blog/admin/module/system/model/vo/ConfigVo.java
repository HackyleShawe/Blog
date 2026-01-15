package com.hackyle.blog.admin.module.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 参数配置
 */
@Data
public class ConfigVo implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
     * 参数键名
     */
    private String configKey;
    /**
     * 参数键值
     */
    private String configValue;
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
