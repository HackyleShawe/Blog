package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否禁用：0-No 1-Yes，禁用的用户无法登录
 */
@Getter
@AllArgsConstructor
public enum DisabledEnum {
    YES(1, "已禁用"),
    NO(0, "未禁用");


    private final Integer code;
    private final String desc;

    public static DisabledEnum of(Integer code) {
        for (DisabledEnum e : DisabledEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
