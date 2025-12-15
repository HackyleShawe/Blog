package com.hackyle.blog.admin.module.article.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.mapper.ArticleMapper;
import com.hackyle.blog.admin.module.article.mapper.CommentMapper;
import com.hackyle.blog.admin.module.article.model.dto.CommentAddDto;
import com.hackyle.blog.admin.module.article.model.dto.CommentQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.CommentUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.ArticleEntity;
import com.hackyle.blog.admin.module.article.model.entity.CommentEntity;
import com.hackyle.blog.admin.module.article.model.vo.CommentVo;
import com.hackyle.blog.admin.module.article.service.CommentService;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.ObjectUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import com.hackyle.blog.common.util.SnowFlakeIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity>
        implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public boolean add(CommentAddDto addDto) {
        addCheck(addDto);

        CommentEntity commentEntity = BeanCopyUtils.copy(addDto, CommentEntity.class);
        commentEntity.setId(SnowFlakeIdUtils.getInstance().nextId());

        return this.save(commentEntity);
    }

    private void addCheck(CommentAddDto addDto) {
        Long articleId = addDto.getArticleId();
        ArticleEntity articleEntity = articleMapper.selectById(articleId);
        if(articleEntity == null) {
            throw new BizException("文章不存在");
        }

        //检查rootId和pid
        Long rootId = addDto.getRootId();
        if(rootId != null && rootId > 0) {
            CommentEntity rootEntity = this.getById(rootId);
            if(rootEntity == null) {
                throw new BizException("根评论不存在");
            }
        }
        Long pid = addDto.getPid();
        if(pid != null && pid > 0) {
            CommentEntity parentEntity = this.getById(pid);
            if(parentEntity == null) {
                throw new BizException("父评论不存在");
            }
        }

    }

    /**
     * 如果是根评论：将会删除该个评论下的所有子评论
     * 如果是非根评论：只会删除该评论
     */
    @Transactional
    @Override
    public boolean del(Set<Long> idSet) {
        List<CommentEntity> delTargets = this.listByIds(idSet);
        if(CollectionUtil.isEmpty(delTargets)) {
            return true;
        }

        //rootId为0，说明是顶级评论，直接删除rootId为当前Id的记录
        //rootId不为0，则删除该条评论即可
        Set<Long> topIds = new HashSet<>();
        Set<Long> delIds = new HashSet<>();
        delTargets.forEach(comment -> {
            if(0 == comment.getRootId()) {
                topIds.add(comment.getId());
            } else {
                delIds.add(comment.getId());
            }
        });

        if(CollectionUtil.isNotEmpty(topIds)) {
            LambdaQueryWrapper<CommentEntity> queryWrapper = Wrappers.<CommentEntity>lambdaQuery()
                    .in(CommentEntity::getRootId, topIds);
            boolean removed = this.remove(queryWrapper);
            if(!removed) {
                throw new BizException("删除根评论出错");
            }
        }
        if(CollectionUtil.isNotEmpty(delIds)) {
            boolean removed = this.removeBatchByIds(delIds);
            if(!removed) {
                throw new BizException("删除评论出错");
            }
        }

        return true;
    }

    /**
     * 只能更新评论内容，不能更新评论者信息、pid、rootid、被评论者
     */
    @Override
    public boolean update(CommentUpdateDto updateDto) {
        updateCheck(updateDto);

        CommentEntity commentEntity = BeanCopyUtils.copy(updateDto, CommentEntity.class);
        boolean updated = this.updateById(commentEntity);

        return updated;
    }

    private void updateCheck(CommentUpdateDto updateDto) {
        boolean empty = ObjectUtils.isEmpty(updateDto, "id");
        if(empty) {
            throw new BizException("更新内容不能为空");
        }

        Long id = updateDto.getId();
        CommentEntity commentEntity = this.getById(id);
        if(commentEntity == null) {
            throw new BizException("评论不存在");
        }

        //ArticleEntity articleEntity = articleMapper.selectById(updateDto.getArticleId());
        //if(articleEntity == null) {
        //    throw new BizException("文章不存在");
        //}

    }

    /**
     * 获取某条评论的详情
     */
    @Override
    public CommentVo get(Long id) {
        CommentEntity comment = this.getById(id);
        if(comment != null) {
            return BeanCopyUtils.copy(comment, CommentVo.class);
        }

        return new CommentVo();
    }

    @Override
    public PageInfo<CommentVo> list(CommentQueryDto queryDto) {
        LambdaQueryWrapper<CommentEntity> queryWrapper = Wrappers.<CommentEntity>lambdaQuery()
                .eq(queryDto.getArticleId() != null, CommentEntity::getArticleId, queryDto.getArticleId())
                .eq(queryDto.getPid() != null, CommentEntity::getPid, queryDto.getPid())
                .eq(queryDto.getRootId() != null, CommentEntity::getRootId, queryDto.getRootId())
                .eq(queryDto.getDeleted() != null, CommentEntity::getDeleted, queryDto.getDeleted())
                .eq(queryDto.getReleased() != null, CommentEntity::getReleased, queryDto.getReleased());

        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        List<CommentEntity> commentEntities = this.list(queryWrapper);

        return PageHelperUtils.getPageInfo(commentEntities, CommentVo.class);
    }

    /**
     * 构建某个文章的所有评论树，不排除已删除的评论
     */
    @Override
    public List<CommentVo> treeByArticle(Long articleId) {
        List<CommentEntity> allComments = this.list(Wrappers.<CommentEntity>lambdaQuery()
                .eq(CommentEntity::getArticleId, articleId));
        if(CollectionUtil.isEmpty(allComments)) {
            return Collections.emptyList();
        }
        List<CommentVo> categoryVos = BeanCopyUtils.copyList(allComments, CommentVo.class);

        //根据pid分组
        Map<Long, List<CommentVo>> pidMap = new HashMap<>();
        for (CommentVo comment : categoryVos) {
            List<CommentVo> pidList = pidMap.getOrDefault(comment.getPid(), new ArrayList<>());
            pidList.add(comment);
            pidMap.put(comment.getPid(), pidList);
        }

        List<CommentVo> topComment = new ArrayList<>();
        for (CommentVo comment : categoryVos) {
            if(0 == comment.getPid() && 0 == comment.getRootId()) {
                topComment.add(comment);
            }
            List<CommentVo> children = pidMap.get(comment.getId());
            comment.setChildren(children);
        }

        return topComment;
    }

    @Override
    public boolean deleteByArticleIds(Set<Long> idSet) {
        if(CollectionUtil.isEmpty(idSet)) {
            return true;
        }

        boolean removed = this.remove(Wrappers.<CommentEntity>lambdaQuery()
                .in(CommentEntity::getArticleId, idSet));
        return removed;
    }

    @Override
    public PageInfo<CommentVo> rootList(CommentQueryDto queryDto) {
        LambdaQueryWrapper<CommentEntity> queryWrapper = Wrappers.<CommentEntity>lambdaQuery()
                .eq(CommentEntity::getRootId, 0) //只查询根评论
                .eq(queryDto.getArticleId() != null, CommentEntity::getArticleId, queryDto.getArticleId())
                .eq(queryDto.getPid() != null, CommentEntity::getPid, queryDto.getPid())
                .eq(queryDto.getRootId() != null, CommentEntity::getRootId, queryDto.getRootId());

        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        List<CommentEntity> commentEntities = this.list(queryWrapper);


        return PageHelperUtils.getPageInfo(commentEntities, CommentVo.class);
    }

    @Override
    public List<CommentVo> getByRoot(Long rootId) {
        List<CommentEntity> commentEntities = this.list(Wrappers.<CommentEntity>lambdaQuery().eq(CommentEntity::getRootId, rootId));
        if(CollectionUtil.isEmpty(commentEntities)) {
            return Collections.emptyList();
        }

        return BeanCopyUtils.copyList(commentEntities, CommentVo.class);
    }


}
