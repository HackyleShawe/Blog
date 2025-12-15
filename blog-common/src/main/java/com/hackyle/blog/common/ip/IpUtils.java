package com.hackyle.blog.common.ip;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取IP
 */
public class IpUtils {
    /**
     * X-Forwarded-For:简称XFF头，它代表客户端，也就是HTTP的请求端真实的IP
     * Squid服务代理:只有在通过了HTTP代理或者负载均衡服务器时才会添加该项
     * 标准格式如下：X-Forwarded-For: client_ip, proxy1_ip, proxy2_ip
     * 此头是可构造的，因此某些应用中应该对获取到的ip进行验证
     * 在多级代理网络中，直接用getHeader("x-forwarded-for")可能获取到的是unknown信息，此时需要获取代理代理服务器重新包装的HTTP头信息
     *
     * Proxy-Client-IP:Apache 服务代理
     * X-Real-IP：Nginx服务代理
     * WL-Proxy-Client-IP：WebLogic 服务代理
     */
    private static final String[] HEADERS = {
            "X-Real-IP",
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    /**
     * 根据 ServletRequest 获取公网 IP
     * @return 如果有多个，则使用逗号分隔；如果没有则返回unknown
     */
    public static String getPublicIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {
            return IpRegexPatternConstants.UNKNOWN;
        }
        HttpServletRequest request = attributes.getRequest();

        //公网IP
        List<String> publicIps = new ArrayList<>();

        //从请求头中获取IP地址
        for (String header : HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !IpRegexPatternConstants.UNKNOWN.equalsIgnoreCase(ip)) {
                if(!IpRegexPatternConstants.PRIVATE_IP_PATTERN.matcher(ip).matches()) {
                    publicIps.add(ip);
                }
            }
        }
        //额外获取请求中获取的IP
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null && !remoteAddr.isEmpty() && !IpRegexPatternConstants.UNKNOWN.equalsIgnoreCase(remoteAddr)) {
            if(!IpRegexPatternConstants.PRIVATE_IP_PATTERN.matcher(remoteAddr).matches()) {
                publicIps.add(remoteAddr);
            }
        }

        //如果没有获取到公网IP，则返回内网IP
        if(!publicIps.isEmpty()) {
            return String.join(",", publicIps);
        } else {
            return IpRegexPatternConstants.UNKNOWN;
        }
    }

    /**
     * 根据 ServletRequest 获取IP，如果没有公网IP，则返回内网IP
     * @return 如果有多个，则使用逗号分隔；如果没有则返回unknown
     */
    public static String getIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {
            return IpRegexPatternConstants.UNKNOWN;
        }
        HttpServletRequest request = attributes.getRequest();

        //公网IP
        List<String> publicIps = new ArrayList<>();
        //私网IP
        List<String> privateIps = new ArrayList<>();

        //从请求头中获取IP地址
        for (String header : HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !IpRegexPatternConstants.UNKNOWN.equalsIgnoreCase(ip)) {
                if(!IpRegexPatternConstants.PRIVATE_IP_PATTERN.matcher(ip).matches()) {
                    publicIps.add(ip);
                } else {
                    privateIps.add(ip);
                }
            }
        }
        //额外获取请求中获取的IP
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null && !remoteAddr.isEmpty() && !IpRegexPatternConstants.UNKNOWN.equalsIgnoreCase(remoteAddr)) {
            if(!IpRegexPatternConstants.PRIVATE_IP_PATTERN.matcher(remoteAddr).matches()) {
                publicIps.add(remoteAddr);
            } else {
                privateIps.add(remoteAddr);
            }
        }

        //如果没有获取到公网IP，则返回内网IP
        if(!publicIps.isEmpty()) {
            return String.join(",", publicIps);
        }
        if(!privateIps.isEmpty()) {
            return String.join(",", privateIps);
        }

        return IpRegexPatternConstants.UNKNOWN;
    }

}
