package com.hackyle.blog.admin.module.article.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;

@Data
public class AuthorQueryDto extends PageQueryBase {
    /**
     * 笔名
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 用户邮箱
     */
    @Email
    private String email;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 1-男; 0-女；2-未知
     */
    @Range(min = 0, max = 2, message = "性别只能为0-2")
    private Integer gender;
    /**
     * 状态：0-无效 1-有效
     */
    @Range(min = 0, max = 1, message = "状态只能为0-1")
    private Boolean status;

}
