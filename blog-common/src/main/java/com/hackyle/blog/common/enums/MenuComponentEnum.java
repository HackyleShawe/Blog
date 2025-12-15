package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单组件类型
 */
@AllArgsConstructor
@Getter
public enum MenuComponentEnum  {
    LAYOUT(1,"Layout"),
    PARENT_VIEW(2,"ParentView"),
    INNER_LINK(3,"InnerLink");

    private final int code;
    private final String desc;

}
