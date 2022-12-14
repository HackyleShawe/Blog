package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.common.constant.RegexVerificationCons;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ArticleAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.ArticleEntity;
import com.hackyle.blog.business.entity.CategoryEntity;
import com.hackyle.blog.business.mapper.ArticleMapper;
import com.hackyle.blog.business.mapper.CategoryMapper;
import com.hackyle.blog.business.po.ArticleAuthorPo;
import com.hackyle.blog.business.po.ArticleCategoryPo;
import com.hackyle.blog.business.po.ArticleTagPo;
import com.hackyle.blog.business.qo.ArticleQo;
import com.hackyle.blog.business.service.*;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
import com.hackyle.blog.business.util.PaginationUtils;
import com.hackyle.blog.business.vo.ArticleVo;
import com.hackyle.blog.business.vo.AuthorVo;
import com.hackyle.blog.business.vo.CategoryVo;
import com.hackyle.blog.business.vo.TagVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Transactional
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity>
        implements ArticleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);
    private static final Pattern patternRUI = Pattern.compile(RegexVerificationCons.URI);

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleAuthorService articleAuthorService;
    @Autowired
    private ArticleCategoryService articleCategoryService;
    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CommentService commentService;

    @Transactional
    @Override
    public ApiResponse<String> add(ArticleAddDto articleAddDto) throws Exception {
        checkAndAdjustURI(articleAddDto);

        ArticleEntity articleEntity = BeanCopyUtils.copy(articleAddDto, ArticleEntity.class);

        //???????????????????????????????????????URI??????????????????
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uri", articleEntity.getUri());
        ArticleEntity checkArticleEntity = articleMapper.selectOne(queryWrapper);

        if(checkArticleEntity != null) {
            //????????????????????????????????????
            articleAddDto.setId(IDUtils.encryptByAES(checkArticleEntity.getId()));
            return update(articleAddDto);

        } else {
            //????????????????????????????????????
            articleEntity.setId(IDUtils.timestampID());
            int inserted = articleMapper.insert(articleEntity);
            if(inserted != 1) {
                throw new RuntimeException("??????????????????");
            }

            if(StringUtils.isNotBlank(articleAddDto.getAuthorIds())) {
                List<String> authorIdList = Arrays.asList(articleAddDto.getAuthorIds().split(","));
                List<Long> authorIds = IDUtils.decrypt(authorIdList);
                articleAuthorService.batchInsert(articleEntity.getId(), authorIds);
            }
            if(StringUtils.isNotBlank(articleAddDto.getCategoryIds())) {
                List<String> categoryIdList = Arrays.asList(articleAddDto.getCategoryIds().split(","));
                List<Long> categoryIds = IDUtils.decrypt(categoryIdList);
                articleCategoryService.batchInsert(articleEntity.getId(), categoryIds);
            }
            if(StringUtils.isNotBlank(articleAddDto.getTagIds())) {
                List<Long> tagIds = IDUtils.decrypt(Arrays.asList(articleAddDto.getTagIds().split(",")));
                articleTagService.batchInsert(articleEntity.getId(), tagIds);
            }
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Transactional
    @Override
    public ApiResponse<String> del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(IDUtils.decryptByAES(idStr));
        }
        articleMapper.logicDeleteByIds(idList);

        //??????????????????????????????????????????????????????????????????????????????????????????????????????
        //articleAuthorService.logicDelByArticleIds(idList);
        //articleCategoryService.logicDelByArticleIds(idList);
        //articleTagService.logicDelByArticleIds(idList);
        //commentService.delByTargetIds(idList);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Transactional
    @Override
    public ApiResponse<String> delReal(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> articleIds = new ArrayList<>();
        for (String idStr : idSplit) {
            articleIds.add(IDUtils.decryptByAES(idStr));
        }

        this.removeByIds(articleIds);

        //???????????????????????????????????????????????????????????????????????????????????????
        articleAuthorService.batchDelByArticleIds(articleIds);
        articleCategoryService.batchDelByArticleIds(articleIds);
        articleTagService.batchDelByArticleIds(articleIds);
        //commentService.delByTargetIds(idList);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Transactional
    @Override
    public ApiResponse<String> update(ArticleAddDto articleUpdateDto) {
        adjustURI(articleUpdateDto);

        ArticleEntity articleEntity = BeanCopyUtils.copy(articleUpdateDto, ArticleEntity.class);
        IDUtils.decrypt(articleUpdateDto, articleEntity);

        UpdateWrapper<ArticleEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ArticleEntity::getId, articleEntity.getId());
        int update = articleMapper.update(articleEntity, updateWrapper);
        if(update != 1) {
            throw new RuntimeException("??????????????????");
        }

        long articleId = articleEntity.getId();

        if(StringUtils.isNotBlank(articleUpdateDto.getAuthorIds())) {
            List<Long> authorIds = IDUtils.decrypt(Arrays.asList(articleUpdateDto.getAuthorIds().split(",")));
            articleAuthorService.update(articleId, authorIds);
        }
        if(StringUtils.isNotBlank(articleUpdateDto.getCategoryIds())) {
            List<Long> categoryIds = IDUtils.decrypt(Arrays.asList(articleUpdateDto.getCategoryIds().split(",")));
            articleCategoryService.update(articleId, categoryIds);
        }
        if(StringUtils.isNotBlank(articleUpdateDto.getTagIds())) {
            List<Long> tagIds = IDUtils.decrypt(Arrays.asList(articleUpdateDto.getTagIds().split(",")));
            articleTagService.update(articleId, tagIds);
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public ArticleVo fetch(String id) {
        long idd = IDUtils.decryptByAES(id);

        ArticleEntity articleEntity = articleMapper.selectById(idd);

        ArticleVo articleEntityLog = BeanCopyUtils.copy(articleEntity, ArticleVo.class);
        articleEntityLog.setContent("?????????????????????"+articleEntityLog.getContent().length());
        LOGGER.info("????????????-??????-idd={}-?????????????????????-article={}", idd, JSON.toJSONString(articleEntityLog));

        ArticleVo articleVo = BeanCopyUtils.copy(articleEntity, ArticleVo.class);
        articleVo.setId(IDUtils.encryptByAES(articleEntity.getId()));

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<ArticleAuthorPo>> authorMap = articleAuthorService.selectByArticleIds(articleIds);
        List<ArticleAuthorPo> articleAuthorPos = authorMap.get(idd);
        List<AuthorVo> authorVos = BeanCopyUtils.copyList(articleAuthorPos, AuthorVo.class);
        IDUtils.batchEncrypt(articleAuthorPos, "authorId", authorVos, "setId");
        articleVo.setAuthors(authorVos);

        Map<Long, List<ArticleCategoryPo>> categoryMap = articleCategoryService.selectByArticleIds(articleIds);
        List<ArticleCategoryPo> articleCategoryPos = categoryMap.get(idd);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyList(articleCategoryPos, CategoryVo.class);
        IDUtils.batchEncrypt(articleCategoryPos, "categoryId", categoryVos, "setId");
        articleVo.setCategories(categoryVos);

        Map<Long, List<ArticleTagPo>> tagMap = articleTagService.selectByArticleIds(articleIds);
        List<ArticleTagPo> articleTagPos = tagMap.get(idd);
        List<TagVo> tagVos = BeanCopyUtils.copyList(articleTagPos, TagVo.class);
        IDUtils.batchEncrypt(articleTagPos, "tagId", tagVos, "setId");
        articleVo.setTags(tagVos);

        return articleVo;
    }

    /**
     * ????????????????????????
     * @param pageRequestDto ????????????
     * @return ??????
     */
    @Override
    public PageResponseDto<ArticleVo> fetchList(PageRequestDto<ArticleQo> pageRequestDto) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply(" 1 = 1 ");

        //??????????????????
        ArticleQo articleQo = pageRequestDto.getCondition();
        if(articleQo != null) {
            if(null != articleQo.getReleased()) {
                queryWrapper.eq("is_released", articleQo.getReleased());
            }

            boolean deleted = articleQo.getDeleted() != null && articleQo.getDeleted();
            queryWrapper.eq("is_deleted", deleted);

            if(StringUtils.isNotBlank(articleQo.getTitle())) {
                queryWrapper.lambda().like(ArticleEntity::getTitle, articleQo.getTitle());
            }
            if(StringUtils.isNotBlank(articleQo.getSummary())) {
                queryWrapper.lambda().like(ArticleEntity::getSummary, articleQo.getSummary());
            }
        }
        queryWrapper.lambda().orderByDesc(ArticleEntity::getUpdateTime);

        //??????????????????
        Page<ArticleEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, ArticleEntity.class);
        Page<ArticleEntity> resultPage = articleMapper.selectPage(paramPage, queryWrapper);

        //?????????Content???????????????????????????URI??????
        String articleUris = resultPage.getRecords().stream().map(ArticleEntity::getUri).collect(Collectors.joining(","));
        LOGGER.info("??????????????????-??????-pageRequestDto={},??????-articleUris={}", JSON.toJSONString(pageRequestDto), articleUris);

        PageResponseDto<ArticleVo> articleVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, ArticleVo.class);
        List<ArticleEntity> articleEntityList = resultPage.getRecords();
        IDUtils.batchEncrypt(articleEntityList, articleVoPageResponseDto.getRows());
        List<Long> articleIdList = articleEntityList.stream().map(ArticleEntity::getId).collect(Collectors.toList());
        if(articleIdList.size() < 1) {
            return articleVoPageResponseDto;
        }

        Map<Long, List<ArticleAuthorPo>> authorMap = articleAuthorService.selectByArticleIds(articleIdList);
        Map<Long, List<ArticleCategoryPo>> categoryMap = articleCategoryService.selectByArticleIds(articleIdList);
        Map<Long, List<ArticleTagPo>> tagMap = articleTagService.selectByArticleIds(articleIdList);

        List<ArticleVo> articleVoList = articleVoPageResponseDto.getRows();
        for (ArticleVo articleVo : articleVoList) {
            long idd = IDUtils.decryptByAES(articleVo.getId());

            List<ArticleAuthorPo> articleAuthorPos = authorMap.get(idd);
            List<AuthorVo> authorVos = BeanCopyUtils.copyList(articleAuthorPos, AuthorVo.class);
            IDUtils.batchEncrypt(articleAuthorPos, "authorId", authorVos, "setId");
            articleVo.setAuthors(authorVos);

            List<ArticleCategoryPo> articleCategoryPos = categoryMap.get(idd);
            List<CategoryVo> categoryVos = BeanCopyUtils.copyList(articleCategoryPos, CategoryVo.class);
            IDUtils.batchEncrypt(articleCategoryPos, "categoryId", categoryVos, "setId");
            articleVo.setCategories(categoryVos);

            List<ArticleTagPo> articleTagPos = tagMap.get(idd);
            List<TagVo> tagVos = BeanCopyUtils.copyList(articleTagPos, TagVo.class);
            IDUtils.batchEncrypt(articleTagPos, "tagId", tagVos, "setId");
            articleVo.setTags(tagVos);
        }
        articleVoPageResponseDto.setRows(articleVoList);

        return articleVoPageResponseDto;
    }

    @Override
    public List<AuthorVo> fetchAuthor(String articleId) {
        long idd = IDUtils.decryptByAES(articleId);

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<ArticleAuthorPo>> articleMap = articleAuthorService.selectByArticleIds(articleIds);

        List<ArticleAuthorPo> articleAuthorPos = articleMap.get(idd);
        List<AuthorVo> authorVos = BeanCopyUtils.copyList(articleAuthorPos, AuthorVo.class);
        IDUtils.batchEncrypt(articleAuthorPos, "authorId", authorVos, "setId");

        return authorVos;
    }

    @Override
    public List<CategoryVo> fetchCategory(String articleId) {
        long idd = IDUtils.decryptByAES(articleId);

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<ArticleCategoryPo>> categoryMap = articleCategoryService.selectByArticleIds(articleIds);
        List<ArticleCategoryPo> articleCategoryPos = categoryMap.get(idd);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyList(articleCategoryPos, CategoryVo.class);
        IDUtils.batchEncrypt(articleCategoryPos, "categoryId", categoryVos, "setId");

        return categoryVos;
    }

    @Override
    public List<TagVo> fetchTag(String articleId) {
        long idd = IDUtils.decryptByAES(articleId);

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<ArticleTagPo>> tagMap = articleTagService.selectByArticleIds(articleIds);
        List<ArticleTagPo> articleTagPos = tagMap.get(idd);
        List<TagVo> tagVos = BeanCopyUtils.copyList(articleTagPos, TagVo.class);
        IDUtils.batchEncrypt(articleTagPos, "tagId", tagVos, "setId");

        return tagVos;
    }

    /**
     * ??????URI?????????
     * 1.????????????
     * 2.???????????????????????????????????????????????????
     * 3.????????????????????????URI??????????????????Code
     */
    private void checkAndAdjustURI(ArticleAddDto articleAddDto) {
        String uri = articleAddDto.getUri();

        if(StringUtils.isBlank(uri)) {
            return;
        }

        uri = uri.toLowerCase();

        //?????????????????????
        Matcher matcher = patternRUI.matcher(uri);
        if(!matcher.find()) {
            throw new RuntimeException("URI?????????");
        }

        if(!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        //URI????????????????????????????????????
        uri = uri.replaceAll(" ", "-");

        //???URI??????????????????????????????
        String categoryIds = articleAddDto.getCategoryIds();
        if(StringUtils.isNotBlank(categoryIds) && categoryIds.split(",").length >= 1) {
            String[] categoryIdArr = categoryIds.split(",");

            long decryptedCategoryId = IDUtils.decryptByAES(categoryIdArr[0]);
            CategoryEntity categoryEntity = categoryMapper.selectById(decryptedCategoryId);
            if(categoryEntity != null && StringUtils.isNotBlank(categoryEntity.getCode())) {
                uri = "/" + categoryEntity.getCode().toLowerCase() + uri;
            }
        }

        articleAddDto.setUri(uri);
    }

    /**
     * ??????URI?????????
     * 1.????????????
     * 2.???????????????????????????????????????????????????
     */
    private void adjustURI(ArticleAddDto articleAddDto) {
        if(StringUtils.isBlank(articleAddDto.getUri())) {
            return;
        }

        String uri = articleAddDto.getUri();
        uri = uri.toLowerCase();

        //?????????????????????
        Matcher matcher = patternRUI.matcher(uri);
        if(!matcher.find()) {
            throw new RuntimeException("URI?????????");
        }

        if(!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        //URI????????????????????????????????????
        uri = uri.replaceAll(" ", "-");

        articleAddDto.setUri(uri);
    }


}
