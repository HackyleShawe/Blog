package com.hackyle.blog.admin.module.monitor.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hackyle.blog.admin.module.monitor.model.vo.RedisCacheInfoVo;
import com.hackyle.blog.admin.module.monitor.model.vo.ServerInfoVo;
import com.hackyle.blog.admin.module.monitor.service.SystemMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Properties;

@Slf4j
@Service
public class SystemMonitorServiceImpl implements SystemMonitorService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public RedisCacheInfoVo getRedisCacheInfo() {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::info);
        Properties commandStats = (Properties) redisTemplate.execute(
                (RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::dbSize);

        if(commandStats == null) {
            throw new RuntimeException("找不到对应的redis信息。");
        }

        RedisCacheInfoVo cacheInfo = new RedisCacheInfoVo();
        cacheInfo.setInfo(info);
        cacheInfo.setDbSize(dbSize);
        cacheInfo.setCommandStats(new ArrayList<>());

        commandStats.stringPropertyNames().forEach(key -> {
            String property = commandStats.getProperty(key);

            RedisCacheInfoVo.CommonStatusVo commonStatus = new RedisCacheInfoVo.CommonStatusVo();
            commonStatus.setName(StrUtil.removePrefix(key, "cmdstat_"));
            commonStatus.setValue(StrUtil.subBetween(property, "calls=", ",usec"));

            cacheInfo.getCommandStats().add(commonStatus);
        });

        return cacheInfo;
    }

    @Override
    public ServerInfoVo getServerInfo() {
        return ServerInfoVo.fillInfo();

    }
}
