package com.hackyle.blog.customer.module.article.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.customer.module.article.model.dto.CategoryDto;
import com.hackyle.blog.customer.module.article.model.dto.CategoryQueryDto;
import com.hackyle.blog.customer.module.article.model.entity.CategoryEntity;
import com.hackyle.blog.customer.module.article.model.vo.ArticleVo;
import com.hackyle.blog.customer.module.article.model.vo.CategoryVo;

import java.util.List;

public interface CategoryService extends IService<CategoryEntity> {

    List<CategoryVo> getCategoryPage();

    List<CategoryDto> getByArticleIds(List<Long> articleIds);

    PageInfo<ArticleVo> selectCategoryArticles(CategoryQueryDto queryDto);

}
