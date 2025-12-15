package com.hackyle.blog.customer.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.customer.module.article.model.dto.AuthorDto;
import com.hackyle.blog.customer.module.article.model.entity.AuthorEntity;

import java.util.List;

public interface AuthorService extends IService<AuthorEntity> {
    List<AuthorDto> getByArticleIds(List<Long> articleIds);

}
