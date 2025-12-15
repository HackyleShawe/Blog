package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 1:目录Directory；2：菜单menu；3：按钮button
 */
@AllArgsConstructor
@Getter
public enum MenuTypeEnum {
    DIR("DIR", "目录"),
    MENU("MENU", "菜单"),
    BUTTON("BUTTON", "按钮"),
    ;

    private final String code;
    private final String desc;

    public static MenuTypeEnum ofCode(String code) {
        for (MenuTypeEnum e : MenuTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
