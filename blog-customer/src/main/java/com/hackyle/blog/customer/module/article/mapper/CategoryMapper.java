package com.hackyle.blog.customer.module.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.customer.module.article.model.dto.CategoryDto;
import com.hackyle.blog.customer.module.article.model.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {

    List<CategoryDto> selectByArticleIds(@Param("articleIds") List<Long> articleIds);
}




