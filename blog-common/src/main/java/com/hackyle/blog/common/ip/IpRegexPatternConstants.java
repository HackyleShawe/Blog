package com.hackyle.blog.common.ip;

import java.util.regex.Pattern;

/**
 * IP相关的正则表达式常量
 */
public class IpRegexPatternConstants {
    /**
     * 1. IPv4 私网范围
     * 10.0.0.0 ~ 10.255.255.255
     * 172.16.0.0 ~ 172.31.255.255
     * 192.168.0.0 ~ 192.168.255.255
     * 127.0.0.1（回环）
     *
     * 2. IPv6 私网范围
     * fc00::/7（即 fc00:: 到 fdff:ffff:ffff:ffff:ffff:ffff:ffff:ffff，本地唯一地址 ULA）
     * ::1（回环地址）
     * fe80::/10（链路本地地址，通常也算局域网地址）
     */
    public static final Pattern PRIVATE_IP_PATTERN = Pattern.compile(
            "^(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"
                    + "|172\\.(1[6-9]|2\\d|3[0-1])\\.\\d{1,3}\\.\\d{1,3}"
                    + "|192\\.168\\.\\d{1,3}\\.\\d{1,3}"
                    + "|127\\.0\\.0\\.1"
                    + "|::1"
                    + "|fc[0-9a-fA-F]{2}:.*"
                    + "|fd[0-9a-fA-F]{2}:.*"
                    + "|fe8[0-9a-fA-F]:.*)$"
    );

    public static final String UNKNOWN = "unknown";

}
