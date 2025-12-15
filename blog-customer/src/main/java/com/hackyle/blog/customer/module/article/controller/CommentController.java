package com.hackyle.blog.customer.module.article.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.common.domain.ApiResponse;
import com.hackyle.blog.customer.module.article.model.dto.CommentAddDto;
import com.hackyle.blog.customer.module.article.model.vo.CommentVo;
import com.hackyle.blog.customer.module.article.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 新增文章评论
     */
    @PostMapping
    public ApiResponse<String> add(CommentAddDto commentAddDto, HttpServletRequest request) {
        log.info("新增文章评论-Controller层入参-commentAddDto={}", JSON.toJSONString(commentAddDto));

        boolean commented = commentService.add(commentAddDto);
        return commented ? ApiResponse.ok() : ApiResponse.fail();
    }

    /**
     * 获取某个顶级评论下的所有评论
     */
    @GetMapping
    public ApiResponse<List<CommentVo>> getByRootId(@RequestParam("rootId") Long rootId) {
        if(rootId == null || rootId <= 0) {
            return ApiResponse.fail();
        }
        List<CommentVo> commentVos = commentService.getByRootId(rootId);
        return ApiResponse.ok(commentVos);
    }
}
