package com.hackyle.blog.admin.infrastructure.redis;

/**
 * 定义本模块下所有的缓存key前缀
 */
public class CacheKey {
    //统一前缀：blog admin
    private static final String UNI_PREFIX = "ba:";


    /**
     * 登录认证
     */
    public static class Auth {
        private static final String PREFIX = UNI_PREFIX + "auth:";

        //每次生成验证码的缓存前缀
        public static final String CAPTCHA = PREFIX + "cha:";
        //登录者用户id
        public static final String LOGIN_UID = PREFIX + "uid:";

    }

    /**
     * 系统管理
     */
    public static class Sys {
        private static final String PREFIX = UNI_PREFIX + "sys:";

        //系统配置
        public static final String CONFIG_HASH_KEY = PREFIX + "config";
        //字典
        public static final String DICT_HASH_KEY = PREFIX + "dict";
    }



}
