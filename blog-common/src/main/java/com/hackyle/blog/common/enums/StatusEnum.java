package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否有效：0-No 1-Yes
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {
    YES(1, "有效"),
    NO(0, "无效");


    private final Integer code;
    private final String desc;

    public static StatusEnum of(Integer code) {
        for (StatusEnum e : StatusEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
