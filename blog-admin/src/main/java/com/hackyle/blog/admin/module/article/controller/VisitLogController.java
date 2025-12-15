package com.hackyle.blog.admin.module.article.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.ArticleQueryDto;
import com.hackyle.blog.admin.module.article.model.dto.VisitLogQueryDto;
import com.hackyle.blog.admin.module.article.model.vo.ArticleVo;
import com.hackyle.blog.admin.module.article.model.vo.VisitLogVo;
import com.hackyle.blog.admin.module.article.service.VisitLogService;
import com.hackyle.blog.common.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/article/visitLog")
public class VisitLogController {
    @Autowired
    private VisitLogService visitLogService;

    @PostMapping("/list")
    public ApiResponse<PageInfo<VisitLogVo>> list(@RequestBody VisitLogQueryDto queryDto) {
        log.info("分页获取访问日志列表-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<VisitLogVo> visitLogVoPageInfo = visitLogService.list(queryDto);
        return ApiResponse.ok(visitLogVoPageInfo);
    }
}
