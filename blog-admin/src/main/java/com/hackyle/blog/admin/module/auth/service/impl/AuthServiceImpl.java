package com.hackyle.blog.admin.module.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.hackyle.blog.admin.infrastructure.holder.AuthedContextHolder;
import com.hackyle.blog.admin.module.auth.model.dto.LoginDto;
import com.hackyle.blog.admin.module.auth.model.dto.UserDetailsDto;
import com.hackyle.blog.admin.module.auth.model.vo.CaptchaVo;
import com.hackyle.blog.admin.module.auth.model.vo.LoginInfoVo;
import com.hackyle.blog.admin.module.auth.model.vo.LoginVo;
import com.hackyle.blog.admin.module.auth.service.AuthService;
import com.hackyle.blog.admin.module.auth.service.JwtService;
import com.hackyle.blog.admin.module.auth.service.LoginUserCacheService;
import com.hackyle.blog.common.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LoginUserCacheService loginUserCacheService;


    @Override
    public LoginVo login(LoginDto loginDto) {
        String cacheCode = (String) redisTemplate.opsForValue().get(loginDto.getUuid());
        redisTemplate.delete(loginDto.getUuid()); // 清除验证码
        if (StringUtils.isBlank(cacheCode)) {
            throw new IllegalArgumentException("验证码不存在或已过期");
        }
        if (!cacheCode.equalsIgnoreCase(loginDto.getCode())) {
            throw new IllegalArgumentException("验证码错误");
        }
        if (!StrUtil.equals(loginDto.getUsername(), username) || !StrUtil.equals(loginDto.getPassword(), password)) {
            throw new AuthenticationException("用户名或密码错误");
        }

        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setUserId(1000L);
        userDetailsDto.setUsername(loginDto.getUsername());
        String jwt = jwtService.createJWT(userDetailsDto);

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(jwt);
        loginVo.setId(1000L);
        loginVo.setUsername(loginDto.getUsername());

        loginUserCacheService.putCache(userDetailsDto);

        AuthedContextHolder.setUserDetailsDto(userDetailsDto);

        return loginVo;
    }


    @Override
    public CaptchaVo captcha() {
        CaptchaVo captchaVo = new CaptchaVo();

        String kaptchaText = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(kaptchaText);

        String base64Code = "";
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            base64Code = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("生成验证码失败: ", e);
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error("生成验证码失败: ", e);
                }
            }
        }

        String uuid = UUID.randomUUID().toString();
        captchaVo.setUuid(uuid);
        captchaVo.setCode("data:image/png;base64," + base64Code);
        redisTemplate.opsForValue().set(uuid, kaptchaText, 60L, TimeUnit.SECONDS);

        return captchaVo;
    }

    @Override
    public LoginInfoVo info() {
        LoginInfoVo loginInfoVo = new LoginInfoVo();
        loginInfoVo.setId(1000L);
        loginInfoVo.setUsername(username);
        loginInfoVo.setNickName(username);
        loginInfoVo.setRealName(username);
        loginInfoVo.setGender(1);

        loginInfoVo.setEmail(username);
        loginInfoVo.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");

        loginInfoVo.setPerms(List.of("*:*:*"));

        return loginInfoVo;
    }

    @Override
    public void logout() {
        UserDetailsDto userDetailsDto = AuthedContextHolder.getUserDetailsDto();

        loginUserCacheService.refreshCache(userDetailsDto);
    }

}
