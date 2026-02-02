package com.hackyle.blog.admin.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailAddDto;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailUpdateDto;
import com.hackyle.blog.admin.module.system.model.entity.SysDictDetailEntity;
import com.hackyle.blog.admin.module.system.model.vo.DictDetailVo;

import java.util.List;
import java.util.Set;

public interface SysDictDetailService extends IService<SysDictDetailEntity> {

    boolean add(List<DictDetailAddDto> addDto);

    boolean del(Set<Long> idSet);

    boolean update(DictDetailUpdateDto updateDto);

    DictDetailVo getByDetailId(Long id);

    PageInfo<DictDetailVo> list(DictDetailQueryDto queryDto);
}
