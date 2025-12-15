package com.hackyle.blog.customer.module.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.customer.module.article.model.entity.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<ArticleEntity> {

    List<ArticleEntity> fetchList(@Param("keywords") List<String> keywords);

}




