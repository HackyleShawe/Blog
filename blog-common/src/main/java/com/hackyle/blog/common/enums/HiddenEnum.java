package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否显示：0-No 1-Yes
 */
@Getter
@AllArgsConstructor
public enum HiddenEnum {
    YES(1, "是"),
    NO(0, "否");

    private final Integer code;
    private final String desc;

    public static HiddenEnum of(Integer code) {
        for (HiddenEnum e : HiddenEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
