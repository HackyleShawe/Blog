package com.hackyle.blog.customer.module.article.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import com.hackyle.blog.customer.module.article.mapper.ArticleCategoryMapper;
import com.hackyle.blog.customer.module.article.mapper.CategoryMapper;
import com.hackyle.blog.customer.module.article.model.dto.AuthorDto;
import com.hackyle.blog.customer.module.article.model.dto.CategoryArticleDto;
import com.hackyle.blog.customer.module.article.model.dto.CategoryDto;
import com.hackyle.blog.customer.module.article.model.dto.CategoryQueryDto;
import com.hackyle.blog.customer.module.article.model.entity.ArticleEntity;
import com.hackyle.blog.customer.module.article.model.entity.CategoryEntity;
import com.hackyle.blog.customer.module.article.model.vo.ArticleVo;
import com.hackyle.blog.customer.module.article.model.vo.CategoryVo;
import com.hackyle.blog.customer.module.article.service.AuthorService;
import com.hackyle.blog.customer.module.article.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity>
        implements CategoryService {

    @Autowired
    private AuthorService authorService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Value("${blog.article-path-prefix}")
    private String articlePathPrefix;

    /**
     * 获取分类页的数据
     */
    @Override
    public List<CategoryVo> getCategoryPage() {
        List<CategoryArticleDto> categoryList = articleCategoryMapper.getAllCategory();
        if(CollectionUtil.isEmpty(categoryList)){
            return Collections.emptyList();
        }

        Map<Long, Integer> categoryMap = categoryList.stream().collect(Collectors.toMap(CategoryArticleDto::getCategoryId, CategoryArticleDto::getArticleNum));

        List<CategoryEntity> categoryEntityList = this.listByIds(categoryMap.keySet());
        List<CategoryVo> categoryVos = BeanCopyUtils.copyList(categoryEntityList, CategoryVo.class);
        for (CategoryVo categoryVo : categoryVos) {
            Integer articleNum = categoryMap.get(categoryVo.getId());
            categoryVo.setArticleNum(articleNum == null ? 0 : articleNum);
        }

        return categoryVos;
    }

    @Override
    public List<CategoryDto> getByArticleIds(List<Long> articleIds) {
        if(CollectionUtils.isEmpty(articleIds)) {
            return Collections.emptyList();
        }

        return categoryMapper.selectByArticleIds(articleIds);
    }

    @Override
    public PageInfo<ArticleVo> selectCategoryArticles(CategoryQueryDto queryDto) {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        List<ArticleEntity> articleEntities = articleCategoryMapper.getArtilceByCategoryId(queryDto.getCategoryId());
        if(CollectionUtil.isEmpty(articleEntities)) {
            return new PageInfo<>();
        }

        List<Long> articleIds = articleEntities.stream().map(ArticleEntity::getId).toList();
        //获取文章的分类信息
        List<CategoryDto> articleCategoryDtos = this.getByArticleIds(articleIds);
        Map<Long, List<CategoryDto>> articleCategoryMap = articleCategoryDtos.stream().collect(Collectors.groupingBy(CategoryDto::getArticleId));
        //获取文章的作者信息
        List<AuthorDto> articleAuthorDtos = authorService.getByArticleIds(articleIds);
        Map<Long, List<AuthorDto>> articleAuthorMap = articleAuthorDtos.stream().collect(Collectors.groupingBy(AuthorDto::getArticleId));

        //塞入文章分类、作者等额外信息值
        List<ArticleVo> articleVoList = new ArrayList<>(articleCategoryDtos.size());
        for (ArticleEntity articleEntity : articleEntities) {
            ArticleVo articleVo = BeanCopyUtils.copy(articleEntity, ArticleVo.class);
            articleVo.setUrl(articlePathPrefix + articleEntity.getPath());

            LocalDateTime updateTime = articleEntity.getUpdateTime() == null ? articleEntity.getCreateTime() : articleEntity.getUpdateTime();
            articleVo.setUpdateTime(updateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            List<CategoryDto> categoryDtos = articleCategoryMap.get(articleEntity.getId());
            if(CollectionUtil.isNotEmpty(categoryDtos)) {
                String categoryNames = categoryDtos.stream().map(CategoryDto::getName).collect(Collectors.joining(","));
                articleVo.setCategoryNames(categoryNames);
            }

            List<AuthorDto> authorDtos = articleAuthorMap.get(articleVo.getId());
            if(CollectionUtil.isNotEmpty(authorDtos)) {
                String authorNames = authorDtos.stream().map(AuthorDto::getNickName).collect(Collectors.joining(","));
                articleVo.setAuthorNames(authorNames);
            }
            articleVoList.add(articleVo);
        }

        PageInfo<ArticleVo> pageInfo = PageHelperUtils.getPageInfo(articleEntities, ArticleVo.class);
        pageInfo.setList(articleVoList);

        return pageInfo;
    }
}
