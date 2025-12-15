package com.hackyle.blog.admin.module.auth.model.vo;

import lombok.Data;

@Data
public class CaptchaVo {

    /**
     *  验证码UUID
     */
    private String uuid;

    /**
     * base64 验证码
     */
    private String code;

}
