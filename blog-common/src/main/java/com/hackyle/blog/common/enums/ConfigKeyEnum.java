package com.hackyle.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigKeyEnum {
    CAPTCHA("auth.captcha", "登录验证码开关", "0-关闭，1-开启"),
    INIT_PW("sys.initpw", "初始化新用户默认密码", ""),
    ;

    private final String key;
    private final String name;
    private final String desc;

    public static ConfigKeyEnum ofKey(String key) {
        for (ConfigKeyEnum e : ConfigKeyEnum.values()) {
            if (e.key.equals(key)) {
                return e;
            }
        }
        return null;
    }

}
