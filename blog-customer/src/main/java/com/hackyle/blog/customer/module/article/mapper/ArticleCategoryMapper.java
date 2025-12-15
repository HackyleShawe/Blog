package com.hackyle.blog.customer.module.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.customer.module.article.model.dto.CategoryArticleDto;
import com.hackyle.blog.customer.module.article.model.entity.ArticleCategoryRelationEntity;
import com.hackyle.blog.customer.module.article.model.entity.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategoryRelationEntity> {

    List<CategoryArticleDto> getAllCategory();

    List<ArticleEntity> getArtilceByCategoryId(Long categoryId);

}
