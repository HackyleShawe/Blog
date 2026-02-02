package com.hackyle.blog.admin.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.model.dto.ConfigAddDto;
import com.hackyle.blog.admin.module.system.model.dto.ConfigQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.ConfigUpdateDto;
import com.hackyle.blog.admin.module.system.model.entity.SysConfigEntity;
import com.hackyle.blog.admin.module.system.model.vo.ConfigVo;

import java.util.Set;

public interface SysConfigService extends IService<SysConfigEntity> {

    boolean add(ConfigAddDto addDto);

    boolean del(Set<Long> idSet);

    boolean update(ConfigUpdateDto updateDto);

    ConfigVo get(Long id);

    ConfigVo get(String configKey);

    PageInfo<ConfigVo> list(ConfigQueryDto queryDto);

    boolean refreshCache();

}
