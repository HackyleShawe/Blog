package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 删除状态：0-False-未删除, 1-True-已删除
 */
@Getter
@AllArgsConstructor
public enum DeletedEnum {
    TRUE((byte)1, "已删除"),
    FALSE((byte)0, "未删除");


    private final Byte code;
    private final String desc;

    public static DeletedEnum of(Byte code) {
        for (DeletedEnum e : DeletedEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
