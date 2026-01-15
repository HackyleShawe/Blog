package com.hackyle.blog.admin.module.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hackyle.blog.admin.infrastructure.redis.CacheKey;
import com.hackyle.blog.admin.module.system.model.entity.SysDictDetailEntity;
import com.hackyle.blog.admin.module.system.model.entity.SysDictEntity;
import com.hackyle.blog.admin.module.system.service.CacheDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 抽取缓存字典相关的业务逻辑
 *
 * 缓存设计
 * 数据结构：hash
 * Key：ba:sys:dict，field：dictCode和表ID，Val：String，数据库表实体
 * 实现方式：Redis
 * 初始化加载：@PostConstruct
 * 逻辑删、未生效的是否放入缓存：是，具体判定交给业务逻辑，缓存类只负责缓存
 * 增、删、改时的数据一致性（保证缓存与DB）：先DB，再缓存
 * 增删改字典明细（detail）时，怎么保持一致性？先DB，再查DB，再放入缓存
 * 在hash中，同一条记录，存两份，分别是以dictCode和表ID为feild，val时一样的。这样可以通过key和id快速找到对应的val
 */
@Service
@Slf4j
public class CacheDictServiceImpl implements CacheDictService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 放入缓存：新增和更新都可
     */
    @Override
    public void putCache(SysDictEntity dictEntity) {
        if(dictEntity == null || dictEntity.getId() == null || StringUtils.isBlank(dictEntity.getCode())){
            return;
        }

        String key = CacheKey.Sys.DICT_HASH_KEY;
        String val = JSON.toJSONString(dictEntity);
        redisTemplate.opsForHash().put(key, dictEntity.getId(), val);
        redisTemplate.opsForHash().put(key, dictEntity.getCode(), val);

        Boolean hasKey = redisTemplate.hasKey(key);//检查是否存在hash这个key
        if(hasKey) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS); //设置有效期
        }
    }

    @Override
    public boolean delCache(long dictId) {
        SysDictEntity dict = getCache(dictId);
        if(dict == null) {
            return false;
        }

        String key = CacheKey.Sys.DICT_HASH_KEY;
        Long delCount = redisTemplate.opsForHash().delete(key, dictId, dict.getCode());
        return 2L == delCount; //因为同一条记录，会缓存两次，删也要全部删除
    }

    @Override
    public boolean delCache(String dictCode) {
        SysDictEntity dict = getCache(dictCode);
        if(dict == null) {
            return false;
        }

        String key = CacheKey.Sys.DICT_HASH_KEY;
        Long delCount = redisTemplate.opsForHash().delete(key, dictCode, dict.getId());
        return 2L == delCount; //因为同一条记录，会缓存两次，删也要全部删除
    }

    @Override
    public SysDictEntity getCache(String dictCode) {
        if(StringUtils.isBlank(dictCode)) {
            return null;
        }
        String key = CacheKey.Sys.DICT_HASH_KEY;
        Object object = redisTemplate.opsForHash().get(key, dictCode);
        if(object == null) {
            return null;
        }

        //return JSON.parseObject(JSON.toJSONString(object), SysDictEntity.class); 导出原始JSON串外再套了一层JSON
        SysDictEntity dictEntity = JSON.parseObject((String) object, SysDictEntity.class);
        JSONObject obj = JSON.parseObject((String) object);
        List<SysDictDetailEntity> details = JSONArray.parseArray(obj.getString("details"), SysDictDetailEntity.class);
        dictEntity.setDetails(details);

        return dictEntity;
    }

    @Override
    public SysDictEntity getCache(long dictId) {
        String key = CacheKey.Sys.DICT_HASH_KEY;
        Object object = redisTemplate.opsForHash().get(key, dictId);
        if(object == null) {
            return null;
        }

        SysDictEntity dictEntity = JSON.parseObject((String) object, SysDictEntity.class);
        JSONObject obj = JSON.parseObject((String) object);
        List<SysDictDetailEntity> details = JSONArray.parseArray(obj.getString("details"), SysDictDetailEntity.class);
        dictEntity.setDetails(details);

        return dictEntity;
    }

    @Override
    public void clearCache() {
        String key = CacheKey.Sys.DICT_HASH_KEY;
        redisTemplate.delete(key);
    }
}
