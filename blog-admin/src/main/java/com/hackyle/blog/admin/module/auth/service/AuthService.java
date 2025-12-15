package com.hackyle.blog.admin.module.auth.service;

import com.hackyle.blog.admin.module.auth.model.dto.LoginDto;
import com.hackyle.blog.admin.module.auth.model.vo.CaptchaVo;
import com.hackyle.blog.admin.module.auth.model.vo.LoginInfoVo;
import com.hackyle.blog.admin.module.auth.model.vo.LoginVo;


public interface AuthService {
    LoginVo login(LoginDto loginDto);

    CaptchaVo captcha();

    LoginInfoVo info();

    void logout();

}
