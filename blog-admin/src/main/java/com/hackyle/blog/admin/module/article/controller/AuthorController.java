package com.hackyle.blog.admin.module.article.controller;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.AuthorAddDto;
import com.hackyle.blog.admin.module.article.model.dto.AuthorQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.AuthorUpdateDto;
import com.hackyle.blog.admin.module.article.model.vo.AuthorVo;
import com.hackyle.blog.admin.module.article.service.impl.AuthorServiceImpl;
import com.hackyle.blog.common.constant.RegexPatternConstants;
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
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/article/author")
public class AuthorController {
    @Autowired
    private AuthorServiceImpl authorService;

    /**
     * 新增作者
     */
    @PostMapping
    public ApiResponse<?> add(@Validated @RequestBody AuthorAddDto authorAddDto) {
        log.info("新增作者-Controller层入参-authorAddDto={}", JSON.toJSONString(authorAddDto));

        boolean status = authorService.add(authorAddDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    /**
     * 删除作者
     */
    @DeleteMapping
    public ApiResponse<String> del(@RequestParam("ids") String ids) {
        log.info("删除作者-Controller层入参-ids={}", ids);

        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }

        boolean status = authorService.del(idSet);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    /**
     * 修改作者
     */
    @PutMapping
    public ApiResponse<String> update(@Validated @RequestBody AuthorUpdateDto updateDto) {
        log.info("修改作者-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));

        boolean status = authorService.update(updateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    /**
     * 作者详情
     */
    @GetMapping
    public ApiResponse<AuthorVo> get(@RequestParam("id") String id) {
        log.info("获取作者详情-controller层入参-id={}", id);

        AuthorVo authorVo = authorService.get(id);
        return ApiResponse.ok(authorVo);
    }

    /**
     * 分页获取所有作者
     */
    @PostMapping("/list")
    public ApiResponse<PageInfo<AuthorVo>> list(@RequestBody AuthorQueryDto queryDto) {
        log.info("获取所有作者-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<AuthorVo> authorVoList = authorService.list(queryDto);
        return ApiResponse.ok(authorVoList);
    }

    /**
     * 获取所有作者
     */
    @GetMapping("/all")
    public ApiResponse<List<AuthorVo>> all() {
        List<AuthorVo> authorVoList = authorService.all();
        return ApiResponse.ok(authorVoList);
    }
}
