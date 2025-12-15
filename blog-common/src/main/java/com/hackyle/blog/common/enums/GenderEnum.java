package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderEnum {
    MAN(1, "man", "男"),
    WOMAN(0, "woman", "女"),
    UNKNOWN(2, "unknown", "未知");

    private final Integer code;
    private final String value;
    private final String desc;


    public static GenderEnum ofCode(Integer code) {
        for (GenderEnum e : GenderEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
