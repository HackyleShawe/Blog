package com.hackyle.blog.admin.module.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.mapper.AuthorMapper;
import com.hackyle.blog.admin.module.article.model.dto.AuthorAddDto;
import com.hackyle.blog.admin.module.article.model.dto.AuthorQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.AuthorUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.AuthorEntity;
import com.hackyle.blog.admin.module.article.model.vo.AuthorVo;
import com.hackyle.blog.admin.module.article.service.AuthorService;
import com.hackyle.blog.common.enums.DeletedEnum;
import com.hackyle.blog.common.enums.StatusEnum;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import com.hackyle.blog.common.util.SnowFlakeIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class AuthorServiceImpl extends ServiceImpl<AuthorMapper, AuthorEntity>
        implements AuthorService {
    @Autowired
    private AuthorMapper authorMapper;

    @Override
    public boolean add(AuthorAddDto authorAddDto) {
        AuthorEntity authorEntity = BeanCopyUtils.copy(authorAddDto, AuthorEntity.class);

        //nick name存在性检查
        LambdaQueryWrapper<AuthorEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthorEntity::getNickName, authorAddDto.getNickName())
                .eq(AuthorEntity::getDeleted, DeletedEnum.FALSE.getCode());
        long count = this.count(queryWrapper);
        if(count >= 1) {
            throw new IllegalArgumentException("Nick Name已存在，请重新的定义");
        }

        authorEntity.setId(SnowFlakeIdUtils.getInstance().nextId());
        authorEntity.setStatus(Boolean.TRUE);
        int inserted = authorMapper.insert(authorEntity);
        return inserted > 0;
    }

    @Override
    public boolean del(Set<Long> idSet) {
        //检查作者下是否有文章
        int articleCount = authorMapper.countAuthorArticle(idSet);
        if(articleCount > 0) {
            throw new BizException("该作者下存在文章，不能删除");
        }

        int delCount = authorMapper.deleteBatchIds(idSet);
        return delCount == idSet.size();
    }

    @Override
    public boolean update(AuthorUpdateDto updateDto) {
        AuthorEntity authorEntity = BeanCopyUtils.copy(updateDto, AuthorEntity.class);

        //Code重复性检查：不检查status，即使被禁用的，也不能出现重复
        QueryWrapper<AuthorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(AuthorEntity::getId, updateDto.getId())
                .eq(AuthorEntity::getNickName, authorEntity.getNickName())
                .eq(AuthorEntity::getDeleted, Boolean.FALSE);
        long codeCount = authorMapper.selectCount(queryWrapper);
        if(codeCount > 1) {
            throw new IllegalArgumentException("作者已存在，请重新的定义");
        }

        int updated = authorMapper.updateById(authorEntity);
        return updated > 0;
    }

    @Override
    public AuthorVo get(String id) {
        AuthorEntity authorEntity = authorMapper.selectById(id);
        return authorEntity == null? null : BeanCopyUtils.copy(authorEntity, AuthorVo.class);
    }

    @Override
    public PageInfo<AuthorVo> list(AuthorQueryDto queryDto) {
        LambdaQueryWrapper<AuthorEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthorEntity::getDeleted, DeletedEnum.FALSE.getCode());
        queryWrapper.eq(AuthorEntity::getStatus, StatusEnum.YES.getCode());

        //组装查询条件
        if(StringUtils.isNotBlank(queryDto.getNickName())) {
            queryWrapper.like(AuthorEntity::getNickName, queryDto.getNickName());
        }
        if(StringUtils.isNotBlank(queryDto.getRealName())) {
            queryWrapper.like(AuthorEntity::getRealName, queryDto.getRealName());
        }
        queryWrapper.orderByDesc(AuthorEntity::getUpdateTime);

        PageHelper.startPage(queryDto.pageNum, queryDto.pageSize);
        List<AuthorEntity> authors = this.list(queryWrapper);

        PageInfo<AuthorVo> pageInfo = PageHelperUtils.getPageInfo(authors, AuthorVo.class);
        for (AuthorVo authorVo : pageInfo.getList()) {
            if(authorVo.getUpdateTime() == null) {
                authorVo.setUpdateTime(authorVo.getCreateTime());
            }
        }

        return pageInfo;
    }

    @Override
    public List<AuthorVo> all() {
        LambdaQueryWrapper<AuthorEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AuthorEntity::getDeleted, DeletedEnum.FALSE.getCode());
        queryWrapper.eq(AuthorEntity::getStatus, StatusEnum.YES.getCode());
        List<AuthorEntity> authorEntities = this.list(queryWrapper);
        return BeanCopyUtils.copyList(authorEntities, AuthorVo.class);
    }
}
