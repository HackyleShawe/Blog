package com.hackyle.blog.admin.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.ArticleAddDto;
import com.hackyle.blog.admin.module.article.model.dto.ArticleQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.ArticleUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.ArticleEntity;
import com.hackyle.blog.admin.module.article.model.vo.ArticleListVo;
import com.hackyle.blog.admin.module.article.model.vo.ArticleVo;
import com.hackyle.blog.admin.module.article.model.vo.AuthorVo;
import com.hackyle.blog.admin.module.article.model.vo.CategoryVo;

import java.util.List;
import java.util.Set;

public interface ArticleService extends IService<ArticleEntity> {
    String add(ArticleAddDto addDto);

    void addArticleSave(ArticleAddDto addDto, ArticleEntity articleEntity);

    boolean del(Set<Long> idSet);

    boolean update(ArticleUpdateDto updateDto);

    void updateArticleSave(ArticleUpdateDto updateDto);

    ArticleVo get(String id);

    PageInfo<ArticleListVo> list(ArticleQueryDto queryDto);

}
