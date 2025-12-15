package com.hackyle.blog.common.ip;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.hackyle.blog.common.exception.BizException;

public class PconlineIpRegionUtils {
    /**
     * website for query geography address from ip
     */
    public static final String ADDRESS_QUERY_SITE = "http://whois.pconline.com.cn/ipJson.jsp";


    public static PconlineIpRegionDto getIpRegion(String ip) {
        //入参为空，或者是私有IP，直接返回null
        if(StrUtil.isBlank(ip) || IpRegexPatternConstants.UNKNOWN.equalsIgnoreCase(ip)
                || IpRegexPatternConstants.PRIVATE_IP_PATTERN.matcher(ip).matches()) {
            return null;
        }

        try {
            String rspStr = HttpUtil.get(ADDRESS_QUERY_SITE + "?ip=" + ip + "&json=true",
                    CharsetUtil.CHARSET_GBK);
            if(StrUtil.isBlank(rspStr)) {
                return null;
            }

            JSONObject jsonObject = JSONObject.parseObject(rspStr);
            String country = jsonObject.getString("country");
            String province = jsonObject.getString("pro");
            String city = jsonObject.getString("city");
            String region = jsonObject.getString("region");
            String isp = jsonObject.getString("addr");

            return new PconlineIpRegionDto(country, province, city, region, isp);
        } catch (Exception e) {
            throw new BizException("获取IP地理位置信息失败");
        }
    }


    public static void main(String[] args) {
        String rspStr = HttpUtil.get(ADDRESS_QUERY_SITE + "?ip=61.171.197.171&json=true",
                CharsetUtil.CHARSET_GBK);
        System.out.println(rspStr);
    }
}
