package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片格式
 */
@AllArgsConstructor
@Getter
public enum ImageTypeEnum {
    JPEG("jpg", "JPEG 图片格式"),
    JPG("jpeg", "JPEG 图片格式"),
    PNG("png", "PNG 图片格式"),
    GIF("gif", "GIF 图片格式"),
    BMP("bmp", "位图 BMP 格式"),
    TIFF("tiff", "高质量 TIFF 格式"),
    WEBP("webp", "高效压缩 WEBP 格式"),
    HEIF("heif", "HEIF 高效图像格式"),
    HEIC("heic", "HEIC 高效图像格式"),
    SVG("svg", "矢量图 SVG 格式"),
    ICO("ico", "Windows 图标 ICO 格式"),
    RAW("raw", "相机原始图片格式");

    private final String code;
    private final String desc;

    public static ImageTypeEnum ofCode(String code) {
        for (ImageTypeEnum e : ImageTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
