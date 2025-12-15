package com.hackyle.blog.admin.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.CommentAddDto;
import com.hackyle.blog.admin.module.article.model.dto.CommentQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.CommentUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.CommentEntity;
import com.hackyle.blog.admin.module.article.model.vo.CommentVo;

import java.util.List;
import java.util.Set;

public interface CommentService extends IService<CommentEntity>  {

    boolean add(CommentAddDto addDto);

    boolean del(Set<Long> idSet);

    boolean update(CommentUpdateDto updateDto);

    CommentVo get(Long id);

    PageInfo<CommentVo> list(CommentQueryDto queryDto);

    List<CommentVo> treeByArticle(Long articleId);

    boolean deleteByArticleIds(Set<Long> idSet);

    PageInfo<CommentVo> rootList(CommentQueryDto queryDto);

    List<CommentVo> getByRoot(Long rootId);

}
