package com.hackyle.blog.common.constant;

/**
 * 缓存Key名称定义
 */
public class CacheKeyConstants {
    //统一前缀：blog admin
    private static final String PREFIX = "blog:";

    /**
     * 系统管理
     */
    public static class System {
        //登录者用户id
        public static final String LOGIN_UID = PREFIX + "auth:uid:";

        //系统配置
        public static final String CONFIG_KEY = PREFIX + "sys:config:";
        //字典
        public static final String DICT_KEY = PREFIX + "sys:dict:";


    }


}
