package com.hackyle.blog.admin.module.article.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.ArticleAddDto;
import com.hackyle.blog.admin.module.article.model.dto.ArticleQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.ArticleUpdateDto;
import com.hackyle.blog.admin.module.article.model.vo.ArticleListVo;
import com.hackyle.blog.admin.module.article.model.vo.ArticleVo;
import com.hackyle.blog.admin.module.article.service.ArticleService;
import com.hackyle.blog.common.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 新增文章
     */
    @PostMapping
    public ApiResponse<String> add(@Validated @RequestBody ArticleAddDto addDto) {
        log.info("新增文章-Controller层入参-authorAddDto={}", JSON.toJSONString(addDto));

        String articleId = articleService.add(addDto);
        return ApiResponse.ok("文章新增成功", articleId);
    }

    /**
     * 删除文章
     */
    @DeleteMapping
    public ApiResponse<String> del(@RequestParam("ids") String ids) {
        log.info("删除文章-Controller层入参-ids={}", ids);

        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }
        if(CollectionUtil.isEmpty(idSet)) {
            throw new IllegalArgumentException("入参id解析错误，请检查");
        }

        boolean status = articleService.del(idSet);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    /**
     * 修改文章
     */
    @PutMapping
    public ApiResponse<String> update(@Validated @RequestBody ArticleUpdateDto updateDto) {
        log.info("修改文章-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));

        boolean status = articleService.update(updateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    /**
     * 文章详情
     */
    @GetMapping
    public ApiResponse<ArticleVo> get(@RequestParam("id") String id) {
        log.info("获取文章详情-controller层入参-id={}", id);

        ArticleVo articleVo = articleService.get(id);
        return ApiResponse.ok(articleVo);
    }

    /**
     * 分页获取所有文章
     */
    @PostMapping("/list")
    public ApiResponse<PageInfo<ArticleListVo>> list(@RequestBody ArticleQueryDto queryDto) {
        log.info("获取所有文章-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<ArticleListVo> articleVoList = articleService.list(queryDto);
        return ApiResponse.ok(articleVoList);
    }

}
