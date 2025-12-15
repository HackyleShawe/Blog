package com.hackyle.blog.admin.module.auth.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDto {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    //@NotBlank(message = "验证码不能为空") 如果关闭验证码，则是非必传
    private String code;

    /**
     * UUID
     */
    //@NotBlank(message = "UUID不能为空") 如果关闭验证码，则是非必传
    private String uuid;

}
