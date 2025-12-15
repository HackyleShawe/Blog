package com.hackyle.blog.admin.module.article.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;

import java.io.Serializable;

/**
 * 静态资源文件（图片、视频、音乐、文档等）
 * @TableName article_file
 */
@Data
@TableName("article_file")
public class FileEntity extends EntityBase implements Serializable {

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

}
