package com.hackyle.blog.admin.module.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 参数配置
 * @TableName sys_config
 */
@Data
@TableName("sys_config")
public class SysConfigEntity extends EntityBase implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
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
     * 参数说明
     */
    private String configDesc;
    /**
     * 状态：0-无效 1-有效
     * @see com.hackyle.blog.common.enums.StatusEnum
     */
    private Boolean status;

}
