package com.hackyle.blog.admin.module.article.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.mapper.ArticleAuthorMapper;
import com.hackyle.blog.admin.module.article.mapper.ArticleCategoryMapper;
import com.hackyle.blog.admin.module.article.mapper.ArticleMapper;
import com.hackyle.blog.admin.module.article.model.dto.ArticleAddDto;
import com.hackyle.blog.admin.module.article.model.dto.ArticleAuthorRelationDto;
import com.hackyle.blog.admin.module.article.model.dto.ArticleCategoryRelationDto;
import com.hackyle.blog.admin.module.article.model.dto.ArticleQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.ArticleUpdateDto;
import com.hackyle.blog.admin.module.article.model.entity.ArticleAuthorRelationEntity;
import com.hackyle.blog.admin.module.article.model.entity.ArticleCategoryRelationEntity;
import com.hackyle.blog.admin.module.article.model.entity.ArticleEntity;
import com.hackyle.blog.admin.module.article.model.entity.AuthorEntity;
import com.hackyle.blog.admin.module.article.model.entity.CategoryEntity;
import com.hackyle.blog.admin.module.article.model.vo.ArticleListVo;
import com.hackyle.blog.admin.module.article.model.vo.ArticleVo;
import com.hackyle.blog.admin.module.article.model.vo.AuthorVo;
import com.hackyle.blog.admin.module.article.model.vo.CategoryVo;
import com.hackyle.blog.admin.module.article.service.ArticleService;
import com.hackyle.blog.admin.module.article.service.AuthorService;
import com.hackyle.blog.admin.module.article.service.CategoryService;
import com.hackyle.blog.admin.module.article.service.CommentService;
import com.hackyle.blog.admin.module.article.service.FileService;
import com.hackyle.blog.admin.module.article.service.IdConfusionService;
import com.hackyle.blog.common.constant.RegexPatternConstants;
import com.hackyle.blog.common.enums.DeletedEnum;
import com.hackyle.blog.common.enums.StatusEnum;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import com.hackyle.blog.common.util.SnowFlakeIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity> implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private IdConfusionService idConfusionService;
    @Autowired
    private ArticleAuthorMapper articleAuthorMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;
    @Autowired
    private FileService fileService;


    private static final Pattern URL_PATTERN = Pattern.compile(RegexPatternConstants.URI);

    /**
     * 文章新增成功后，返回文章id，方便保存时的唯一性
     * 抽取方法，按照这类格式：[stage 1]入参检查；[stage 2]计算和转换；[stage 3]落表，最后一步落表可以事务
     * @return 文章id
     */
    public String add(ArticleAddDto addDto) {
        addArticleCheck(addDto);

        //存在性检查
        if(addDto.getId() != null) {
            ArticleEntity existsArticle = this.getById(addDto.getId());
            if(existsArticle != null) { //存在走update逻辑
                ArticleUpdateDto updateDto = BeanCopyUtils.copy(addDto, ArticleUpdateDto.class);
                this.update(updateDto);
                return String.valueOf(existsArticle.getId());
            }
        }

        ArticleEntity articleEntity = BeanCopyUtils.copy(addDto, ArticleEntity.class);
        articleEntity.setId(SnowFlakeIdUtils.getInstance().nextId());

        //文章路径生成
        String path = pathGenerator(articleEntity.getId(), addDto.getCategoryIds());
        articleEntity.setPath(path);

        //解析文章中的图片文件
        //TODO 目前只支持解析图片，其他文件需要手动上传添加到文章内容中
        Set<String> imgUrls = imgParse(addDto.getContent());
        addDto.setImgUrls(imgUrls);

        // 使用代理调用 -> 事务生效
        ((ArticleService) AopContext.currentProxy()).addArticleSave(addDto, articleEntity);

        return String.valueOf(articleEntity.getId());
    }

    private Set<String> imgParse(String articleContent) {
        if(StringUtils.isBlank(articleContent)) {
            return Collections.emptySet();
        }

        Set<String> imgSet = new HashSet<>(); //存储图片链接
        Document doc = Jsoup.parse(articleContent);
        Elements imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            String imgLink = img.attr("src");
            if(StringUtils.isNotBlank(imgLink)) {
                imgSet.add(imgLink);
            }
        }

        return imgSet;
    }

    /**
     * 新增文章，落库。注意事务失效
     */
    @Transactional(rollbackFor = Exception.class)
    public void addArticleSave(ArticleAddDto addDto, ArticleEntity articleEntity) {
        boolean save = this.save(articleEntity);
        if(!save) {
            throw new BizException("新增文章失败");
        }
        if(StringUtils.isNotBlank(addDto.getCategoryIds())) {
            Set<Long> categoryIdSet = new HashSet<>();
            String[] categoryArr = addDto.getCategoryIds().split(",");
            for (String category : categoryArr) {
                categoryIdSet.add(Long.parseLong(category));
            }
            int insert = articleCategoryMapper.batchInsert(articleEntity.getId(), categoryIdSet);
            if(insert < 1) {
                throw new BizException("文章分类新增失败");
            }
        }
        if(StringUtils.isNotBlank(addDto.getAuthorIds())) {
            Set<Long> authorIdSet = new HashSet<>();
            String[] authorArr = addDto.getAuthorIds().split(",");
            for (String author : authorArr) {
                authorIdSet.add(Long.parseLong(author));
            }
            int insert = articleAuthorMapper.batchInsert(articleEntity.getId(), authorIdSet);
            if(insert < 1) {
                throw new BizException("文章分类新增失败");
            }
        }

        //保存文章中的静态图片资源
        if(CollectionUtil.isNotEmpty(addDto.getImgUrls())) {
            boolean saved = fileService.saveImgFile(articleEntity.getId(), addDto.getImgUrls());
            if(!saved) {
                throw new BizException("保存文章中的图片失败");
            }

        }
    }

    /**
     * 文章链接path = id混淆
     */
    private String pathGenerator(long articleId, String categoryIds) {
        //不要把文章分类ID作为一部分，因为文章分类可能会变
        //if (StringUtils.isNotBlank(categoryIds)) {
        //    String[] categoryArr = categoryIds.split(",");
        //    //取分类的第一个作为文章链接path的前缀
        //    String categoryId = categoryArr[0];
        //    com.hackyle.blog.admin.module.article.model.entity.CategoryEntity categoryEntity = categoryService.getById(categoryId);
        //    String code = categoryEntity.getCode();
        //    path += "/" + code;
        //    Matcher matcher = URL_PATTERN.matcher(path);
        //    if (!matcher.find()) {
        //        throw new IllegalArgumentException("PATH不符合URL格式，请检查所选第一个文章分类code是否合法");
        //    }
        //}
        String idConfusion = idConfusionService.encode(articleId);
        String path = "/" + idConfusion;
        Matcher matcher = URL_PATTERN.matcher(path);
        if (!matcher.find()) {
            throw new IllegalArgumentException("PATH不符合URL格式，请检查id混淆规则是否合法");
        }
        return path;
    }


    private void addArticleCheck(ArticleAddDto addDto) {
        //path不允许修改，在新增文章时自动生成，唯一确定
        //检查path是否符合URL格式
        //Matcher matcher = URL_PATTERN.matcher(addDto.getPath());
        //if(!matcher.find()) {
        //    throw new IllegalArgumentException("PATH不符合URL格式");
        //}
        //if(!addDto.getPath().startsWith("/")) {
        //    String path = "/" + addDto.getPath();
        //    addDto.setPath(path);
        //}

        //检查作者是否存在
        Set<String> authorIds = Arrays.stream(addDto.getAuthorIds().split(",")).collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(authorIds)) {
            throw new IllegalArgumentException("作者不能为空");
        }
        long authorCount = authorService.count(Wrappers.<AuthorEntity>lambdaQuery()
                .in(AuthorEntity::getId, authorIds)
                .eq(AuthorEntity::getDeleted, DeletedEnum.FALSE.getCode())
                .eq(AuthorEntity::getStatus, StatusEnum.YES.getCode()));
        if (authorCount <= 0 || authorCount != authorIds.size()) {
            throw new IllegalArgumentException("部分作者不存在");
        }

        //检查分类是否存在
        if (StringUtils.isNotBlank(addDto.getCategoryIds())) {
            Set<String> categoryIds = Arrays.stream(addDto.getCategoryIds().split(",")).collect(Collectors.toSet());
            if (CollectionUtil.isEmpty(categoryIds)) {
                throw new IllegalArgumentException("分类ID解析失败");
            }
            long categoryCount = categoryService.count(Wrappers.<com.hackyle.blog.admin.module.article.model.entity.CategoryEntity>lambdaQuery()
                    .in(com.hackyle.blog.admin.module.article.model.entity.CategoryEntity::getId, categoryIds)
                    .eq(com.hackyle.blog.admin.module.article.model.entity.CategoryEntity::getDeleted, DeletedEnum.FALSE.getCode())
                    .eq(com.hackyle.blog.admin.module.article.model.entity.CategoryEntity::getStatus, StatusEnum.YES.getCode()));
            if (categoryCount <= 0 || categoryCount != categoryIds.size()) {
                throw new IllegalArgumentException("部分分类不存在");
            }
        }

    }

    @Transactional
    @Override
    public boolean del(Set<Long> idSet) {
        boolean articleDel = this.removeBatchByIds(idSet);
        if(!articleDel) {
            throw new BizException("文章删除失败");
        }

        //文章不一定有分类，所以没法判断删除文章分类是否成功
        articleCategoryMapper.deleteByArticleIds(idSet);

        //文章一定有作者，需要判断删除作者是否成功
        int articleAuthorDel = articleAuthorMapper.deleteByArticleIds(idSet);
        if(articleAuthorDel < 1) {
            throw new BizException("文章作者删除失败");
        }

        //删除评论，注意不一定有评论
        commentService.deleteByArticleIds(idSet);

        return true;
    }

    @Override
    public boolean update(ArticleUpdateDto updateDto) {
        updateArticleCheck(updateDto);

        //解析文章中的图片文件
        //TODO 目前只支持解析图片，其他文件需要手动上传添加到文章内容中
        Set<String> imgUrls = imgParse(updateDto.getContent());
        updateDto.setImgUrls(imgUrls);

        // 使用代理调用 -> 事务生效
        ((ArticleService) AopContext.currentProxy()).updateArticleSave(updateDto);

        return true;
    }

    @Transactional
    @Override
    public void updateArticleSave(ArticleUpdateDto updateDto) {
        ArticleEntity articleEntity = BeanCopyUtils.copy(updateDto, ArticleEntity.class);

        boolean updated = this.updateById(articleEntity);
        if(!updated) {
            throw new BizException("更新文章失败");
        }

        Long articleId = articleEntity.getId();
        if(StringUtils.isNotBlank(updateDto.getAuthorIds())) {
            Set<Long> articelIdSet = new HashSet<>();
            articelIdSet.add(articleId);
            int del = articleAuthorMapper.deleteByArticleIds(articelIdSet);
            if(del < 1) { //一篇文章肯定有作者，所以这里一定有值
                throw new BizException("删除文章的作者失败");
            }
            Set<Long> authorIds = Arrays.stream(updateDto.getAuthorIds().split(","))
                    .map(Long::parseLong).collect(Collectors.toSet());
            int insert = articleAuthorMapper.batchInsert(articleId, authorIds);
            if(insert < 1) {
                throw new BizException("更新文章作者时失败");
            }
        }

        if(StringUtils.isNotBlank(updateDto.getCategoryIds())) {
            Set<Long> articelIdSet = new HashSet<>();
            articelIdSet.add(articleId);
            int del = articleCategoryMapper.deleteByArticleIds(articelIdSet);
            //if(del < 1) {//文章的分类不是必填，这里不能强判断
            //    throw new BizException("删除文章的分类失败");
            //}
            Set<Long> categoryIds = Arrays.stream(updateDto.getCategoryIds().split(","))
                    .map(Long::parseLong).collect(Collectors.toSet());
            int insert = articleCategoryMapper.batchInsert(articleId, categoryIds);
            if(insert < 1) {
                throw new BizException("更新文章分类新增失败");
            }
        }

        //保存文章中的静态图片资源
        if(CollectionUtil.isNotEmpty(updateDto.getImgUrls())) {
            boolean saved = fileService.saveImgFile(articleEntity.getId(), updateDto.getImgUrls());
            if(!saved) {
                throw new BizException("保存文章中的图片失败");
            }
        }
    }

    private void updateArticleCheck(ArticleUpdateDto updateDto) {
        //检查文章是否存在
        ArticleEntity articleEntity = this.getById(updateDto.getId());
        if(articleEntity == null) {
            throw new IllegalArgumentException("文章不存在");
        }

        //检查作者是否存在
        if (StringUtils.isNotBlank(updateDto.getAuthorIds())) {
            Set<String> authorIds = Arrays.stream(updateDto.getAuthorIds().split(",")).collect(Collectors.toSet());
            if (CollectionUtil.isEmpty(authorIds)) {
                throw new IllegalArgumentException("作者不能为空");
            }
            long authorCount = authorService.count(Wrappers.<AuthorEntity>lambdaQuery()
                    .in(AuthorEntity::getId, authorIds)
                    .eq(AuthorEntity::getDeleted, DeletedEnum.FALSE.getCode())
                    .eq(AuthorEntity::getStatus, StatusEnum.YES.getCode()));
            if (authorCount <= 0 || authorCount != authorIds.size()) {
                throw new IllegalArgumentException("部分作者不存在");
            }
        }

        //检查分类是否存在
        if (StringUtils.isNotBlank(updateDto.getCategoryIds())) {
            Set<String> categoryIds = Arrays.stream(updateDto.getCategoryIds().split(",")).collect(Collectors.toSet());
            if (CollectionUtil.isEmpty(categoryIds)) {
                throw new IllegalArgumentException("分类ID解析失败");
            }
            long categoryCount = categoryService.count(Wrappers.<com.hackyle.blog.admin.module.article.model.entity.CategoryEntity>lambdaQuery()
                    .in(com.hackyle.blog.admin.module.article.model.entity.CategoryEntity::getId, categoryIds)
                    .eq(com.hackyle.blog.admin.module.article.model.entity.CategoryEntity::getDeleted, DeletedEnum.FALSE.getCode())
                    .eq(com.hackyle.blog.admin.module.article.model.entity.CategoryEntity::getStatus, StatusEnum.YES.getCode()));
            if (categoryCount <= 0 || categoryCount != categoryIds.size()) {
                throw new IllegalArgumentException("部分分类不存在");
            }
        }
    }

    @Override
    public ArticleVo get(String id) {
        ArticleEntity articleEntity = this.getById(id);
        if(articleEntity == null) {
            throw new BizException("文章不存在");
        }

        ArticleVo articleVo = BeanCopyUtils.copy(articleEntity, ArticleVo.class);

        Long articleId = articleEntity.getId();
        List<ArticleAuthorRelationEntity> authors = articleAuthorMapper.selectList(Wrappers
                .<ArticleAuthorRelationEntity>lambdaQuery()
                .eq(ArticleAuthorRelationEntity::getArticleId, articleId));
        if(CollectionUtil.isNotEmpty(authors)) {
            List<Long> authorIds = authors.stream().map(ArticleAuthorRelationEntity::getAuthorId).toList();
            List<AuthorEntity> authorEntities = authorService.listByIds(authorIds);
            List<AuthorVo> authorVos = BeanCopyUtils.copyList(authorEntities, AuthorVo.class);
            articleVo.setAuthors(authorVos);
        }
        List<ArticleCategoryRelationEntity> categorys = articleCategoryMapper.selectList(Wrappers.
                <ArticleCategoryRelationEntity>lambdaQuery()
                .eq(ArticleCategoryRelationEntity::getArticleId, articleId));
        if(CollectionUtil.isNotEmpty(categorys)) {
            List<Long> categoryIds = categorys.stream().map(ArticleCategoryRelationEntity::getCategoryId).toList();
            List<CategoryEntity> categoryEntities = categoryService.listByIds(categoryIds);
            List<CategoryVo> categoryVos = BeanCopyUtils.copyList(categoryEntities, CategoryVo.class);
            articleVo.setCategories(categoryVos);
        }

        return articleVo;
    }

    @Override
    public PageInfo<ArticleListVo> list(ArticleQueryDto queryDto) {
        LambdaQueryWrapper<ArticleEntity> queryWrapper = Wrappers.<ArticleEntity>lambdaQuery();
        if(queryDto.getDeleted() != null) {
            queryWrapper.eq(ArticleEntity::getDeleted, queryDto.getDeleted());
        }
        if(queryDto.getReleased() != null) {
            queryWrapper.eq(ArticleEntity::getReleased, queryDto.getReleased());
        }
        if(queryDto.getCommented() != null) {
            queryWrapper.eq(ArticleEntity::getCommented, queryDto.getCommented());
        }
        if(StringUtils.isNotBlank(queryDto.getTitle())) {
            queryWrapper.like(ArticleEntity::getTitle, queryDto.getTitle());
        }

        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        List<ArticleEntity> articles = this.list(queryWrapper);

        PageInfo<ArticleListVo> pageInfo = PageHelperUtils.getPageInfo(articles, ArticleListVo.class);
        List<ArticleListVo> articleListVoList = pageInfo.getList();

        Set<Long> articleIds = articleListVoList.stream().map(ArticleListVo::getId).collect(Collectors.toSet());
        //查文章的作者
        Map<Long, String> articleAuthorMap = getAuthorByArticleId(articleIds);
        //查文章的分类
        Map<Long, String> articleCategoryMap = getCategoryByArticleId(articleIds);

        for (ArticleListVo articleVo : articleListVoList) {
            //没有进行更新操作时，updateTime值为null，设置为createTime
            if(articleVo.getUpdateTime() == null) {
                articleVo.setUpdateTime(articleVo.getCreateTime());
            }

            String authorName = articleAuthorMap.get(articleVo.getId());
            articleVo.setAuthorName(StringUtil.isBlank(authorName)? "" : authorName);
            String categoryName = articleCategoryMap.get(articleVo.getId());
            articleVo.setCategoryName(StringUtil.isBlank(categoryName)? "" : categoryName);
        }

        return pageInfo;
    }

    private Map<Long, String> getAuthorByArticleId(Set<Long> articleIds) {
        if(CollectionUtil.isEmpty(articleIds)) {
            return Collections.emptyMap();
        }
        List<ArticleAuthorRelationDto> authors = articleAuthorMapper.getAuthorByArticleId(articleIds);
        if(CollectionUtil.isEmpty(authors)) {
            return Collections.emptyMap();
        }

        Map<Long, String> res = new HashMap<>();
        Map<Long, List<ArticleAuthorRelationDto>> articleAuthorMap = authors.stream().collect(Collectors.groupingBy(ArticleAuthorRelationDto::getArticleId));
        articleAuthorMap.forEach((articleId, authorList) -> {
            res.put(articleId, authorList.stream().map(ArticleAuthorRelationDto::getNickName).collect(Collectors.joining(",")));
        });

        return res;
    }

    private Map<Long, String> getCategoryByArticleId(Set<Long> articleIds) {
        if(CollectionUtil.isEmpty(articleIds)) {
            return Collections.emptyMap();
        }
        List<ArticleCategoryRelationDto> categories = articleCategoryMapper.getCategoryByArticleId(articleIds);
        if(CollectionUtil.isEmpty(categories)) {
            return Collections.emptyMap();
        }

        Map<Long, String> res = new HashMap<>();
        Map<Long, List<ArticleCategoryRelationDto>> articleAuthorMap = categories.stream().collect(Collectors.groupingBy(ArticleCategoryRelationDto::getArticleId));
        articleAuthorMap.forEach((articleId, authorList) -> {
            res.put(articleId, authorList.stream().map(ArticleCategoryRelationDto::getName).collect(Collectors.joining(",")));
        });

        return res;
    }
}
