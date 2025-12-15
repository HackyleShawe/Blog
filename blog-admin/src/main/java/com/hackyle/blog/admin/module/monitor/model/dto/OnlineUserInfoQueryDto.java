package com.hackyle.blog.admin.module.monitor.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;

@Data
public class OnlineUserInfoQueryDto extends PageQueryBase {

    private Long userId;

    private String username;


}
