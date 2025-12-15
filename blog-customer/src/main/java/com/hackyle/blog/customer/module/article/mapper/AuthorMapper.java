package com.hackyle.blog.customer.module.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.customer.module.article.model.dto.AuthorDto;
import com.hackyle.blog.customer.module.article.model.entity.AuthorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthorMapper extends BaseMapper<AuthorEntity> {
    List<AuthorDto> selectByArticleIds(@Param("articleIds") List<Long> articleIds);
}
