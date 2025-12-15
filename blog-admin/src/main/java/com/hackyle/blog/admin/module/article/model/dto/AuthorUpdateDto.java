package com.hackyle.blog.admin.module.article.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class AuthorUpdateDto {
    @NotNull
    private Long id;

    /**
     * 笔名
     */
    @NotBlank(message = "昵称不能为空")
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
    @Email(message = "邮箱格式不正确")
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    /**
     * 1-男; 0-女；2-未知
     */
    @Range(min = 0, max = 2, message = "性别只能为0-2")
    private Integer gender;
}
