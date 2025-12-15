package com.hackyle.blog.admin.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.AuthorAddDto;
import com.hackyle.blog.admin.module.article.model.dto.AuthorQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.AuthorUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.AuthorEntity;
import com.hackyle.blog.admin.module.article.model.vo.AuthorVo;

import java.util.List;
import java.util.Set;

public interface AuthorService extends IService<AuthorEntity> {
    boolean add(AuthorAddDto authorAddDto);

    boolean del(Set<Long> idSet);

    boolean update(AuthorUpdateDto updateDto);

    AuthorVo get(String id);

    PageInfo<AuthorVo> list(AuthorQueryDto queryDto);

    List<AuthorVo> all();

}
