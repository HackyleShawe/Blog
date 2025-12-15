package com.hackyle.blog.customer.module.article.model.dto;

import lombok.Data;

@Data
public class AuthorDto {
    private Long articleId;

    private Long authorId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 描述
     */
    private String description;

}
