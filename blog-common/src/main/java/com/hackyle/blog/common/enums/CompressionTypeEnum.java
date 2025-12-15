package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压缩文件类型枚举
 */
@AllArgsConstructor
@Getter
public enum CompressionTypeEnum {
    ZIP("zip", "ZIP压缩文件"),
    GZIP("gz", "GZIP压缩文件"),
    TAR("tar", "TAR归档文件"),
    TAR_GZ("tar.gz", "TAR.GZ压缩文件"),
    RAR("rar", "RAR压缩文件"),
    SEVEN_Z("7z", "7-Zip压缩文件"),
    BZ2("bz2", "BZIP2压缩文件"),
    XZ("xz", "XZ压缩文件"),
    ISO("iso", "ISO镜像文件")
    ;

    private final String code;
    private final String desc;

    public static CompressionTypeEnum ofCode(String code) {
        for (CompressionTypeEnum e : CompressionTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
