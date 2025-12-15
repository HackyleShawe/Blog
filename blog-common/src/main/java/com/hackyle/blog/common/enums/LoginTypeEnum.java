package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录登出类型：登入IN，登出OUT
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {
    IN("IN", "登入"),
    OUT("OUT", "登出");

    private final String code;
    private final String desc;

    public static LoginTypeEnum of(String code) {
        for (LoginTypeEnum e : LoginTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
