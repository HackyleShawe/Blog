package com.hackyle.blog.admin.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.model.dto.DictAddDto;
import com.hackyle.blog.admin.module.system.model.dto.DictQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.DictUpdateDto;
import com.hackyle.blog.admin.module.system.model.entity.SysDictEntity;
import com.hackyle.blog.admin.module.system.model.vo.DictVo;

import java.util.Set;

public interface SysDictService extends IService<SysDictEntity> {

    boolean add(DictAddDto addDto);

    boolean del(Set<Long> idSet);

    boolean update(DictUpdateDto updateDto);

    DictVo get(Long id);

    PageInfo<DictVo> list(DictQueryDto queryDto);

    void refreshCache();
}
