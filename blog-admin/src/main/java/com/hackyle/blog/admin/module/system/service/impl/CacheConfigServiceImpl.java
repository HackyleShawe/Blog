package com.hackyle.blog.admin.module.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.admin.infrastructure.redis.CacheKey;
import com.hackyle.blog.admin.module.system.model.entity.SysConfigEntity;
import com.hackyle.blog.admin.module.system.service.CacheConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 抽取缓存系统配置相关的业务逻辑
 *
 * 缓存设计
 * 数据结构：hash
 * Key：ba:sys:config，field：configKey和表ID，Val：String，数据库表实体
 * 实现方式：Redis
 * 初始化加载：@PostConstruct
 * 逻辑删、未生效的是否放入缓存：是，具体判定交给业务逻辑，缓存类只负责缓存
 * 增、删、改时的数据一致性（保证缓存与DB）：先DB，再缓存
 * 在hash中，同一条记录，存两份，分别是以configKey和表ID为feild，val时一样的。这样可以通过key和id快速找到对应的val
 */
@Service
@Slf4j
public class CacheConfigServiceImpl implements CacheConfigService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增和更新都可以调用此API，因为会自动覆盖
     */
    @Override
    public void putCache(SysConfigEntity config) {
        if(config == null || StringUtils.isBlank(config.getConfigKey()) || config.getId() == null) {
            return;
        }
        String key = CacheKey.Sys.CONFIG_HASH_KEY;
        String val = JSON.toJSONString(config);
        redisTemplate.opsForHash().put(key, config.getConfigKey(), val);
        redisTemplate.opsForHash().put(key, config.getId(), val);

        Boolean hasKey = redisTemplate.hasKey(key);//检查是否存在hash这个key
        if(hasKey) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS); //设置有效期
        }
    }

    @Override
    public SysConfigEntity getCache(String configKey) {
        if(StringUtils.isBlank(configKey)) {
            return null;
        }
        String key = CacheKey.Sys.CONFIG_HASH_KEY;
        Object object = redisTemplate.opsForHash().get(key, configKey);
        //return JSON.parseObject(JSON.toJSONString(object), SysConfigEntity.class); 导出原始JSON串外再套了一层JSON
        return JSON.parseObject((String) object, SysConfigEntity.class);
    }
    @Override
    public SysConfigEntity getCache(long configId) {
        String key = CacheKey.Sys.CONFIG_HASH_KEY;
        Object object = redisTemplate.opsForHash().get(key, configId);
        return JSON.parseObject((String) object, SysConfigEntity.class);
    }

    @Override
    public boolean delCache(String configKey) {
        SysConfigEntity config = getCache(configKey);
        if(config == null) {
            return false;
        }

        String key = CacheKey.Sys.CONFIG_HASH_KEY;
        Long delCount = redisTemplate.opsForHash().delete(key, configKey, config.getId());
        return 2L == delCount; //因为同一条记录，会缓存两次，删也要全部删除
    }
    @Override
    public boolean delCache(long configId) {
        SysConfigEntity config = getCache(configId);
        if(config == null) {
            return false;
        }

        String key = CacheKey.Sys.CONFIG_HASH_KEY;
        Long delCount = redisTemplate.opsForHash().delete(key, configId, config.getConfigKey());
        return 2L == delCount; //因为同一条记录，会缓存两次，删也要全部删除
    }

    @Override
    public void clearCache() {
        String key = CacheKey.Sys.CONFIG_HASH_KEY;
        redisTemplate.delete(key);
    }
}
