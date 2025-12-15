package com.hackyle.blog.admin.module.article.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.mapper.ArticleCategoryMapper;
import com.hackyle.blog.admin.module.article.mapper.CategoryMapper;
import com.hackyle.blog.admin.module.article.model.dto.ArticleCategoryRelationDto;
import com.hackyle.blog.admin.module.article.model.dto.CategoryAddDto;
import com.hackyle.blog.admin.module.article.model.dto.CategoryQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.CategoryUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.ArticleCategoryRelationEntity;
import com.hackyle.blog.admin.module.article.model.entity.CategoryEntity;
import com.hackyle.blog.admin.module.article.model.vo.CategoryVo;
import com.hackyle.blog.admin.module.article.service.CategoryService;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import com.hackyle.blog.common.util.SnowFlakeIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity>
        implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public boolean add(CategoryAddDto categoryAddDto) {
        CategoryEntity categoryEntity = BeanCopyUtils.copy(categoryAddDto, CategoryEntity.class);
        categoryEntity.setCode(categoryEntity.getCode().toLowerCase());

        //code重复性检查：不检查status，即使被禁用的，也不能出现重复
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CategoryEntity::getCode, categoryAddDto.getCode())
                .eq(CategoryEntity::getDeleted, Boolean.FALSE);
        long count = categoryMapper.selectCount(queryWrapper);
        if(count >= 1) {
            throw new BizException("文章分类已存在，请重新定义");
        }

        //检查pid是否合法
        if(categoryEntity.getPid() != null && categoryEntity.getPid() > 0) {
            CategoryEntity entityByPid = categoryMapper.selectById(categoryEntity.getPid());
            if(entityByPid == null) {
                throw new BizException("文章分类父级不存在，请重新定义");
            }
        }

        categoryEntity.setId(SnowFlakeIdUtils.getInstance().nextId());
        categoryEntity.setStatus(Boolean.TRUE);
        int inserted = categoryMapper.insert(categoryEntity);
        return inserted == 1;
    }

    @Override
    public boolean del(Set<Long> idSet) {
        //检查分类下是否有文章
        int count = categoryMapper.countCategoryArticle(idSet);
        if(count > 0) {
            throw new BizException("分类下有文章，不能删除");
        }

        int delCount = categoryMapper.deleteBatchIds(idSet);
        return delCount == idSet.size();
    }

    @Override
    public boolean update(CategoryUpdateDto categoryUpdateDto) {
        CategoryEntity categoryEntity = BeanCopyUtils.copy(categoryUpdateDto, CategoryEntity.class);
        categoryEntity.setCode(categoryEntity.getCode().toLowerCase());

        //Code重复性检查：不检查status，即使被禁用的，也不能出现重复
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(CategoryEntity::getId, categoryUpdateDto.getId())
                .eq(CategoryEntity::getCode, categoryEntity.getCode())
                .eq(CategoryEntity::getDeleted, Boolean.FALSE);
        long codeCount = categoryMapper.selectCount(queryWrapper);
        if(codeCount > 0) {
            throw new IllegalArgumentException("Code已存在，请重新的定义");
        }
        //检查pid是否合法
        if(categoryEntity.getPid() != null && categoryEntity.getPid() > 0) {
            CategoryEntity entityByPid = categoryMapper.selectById(categoryEntity.getPid());
            if(entityByPid == null) {
                throw new BizException("文章分类父级不存在，请重新定义");
            }
        }

        UpdateWrapper<CategoryEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(CategoryEntity::getId, categoryEntity.getId());
        int updated = categoryMapper.update(categoryEntity, updateWrapper);
        return updated == 1;
    }

    @Override
    public CategoryVo get(String id) {
        long idd = Long.parseLong(id);

        CategoryEntity categoryEntity = categoryMapper.selectById(idd);
        CategoryVo categoryVo = BeanCopyUtils.copy(categoryEntity, CategoryVo.class);
        return categoryVo;
    }

    @Override
    public PageInfo<CategoryVo> list(CategoryQueryDto queryDto) {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryEntity::getDeleted, Boolean.FALSE);

        //组装查询条件
        if(StringUtils.isNotBlank(queryDto.getName())) {
            queryWrapper.like(CategoryEntity::getName, queryDto.getName());
        }
        if(StringUtils.isNotBlank(queryDto.getCode())) {
            queryWrapper.like(CategoryEntity::getCode, queryDto.getCode());
        }
        queryWrapper.orderByDesc(CategoryEntity::getUpdateTime);

        PageHelper.startPage(queryDto.pageNum, queryDto.pageSize);
        List<CategoryEntity> categoryEntityList = this.list(queryWrapper);

        PageInfo<CategoryVo> pageInfo = PageHelperUtils.getPageInfo(categoryEntityList, CategoryVo.class);

        if(CollectionUtil.isNotEmpty(categoryEntityList)) {
            Set<Long> categoryIds = categoryEntityList.stream().map(CategoryEntity::getId).filter(Objects::nonNull).collect(Collectors.toSet());
            //收集每个分类下的文章数
            List<ArticleCategoryRelationDto> articleCategory = articleCategoryMapper.getArticleByCategoryId(categoryIds);

            Map<Long, List<ArticleCategoryRelationDto>> categoryMap = articleCategory.stream().collect(Collectors.groupingBy(ArticleCategoryRelationDto::getCategoryId));
            for (CategoryVo categoryVo : pageInfo.getList()) {
                List<ArticleCategoryRelationDto> articles = categoryMap.get(categoryVo.getId());
                categoryVo.setArticleCount(CollectionUtil.isEmpty(articles) ? 0 : articles.size());
                if(categoryVo.getUpdateTime() == null) {
                    categoryVo.setUpdateTime(categoryVo.getCreateTime());
                }
            }
        }

        return pageInfo;
    }

    @Override
    public List<CategoryVo> all() {
        List<CategoryEntity> categoryEntityList = this.list();
        return BeanCopyUtils.copyList(categoryEntityList, CategoryVo.class);
    }

}
