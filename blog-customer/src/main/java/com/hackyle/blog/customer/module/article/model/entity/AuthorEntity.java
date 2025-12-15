package com.hackyle.blog.customer.module.article.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章作者
 */
@TableName("article_author")
@Data
public class AuthorEntity extends EntityBase implements Serializable {

    private Long id;
    /**
     * 笔名
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 描述
     */
    private String description;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 地址
     */
    private String address;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 1-男; 0-女；2-未知
     */
    private Integer gender;
    /**
     * 状态：0-无效 1-有效
     */
    private Boolean status;


}
