package com.hackyle.blog.admin.module.monitor.controller;

import com.hackyle.blog.admin.module.monitor.model.vo.RedisCacheInfoVo;
import com.hackyle.blog.admin.module.monitor.model.vo.ServerInfoVo;
import com.hackyle.blog.admin.module.monitor.service.SystemMonitorService;
import com.hackyle.blog.common.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统监控
 */
@RestController
@Slf4j
@RequestMapping("/monitor/system")
public class SystemMonitorController {
    @Autowired
    private SystemMonitorService systemMonitorService;


    @GetMapping("/redisCacheInfo")
    public ApiResponse<RedisCacheInfoVo> getRedisCacheInfo() {
        RedisCacheInfoVo redisCacheInfo = systemMonitorService.getRedisCacheInfo();
        return ApiResponse.ok(redisCacheInfo);
    }


    @GetMapping("/serverInfo")
    public ApiResponse<ServerInfoVo> getServerInfo() {
        ServerInfoVo serverInfo = systemMonitorService.getServerInfo();
        return ApiResponse.ok(serverInfo);
    }


}
