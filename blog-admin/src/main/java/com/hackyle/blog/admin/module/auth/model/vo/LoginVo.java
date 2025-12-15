package com.hackyle.blog.admin.module.auth.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 登录成功后的返回体
 */
@Data
public class LoginVo {
    /**
     * jwt
     */
    private String token;

    /**
     * 用户ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 登录用户名
     */
    private String username;

}
