package com.hackyle.blog.admin.module.system.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 数据字典
 * @TableName sys_dict
 */
@Data
@TableName("sys_dict")
public class SysDictEntity extends EntityBase implements Serializable {

    @TableId(type = IdType.AUTO)
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
     * 排除该个字段，不参与SQL拼接
     */
    @TableField(exist = false)
    private List<SysDictDetailEntity> details;


}
