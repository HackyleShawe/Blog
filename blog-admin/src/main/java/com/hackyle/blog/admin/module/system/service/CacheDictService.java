package com.hackyle.blog.admin.module.system.service;


import com.hackyle.blog.admin.module.system.model.entity.SysConfigEntity;
import com.hackyle.blog.admin.module.system.model.entity.SysDictDetailEntity;
import com.hackyle.blog.admin.module.system.model.entity.SysDictEntity;

import java.util.List;

public interface CacheDictService {
    void putCache(SysDictEntity dictEntity);

    boolean delCache(long dictId);

    boolean delCache(String dictCode);

    SysDictEntity getCache(String dictCode);

    SysDictEntity getCache(long dictId);

    void clearCache();

}
