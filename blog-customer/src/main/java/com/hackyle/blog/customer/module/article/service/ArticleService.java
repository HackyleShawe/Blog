package com.hackyle.blog.customer.module.article.service;

import com.github.pagehelper.PageInfo;
import com.hackyle.blog.customer.module.article.model.dto.ArticleQueryDto;
import com.hackyle.blog.customer.module.article.model.vo.ArticleVo;

public interface ArticleService {
    PageInfo<ArticleVo> list(ArticleQueryDto articleQueryDto);

    ArticleVo get(String articleCode);

    ArticleVo get(String categoryCode, String articleCode);

}
