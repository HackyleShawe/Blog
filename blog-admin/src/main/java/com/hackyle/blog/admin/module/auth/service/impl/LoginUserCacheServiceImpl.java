package com.hackyle.blog.admin.module.auth.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hackyle.blog.admin.infrastructure.redis.CacheKey;
import com.hackyle.blog.admin.module.auth.model.dto.UserDetailsDto;
import com.hackyle.blog.admin.module.auth.service.LoginUserCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * 登录用户信息的缓存
 *
 * Key：统一前缀 + 用户ID
 * Value：UserDetailsDto implements UserDetails
 * expire: 取配置文件中的值
 */
@Service
@Slf4j
public class LoginUserCacheServiceImpl implements LoginUserCacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 令牌有效期
    @Value("${auth.token.expireMinutes}")
    private int expireMinutes;

    @Override
    public void putCache(UserDetailsDto userDetails) {
        String key = CacheKey.Auth.LOGIN_UID + userDetails.getUserId();
        redisTemplate.opsForValue().set(key, userDetails, expireMinutes, TimeUnit.MINUTES);
    }

    @Override
    public void refreshCache(UserDetailsDto userDetailsDto) {
        String key = CacheKey.Auth.LOGIN_UID + userDetailsDto.getUserId();

        Long expire = redisTemplate.getExpire(key, TimeUnit.MINUTES);
        if(expire <= 20) { //登录态有效期小于5分钟，刷新缓存
            redisTemplate.delete(key);
            redisTemplate.opsForValue().set(key, userDetailsDto, expireMinutes, TimeUnit.MINUTES);
        }
    }

    @Override
    public UserDetailsDto getCache(Long userId) {
        String key = CacheKey.Auth.LOGIN_UID + userId;

        Object object = redisTemplate.opsForValue().get(key);
        if (object == null) {
            return null;
        }

        return JSONObject.parseObject(JSON.toJSONString(object), UserDetailsDto.class);
    }

    @Override
    public boolean removeCache(Long userId) {
        String key = CacheKey.Auth.LOGIN_UID + userId;
        return redisTemplate.delete(key);
    }

    @Override
    public List<UserDetailsDto> getAll() {
        String keyPattern = CacheKey.Auth.LOGIN_UID + "*";
        Set<String> keys = redisTemplate.keys(keyPattern);

        List<UserDetailsDto> res = new ArrayList<>();
        for (String key : keys) {
            Object object = redisTemplate.opsForValue().get(key);
            if (object == null) {
                continue;
            }

            UserDetailsDto userDetailsDto = JSONObject.parseObject(JSON.toJSONString(object), UserDetailsDto.class);
            res.add(userDetailsDto);
        }

        return res;
    }
}
