package com.hackyle.blog.customer.module.article.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.common.enums.DeletedEnum;
import com.hackyle.blog.common.enums.StatusEnum;
import com.hackyle.blog.common.exception.Page404Exception;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import com.hackyle.blog.customer.module.article.mapper.ArticleMapper;
import com.hackyle.blog.customer.module.article.model.dto.ArticleQueryDto;
import com.hackyle.blog.customer.module.article.model.dto.AuthorDto;
import com.hackyle.blog.customer.module.article.model.dto.CategoryDto;
import com.hackyle.blog.customer.module.article.model.entity.ArticleEntity;
import com.hackyle.blog.customer.module.article.model.entity.CategoryEntity;
import com.hackyle.blog.customer.module.article.model.vo.ArticleVo;
import com.hackyle.blog.customer.module.article.model.vo.CommentVo;
import com.hackyle.blog.customer.module.article.service.ArticleService;
import com.hackyle.blog.customer.module.article.service.AuthorService;
import com.hackyle.blog.customer.module.article.service.CategoryService;
import com.hackyle.blog.customer.module.article.service.CommentService;
import com.hackyle.blog.customer.module.article.service.IdConfusionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity> implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private IdConfusionService idConfusionService;

    @Value("${blog.article-path-prefix}")
    private String articlePathPrefix;

    @Override
    public PageInfo<ArticleVo> list(ArticleQueryDto articleQueryDto) {
        List<String> keywords = new ArrayList<>();
        if(StringUtils.isNotBlank(articleQueryDto.getKeywords())) {
            keywords.addAll(Arrays.asList(articleQueryDto.getKeywords().split(",")));
        }

        PageHelper.startPage(articleQueryDto.getPageNum(), articleQueryDto.getPageSize());
        List<ArticleEntity> articleEntities = articleMapper.fetchList(keywords);
        if(CollectionUtil.isEmpty(articleEntities)) {
            return new PageInfo<>();
        }

        //如果是第一页则塞入置顶文章
        if(articleQueryDto.getPageNum() == 1){
            //获取置顶文章

            //过滤掉置顶已经出现过的文章

            //将置顶文章放在最前面
        }

        List<Long> articleIds = articleEntities.stream().map(ArticleEntity::getId).toList();
        //获取文章的分类信息
        List<CategoryDto> articleCategoryDtos = categoryService.getByArticleIds(articleIds);
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

    @Override
    public ArticleVo get(String articleCode) {
        LambdaQueryWrapper<ArticleEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(articleCode.contains("-")) { //兼容历史的文章路径，防止历史的文章路径SEO失效
            articleCode = articleCode.startsWith("/") ? articleCode : "/" + articleCode;
            queryWrapper.eq(ArticleEntity::getPath, articleCode)
                    .eq(ArticleEntity::getReleased, StatusEnum.YES.getCode())
                    .eq(ArticleEntity::getDeleted, DeletedEnum.FALSE.getCode())
                    .last("limit 1");
        } else {
            long articleId = idConfusionService.decode(articleCode);
            queryWrapper.eq(ArticleEntity::getId, articleId)
                    .eq(ArticleEntity::getReleased, StatusEnum.YES.getCode())
                    .eq(ArticleEntity::getDeleted, DeletedEnum.FALSE.getCode())
                    .last("limit 1");
        }
        ArticleEntity articleEntity = this.getOne(queryWrapper);
        if(articleEntity == null) {
            throw new Page404Exception("文章不存在");
        }

        ArticleVo articleVo = BeanCopyUtils.copy(articleEntity, ArticleVo.class);
        articleVo.setUrl(articlePathPrefix + articleEntity.getPath());
        articleVo.setUpdateTime(articleEntity.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        List<CategoryDto> categoryDtos = categoryService.getByArticleIds(Collections.singletonList(articleEntity.getId()));
        if(CollectionUtil.isNotEmpty(categoryDtos)) {
            articleVo.setCategoryNames(categoryDtos.stream().map(CategoryDto::getName).collect(Collectors.joining(",")));
        }
        List<AuthorDto> authorDtos = authorService.getByArticleIds(Collections.singletonList(articleEntity.getId()));
        if(CollectionUtil.isNotEmpty(authorDtos)) {
            articleVo.setAuthorNames(authorDtos.stream().map(AuthorDto::getNickName).collect(Collectors.joining(",")));
        }

        List<CommentVo> commentVos = commentService.getTopByArticle(articleEntity.getId());
        articleVo.setCommentVos(commentVos);

        return articleVo;
    }

    @Override
    public ArticleVo get(String categoryCode, String articleCode) {
        LambdaQueryWrapper<CategoryEntity> categoryQuery = new LambdaQueryWrapper<>();
        categoryQuery.eq(CategoryEntity::getCode, categoryCode);
        categoryQuery.eq(CategoryEntity::getDeleted, DeletedEnum.FALSE.getCode());
        categoryQuery.eq(CategoryEntity::getStatus, StatusEnum.YES.getCode());
        long count = categoryService.count(categoryQuery);
        if(count == 0) {
            throw new Page404Exception("分类编码不存在");
        }

        //历史文章的完整路径：分类编码 + 文章编码
        String path = "/" +categoryCode + "/" + articleCode;
        return get(path);
    }
}
