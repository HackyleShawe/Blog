package com.hackyle.blog.admin.module.auth.controller;

import com.hackyle.blog.admin.module.auth.model.dto.LoginDto;
import com.hackyle.blog.admin.module.auth.model.vo.CaptchaVo;
import com.hackyle.blog.admin.module.auth.model.vo.LoginInfoVo;
import com.hackyle.blog.admin.module.auth.model.vo.LoginVo;
import com.hackyle.blog.admin.module.auth.service.AuthService;
import com.hackyle.blog.common.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 登录验证、获取用户信息
 */
@RequestMapping("/auth")
@RestController
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginVo> login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) {
        LoginVo loginVo = authService.login(loginDto);

        if(null == loginVo) {
            return ApiResponse.fail("登录失败");
        }

        response.setHeader("Authorization", loginVo.getToken());
        return ApiResponse.ok(loginVo);
    }


    @GetMapping("/getCaptcha")
    public ApiResponse<CaptchaVo> captcha() {
        CaptchaVo captchaVo = authService.captcha();

        if(null == captchaVo) {
            return ApiResponse.fail("获取验证码失败");
        }
        return ApiResponse.ok(captchaVo);
    }

    @GetMapping("/getInfo")
    public ApiResponse<LoginInfoVo> userinfo() {
        LoginInfoVo loginInfoVo = authService.info();
        return ApiResponse.ok(loginInfoVo);
    }

    @DeleteMapping("/logout")
    public ApiResponse<?> logout() {
        authService.logout();
        return ApiResponse.ok();
    }
}
