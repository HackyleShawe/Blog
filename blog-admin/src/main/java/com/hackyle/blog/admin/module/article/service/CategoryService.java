package com.hackyle.blog.admin.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.CategoryAddDto;
import com.hackyle.blog.admin.module.article.model.dto.CategoryQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.CategoryUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.CategoryEntity;
import com.hackyle.blog.admin.module.article.model.vo.CategoryVo;

import java.util.List;
import java.util.Set;

public interface CategoryService extends IService<CategoryEntity> {
    boolean add(CategoryAddDto categoryAddDto);

    boolean del(Set<Long> idSet);

    boolean update(CategoryUpdateDto categoryUpdateDto);

    CategoryVo get(String id);

    PageInfo<CategoryVo> list(CategoryQueryDto queryDto);

    List<CategoryVo> all();

}
