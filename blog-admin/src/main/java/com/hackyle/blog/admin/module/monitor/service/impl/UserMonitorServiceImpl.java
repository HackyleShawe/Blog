package com.hackyle.blog.admin.module.monitor.service.impl;

import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.monitor.model.dto.OnlineUserInfoQueryDto;
import com.hackyle.blog.admin.module.monitor.model.vo.OnlineUserInfoVo;
import com.hackyle.blog.admin.module.monitor.service.UserMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserMonitorServiceImpl implements UserMonitorService {

    @Override
    public PageInfo<OnlineUserInfoVo> getOnlineUserList(OnlineUserInfoQueryDto queryDto) {
        return null;
    }

    @Override
    public boolean forceLogout(Long userId) {
        return false;
    }
}
