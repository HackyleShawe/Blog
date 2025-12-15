package com.hackyle.blog.admin.module.article.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.CommentAddDto;
import com.hackyle.blog.admin.module.article.model.dto.CommentQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.CommentUpdateDto;
import com.hackyle.blog.admin.module.article.model.vo.CommentVo;
import com.hackyle.blog.admin.module.article.service.CommentService;
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
@RequestMapping("/article/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ApiResponse<?> add(@Validated @RequestBody CommentAddDto addDto) {
        log.info("新增评论-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        boolean status = commentService.add(addDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @DeleteMapping
    public ApiResponse<?> del(@RequestParam("ids")String ids) {
        log.info("删除评论-Controller层入参-ids={}", ids);
        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }

        boolean status =  commentService.del(idSet);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CommentUpdateDto updateDto) {
        log.info("修改评论-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));
        boolean status = commentService.update(updateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    /**
     * 获取某条评论的详情
     */
    @GetMapping
    public ApiResponse<CommentVo> get(@RequestParam("id") Long id) {
        log.info("获取评论详情-Controller层入参-id={}", id);

        if(id == null || id <= 0) {
            throw new IllegalArgumentException("入参不能为空");
        }

        CommentVo commentVo = commentService.get(id);
        return ApiResponse.ok(commentVo);
    }

    /**
     * 获取评论列表（不是树结构）
     */
    @PostMapping("/list")
    public ApiResponse<PageInfo<CommentVo>> list(@RequestBody CommentQueryDto queryDto) {
        log.info("根据查询条件获取评论-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<CommentVo> commentVos = commentService.list(queryDto);
        return ApiResponse.ok(commentVos);
    }

    /**
     * 获取根评论列表
     */
    @PostMapping("/rootList")
    public ApiResponse<PageInfo<CommentVo>> rootList(@RequestBody CommentQueryDto queryDto) {
        log.info("获取根评论列表-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<CommentVo> commentVos = commentService.rootList(queryDto);
        return ApiResponse.ok(commentVos);
    }

    /**
     * 获取某条根评论下的所有子评论
     */
    @GetMapping("getByRoot")
    public ApiResponse<List<CommentVo>> getByRoot(@RequestParam("rootId") Long rootId) {
        log.info("获取某条根评论下的所有子评论-rootId={}", rootId);

        if(rootId == null || rootId <= 0) {
            throw new IllegalArgumentException("入参不能为空");
        }

        List<CommentVo> commentVos = commentService.getByRoot(rootId);
        return ApiResponse.ok(commentVos);
    }

}
