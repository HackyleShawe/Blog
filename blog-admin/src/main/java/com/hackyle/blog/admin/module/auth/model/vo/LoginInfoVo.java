package com.hackyle.blog.admin.module.auth.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 此Vo类和LoginVo的区别是什么？
 * LoginInfoVo是登录成功后，再次请求后台获取用户信息，重点在于用户的基本信息、角色信息、权限信息等
 * LoginVo登录成功后返回给前端，重点在于jwt
 */
@Data
public class LoginInfoVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 1-男; 0-女
     */
    private int gender;
    /**
     * 生日
     */
    private Date birthday;
    /**
     * 用户的头像地址
     */
    private String avatar;
    /**
     * 用户的自我描述
     */
    private String description;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 地址
     */
    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 该个用户拥有那些角色
     */
    private List<String> roleCodes;

    private String roleCode;

    /**
     * 该个用户拥有那些菜单
     */
    //private List<MenuVo> menus;

    /**
     * 权限
     */
    private List<String> perms;
}
