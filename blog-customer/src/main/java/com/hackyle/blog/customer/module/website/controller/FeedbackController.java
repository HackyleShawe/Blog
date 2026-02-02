package com.hackyle.blog.customer.module.website.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.common.domain.ApiResponse;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackAddDto;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackQueryDto;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackUpdateDto;
import com.hackyle.blog.customer.module.website.model.vo.FeedbackVo;
import com.hackyle.blog.customer.module.website.service.WebsiteFeedbackService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/website/feedback")
public class FeedbackController {
    @Autowired
    private WebsiteFeedbackService websiteFeedbackService;


    @ResponseBody
    @PostMapping
    public ApiResponse<?> add(@Validated FeedbackAddDto addDto) {
        log.info("新增留言反馈-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        //单独捕获，否则返回400页面
        try {
            boolean status =  websiteFeedbackService.add(addDto);
            return status ? ApiResponse.ok() : ApiResponse.fail();
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
