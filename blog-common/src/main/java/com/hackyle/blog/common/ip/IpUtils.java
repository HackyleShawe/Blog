package com.hackyle.blog.common.ip;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.NetUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取IP
 */
public class IpUtils {

    /**
     * 获取Web客户端真实IP
     * @return 如果没有公网IP，则返回内网IP
     */
    public static String getClientIP() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {
            return IpRegexPatternConstants.UNKNOWN;
        }
        HttpServletRequest request = attributes.getRequest();

        //优先解析X-Forwarded-For
        String ip = parseXFF(request);
        if(StringUtils.isNotBlank(ip)) {
            return ip;
        }

        //解析其他代理相关头字段
        ip = parseExtendHeader(request);
        if(StringUtils.isNotBlank(ip)) {
            return ip;
        }

        //最后降级使用getRemoteAddr
        ip = request.getRemoteAddr();

        return IpRegexPatternConstants.LOCALHOST_IPV6.equals(ip) ? IpRegexPatternConstants.LOCALHOST_IP : ip;
    }

    /**
     * 从 X-Forwarded-For 中从右往左优先返回第一个有效公网IP，无公网IP则返回第一个有效IP
     */
    private static String parseXFF(HttpServletRequest request) {
        if(request == null) {
            return null;
        }
        String xff =  request.getHeader("X-Forwarded-For");
        if(StringUtils.isBlank(xff)) {
            return null;
        }

        String[] ips = xff.trim().split(",");
        //从后往前找第一个有效公网IP
        for (int i = ips.length - 1; i >= 0; i--) {
            String ip = ips[i].trim();
            //IP格式合法 + 非内网IP = 有效公网IP
            if((Validator.isIpv4(ip) || Validator.isIpv6(ip)) && !NetUtil.isInnerIP(ip)) {
                return ip;
            }
        }
        //无公网IP时，返回第一个格式合法的IP（可能是内网IP）
        for (String ip : ips) {
            ip = ip.trim();
            if (Validator.isIpv4(ip) || Validator.isIpv6(ip)) {
                return ip;
            }
        }

        return null;
    }

    private static String parseExtendHeader(HttpServletRequest request) {
        if(request == null) {
            return null;
        }

        String[] headers = {
                "X-Real-IP", //Nginx
                "Proxy-Client-IP", //Apache
                "WL-Proxy-Client-IP", //WebLogic
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };
        // 遍历头字段，找到第一个有效IP
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (Validator.isIpv4(ip) || Validator.isIpv6(ip)) {
                return ip;
            }
        }

        return null;
    }


}
