package com.hackyle.blog.admin.module.system.service;

import com.hackyle.blog.admin.module.system.model.entity.SysConfigEntity;

public interface CacheConfigService {

    void putCache(SysConfigEntity config);

    SysConfigEntity getCache(String configKey);

    SysConfigEntity getCache(long configId);

    boolean delCache(String configKey);

    boolean delCache(long configId);

    void clearCache();

}
