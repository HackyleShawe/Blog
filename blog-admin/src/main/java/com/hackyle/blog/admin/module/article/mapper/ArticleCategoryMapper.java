package com.hackyle.blog.admin.module.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.admin.module.article.model.dto.ArticleCategoryRelationDto;
import com.hackyle.blog.admin.module.article.model.entity.ArticleCategoryRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategoryRelationEntity> {
    int batchInsert(@Param("articleId") long articleId, @Param("categoryIds") Set<Long> categoryIds);

    int deleteByArticleIds(@Param("articleIds")Set<Long> idSet);

    List<ArticleCategoryRelationDto> getCategoryByArticleId(@Param("articleIds")Set<Long> articleId);

    List<ArticleCategoryRelationDto> getArticleByCategoryId(@Param("categoryIds")Set<Long> categoryIds);
}
