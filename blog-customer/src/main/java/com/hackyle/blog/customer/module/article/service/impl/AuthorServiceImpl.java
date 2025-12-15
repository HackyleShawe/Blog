package com.hackyle.blog.customer.module.article.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.customer.module.article.mapper.AuthorMapper;
import com.hackyle.blog.customer.module.article.model.dto.AuthorDto;
import com.hackyle.blog.customer.module.article.model.entity.AuthorEntity;
import com.hackyle.blog.customer.module.article.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AuthorServiceImpl extends ServiceImpl<AuthorMapper, AuthorEntity>
    implements AuthorService {

    @Autowired
    private AuthorMapper authorMapper;

    /**
     * 获取多个文章的作者信息
     * @param articleIds
     */
    @Override
    public List<AuthorDto> getByArticleIds(List<Long> articleIds) {
        if(CollectionUtil.isEmpty(articleIds)){
            return Collections.emptyList();
        }

        return authorMapper.selectByArticleIds(articleIds);
    }
}




