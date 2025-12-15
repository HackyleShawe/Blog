package com.hackyle.blog.admin.module.monitor.service;

import com.hackyle.blog.admin.module.monitor.model.vo.RedisCacheInfoVo;
import com.hackyle.blog.admin.module.monitor.model.vo.ServerInfoVo;

public interface SystemMonitorService {
    RedisCacheInfoVo getRedisCacheInfo();

    ServerInfoVo getServerInfo();

}
