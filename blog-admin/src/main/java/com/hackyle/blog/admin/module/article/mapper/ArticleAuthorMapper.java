package com.hackyle.blog.admin.module.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.admin.module.article.model.dto.ArticleAuthorRelationDto;
import com.hackyle.blog.admin.module.article.model.entity.ArticleAuthorRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ArticleAuthorMapper extends BaseMapper<ArticleAuthorRelationEntity> {
    int batchInsert(@Param("articleId") long articleId, @Param("authorIds") Set<Long> authorIds);

    int deleteByArticleIds(@Param("articleIds") Set<Long> idSet);

    List<ArticleAuthorRelationDto> getAuthorByArticleId(@Param("articleIds")Set<Long> articleId);

}
