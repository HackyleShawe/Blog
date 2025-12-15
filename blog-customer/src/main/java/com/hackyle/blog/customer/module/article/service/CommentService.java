package com.hackyle.blog.customer.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.common.domain.ApiResponse;
import com.hackyle.blog.customer.module.article.model.dto.CommentAddDto;
import com.hackyle.blog.customer.module.article.model.entity.CommentEntity;
import com.hackyle.blog.customer.module.article.model.vo.CommentVo;

import java.util.List;

public interface CommentService extends IService<CommentEntity> {
    boolean add(CommentAddDto addDto);

    List<CommentVo> getTopByArticle(long articleId);

    List<CommentVo> getByRootId(Long rootId);

}
