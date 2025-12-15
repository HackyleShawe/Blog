package com.hackyle.blog.admin.module.article.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 文件地址
     */
    private String fileLink;
    /**
     * 文件类型：text文本、image图片、video视频、audio音频、zip压缩包、bin二进制数据
     */
    private String fileType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
