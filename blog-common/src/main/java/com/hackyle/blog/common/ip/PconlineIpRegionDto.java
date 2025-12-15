package com.hackyle.blog.common.ip;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PconlineIpRegionDto {
    /**
     * 国
      */
    private String country;
    /**
     * 区域
     */
    private String region;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 运营商
     */
    private String isp;
}
