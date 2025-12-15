package com.hackyle.blog.admin.module.monitor.service;

import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.monitor.model.dto.OnlineUserInfoQueryDto;
import com.hackyle.blog.admin.module.monitor.model.vo.OnlineUserInfoVo;

public interface UserMonitorService {
    PageInfo<OnlineUserInfoVo> getOnlineUserList(OnlineUserInfoQueryDto queryDto);

    boolean forceLogout(Long userId);

}
