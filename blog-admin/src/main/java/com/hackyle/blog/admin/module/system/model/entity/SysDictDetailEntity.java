package com.hackyle.blog.admin.module.system.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据字典详情
 * @TableName sys_dict_detail
 */
@Data
@TableName("sys_dict_detail")
public class SysDictDetailEntity extends EntityBase implements Serializable {
    @TableId(type = IdType.AUTO)
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


}
