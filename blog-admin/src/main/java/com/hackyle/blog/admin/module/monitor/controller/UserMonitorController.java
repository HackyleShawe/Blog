package com.hackyle.blog.admin.module.monitor.controller;

import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.monitor.model.dto.OnlineUserInfoQueryDto;
import com.hackyle.blog.admin.module.monitor.model.vo.OnlineUserInfoVo;
import com.hackyle.blog.admin.module.monitor.service.UserMonitorService;
import com.hackyle.blog.common.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户监控
 */
@RestController
@RequestMapping("/monitor/user")
@Slf4j
public class UserMonitorController {
    @Autowired
    private UserMonitorService userMonitorService;

    /**
     * 获取在线用户列表
     */
    @GetMapping("/onlineUser/list")
    public ApiResponse<PageInfo<OnlineUserInfoVo>> list(OnlineUserInfoQueryDto queryDto) {
        log.info("获取在线用户列表：queryDto={}", queryDto);
        PageInfo<OnlineUserInfoVo> onlineUserList = userMonitorService.getOnlineUserList(queryDto);
        return ApiResponse.ok(onlineUserList);
    }

    /**
     * 强退用户
     */
    @DeleteMapping("/onlineUser/{userId}")
    public ApiResponse<?> forceLogout(@PathVariable Long userId) {
        log.info("强退用户：userId={}", userId);
        if (userId == null || userId <= 0) {
            return ApiResponse.fail("用户ID不能为空");
        }

        boolean result = userMonitorService.forceLogout(userId);
        return result ? ApiResponse.ok() : ApiResponse.fail("强退失败");
    }
}
