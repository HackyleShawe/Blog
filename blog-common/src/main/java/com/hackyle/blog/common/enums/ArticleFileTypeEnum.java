package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件类型：text文本、image图片、video视频、audio音频、zip压缩包、bin二进制数据
 */
@AllArgsConstructor
@Getter
public enum ArticleFileTypeEnum {
    TEXT("text", "文本"),
    IMAGE("image", "图片"),
    VIDEO("video", "视频"),
    AUDIO("audio", "音频"),
    ZIP("zip", "压缩包"),
    BIN("bin", "二进制数据"),
    ;

    private final String code;
    private final String desc;

    public static ArticleFileTypeEnum ofCode(String code) {
        for (ArticleFileTypeEnum e : ArticleFileTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
