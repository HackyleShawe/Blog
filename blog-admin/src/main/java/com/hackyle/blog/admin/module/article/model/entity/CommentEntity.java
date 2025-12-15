package com.hackyle.blog.admin.module.article.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.hackyle.blog.common.base.EntityBase;
import lombok.Data;

import java.io.Serializable;

/**
 * 评论
 * @TableName article_comment
 */
@Data
@TableName("article_comment")
public class CommentEntity extends EntityBase implements Serializable {

    private Long id;
    /**
     * 被评论文章ID
     */
    private Long articleId;
    /**
     * 评论者ID
     */
    private Long commentatorId;
    /**
     * 评论者名称
     */
    private String commentatorName;
    /**
     * 评论人的邮箱
     */
    private String commentatorEmail;
    /**
     * 评论者的拓展链接，如个人网站地址
     */
    private String commentatorLink;
    /**
     * 评论者的ip地址
     */
    private String commentatorIp;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 根级父评论，顶级父评论ID，如果为0表示当前记录没有父评论，也即为顶级评论
     */
    private Long rootId;
    /**
     * 父评论ID，如果为0表示当前记录没有父评论
     */
    private Long pid;
    /**
     * 父评论名称，如果为0表示当前记录没有父评论
     */
    private String pname;
    /**
     * 是否发布评论：0-未审核、不发布，1-审核通过、发布
     */
    private Boolean released;

}
