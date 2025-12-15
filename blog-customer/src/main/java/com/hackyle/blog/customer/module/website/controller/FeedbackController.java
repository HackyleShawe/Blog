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
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/website/feedback")
public class FeedbackController {
    @Autowired
    private WebsiteFeedbackService websiteFeedbackService;


    @PostMapping
    public ApiResponse<?> add(@Validated @RequestBody FeedbackAddDto addDto) {
        log.info("新增留言反馈-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        boolean status =  websiteFeedbackService.add(addDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @DeleteMapping
    public ApiResponse<?> del(@RequestParam("ids")String ids) {
        log.info("删除留言反馈-Controller层入参-ids={}", ids);
        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }

        boolean status =  websiteFeedbackService.del(idSet);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody FeedbackUpdateDto updateDto) {
        log.info("修改留言反馈-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));
        boolean status = websiteFeedbackService.update(updateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @GetMapping
    public ApiResponse<FeedbackVo> get(@RequestParam("id") Long id) {
        log.info("获取留言反馈详情-Controller层入参-id={}", id);

        if(id == null || id < 1) {
            throw new IllegalArgumentException("入参不能为空");
        }

        FeedbackVo FeedbackVo = websiteFeedbackService.get(id);
        return ApiResponse.ok(FeedbackVo);
    }

    /**
     * 根据查询条件获取留言反馈
     */
    @PostMapping("/list")
    public ApiResponse<PageInfo<FeedbackVo>> list(@RequestBody FeedbackQueryDto queryDto) {
        log.info("根据查询条件获取留言反馈-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<FeedbackVo> pageInfo = websiteFeedbackService.list(queryDto);
        return ApiResponse.ok(pageInfo);
    }
}
