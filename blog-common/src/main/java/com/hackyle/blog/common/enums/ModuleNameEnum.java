package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模块名称枚举
 */
@Getter
@AllArgsConstructor
public enum ModuleNameEnum {
    TEXT("system", "系统管理"),
    IMAGE("article", "文章管理"),
    ;

    private final String code;
    private final String desc;

    public static ModuleNameEnum ofCode(String code) {
        for (ModuleNameEnum e : ModuleNameEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
