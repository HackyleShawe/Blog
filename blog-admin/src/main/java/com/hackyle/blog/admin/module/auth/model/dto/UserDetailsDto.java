package com.hackyle.blog.admin.module.auth.model.dto;

import lombok.Data;

@Data
public class UserDetailsDto {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户name
     */
    private String username;

}
