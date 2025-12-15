package com.hackyle.blog.customer.module.article.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
     * 被回复的评论ID
     */
    private Long pid;
    /**
     * 被评论者的名称
     */
    private String pname;

    /**
     * 是否发布评论：0-未审核、不发布，1-审核通过、发布
     */
    private Boolean released;

    /**
     * 0-False-未删除, 1-True-已删除
     */
    private Boolean deleted;

    private String createTime;

    /**
     * 子评论
     */
    private List<CommentVo> children;
}
