package com.hackyle.blog.admin.module.article.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.CategoryAddDto;
import com.hackyle.blog.admin.module.article.model.dto.CategoryQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.CategoryUpdateDto;
import com.hackyle.blog.admin.module.article.model.vo.CategoryVo;
import com.hackyle.blog.admin.module.article.service.CategoryService;
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
@RequestMapping("/article/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ApiResponse<?> add(@Validated @RequestBody CategoryAddDto categoryAddDto) {
        log.info("新增文章分类-Controller层入参-categoryAddDto={}", JSON.toJSONString(categoryAddDto));

        boolean status =  categoryService.add(categoryAddDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @DeleteMapping
    public ApiResponse<?> del(@RequestParam("ids")String ids) {
        log.info("删除文章分类-Controller层入参-ids={}", ids);
        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }

        boolean status =  categoryService.del(idSet);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CategoryUpdateDto categoryUpdateDto) {
        log.info("修改文章分类-Controller层入参-categoryUpdateDto={}", JSON.toJSONString(categoryUpdateDto));
        boolean status = categoryService.update(categoryUpdateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @GetMapping
    public ApiResponse<CategoryVo> get(@RequestParam("id") String id) {
        log.info("获取文章分类详情-Controller层入参-id={}", id);

        if(id == null || StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("入参不能为空");
        }

        CategoryVo categoryVo = categoryService.get(id);
        return ApiResponse.ok(categoryVo);
    }

    /**
     * 根据查询条件获取文章分类
     */
    @PostMapping("/list")
    public ApiResponse<PageInfo<CategoryVo>> list(@RequestBody CategoryQueryDto queryDto) {
        log.info("根据查询条件获取文章分类-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<CategoryVo> pageInfo = categoryService.list(queryDto);
        return ApiResponse.ok(pageInfo);
    }

    /**
     * 获取所有文章分类
     */
    @GetMapping("/all")
    public ApiResponse<List<CategoryVo>> all() {
        List<CategoryVo> categoryVoList = categoryService.all();
        return ApiResponse.ok(categoryVoList);
    }
}
