package com.hackyle.blog.admin.module.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.admin.module.article.model.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {

    int countCategoryArticle(@Param("idSet") Set<Long> idSet);

}




