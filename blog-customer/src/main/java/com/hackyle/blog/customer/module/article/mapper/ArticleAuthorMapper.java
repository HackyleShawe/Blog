package com.hackyle.blog.customer.module.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.customer.module.article.model.entity.ArticleAuthorRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface ArticleAuthorMapper extends BaseMapper<ArticleAuthorRelationEntity> {

}
