package com.hackyle.blog.business.dto;

public class AdminSignInDto {
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;

    public AdminSignInDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
