package com.hackyle.blog.admin.module.system.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailAddDto;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailUpdateDto;
import com.hackyle.blog.admin.module.system.model.vo.DictDetailVo;
import com.hackyle.blog.admin.module.system.service.SysDictDetailService;
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
@RequestMapping("/system/dict/detail")
public class DictDetailController {
    @Autowired
    private SysDictDetailService sysDictDetailService;

    @PostMapping
    public ApiResponse<?> add(@Validated @RequestBody List<DictDetailAddDto> addDto) {
        log.info("新增字典明细-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        boolean status =  sysDictDetailService.add(addDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @DeleteMapping
    public ApiResponse<?> del(@RequestParam("ids")String ids) {
        log.info("删除字典明细-Controller层入参-ids={}", ids);
        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }

        boolean status =  sysDictDetailService.del(idSet);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody DictDetailUpdateDto updateDto) {
        log.info("修改字典明细-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));
        boolean status = sysDictDetailService.update(updateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    /**
     * 根据字典详情ID获取
     */
    @GetMapping
    public ApiResponse<DictDetailVo> getByDetailId(@RequestParam("id") Long id) {
        log.info("获取字典明细详情-Controller层入参-id={}", id);

        if(id == null || id < 1) {
            throw new IllegalArgumentException("入参不能为空");
        }

        DictDetailVo detailVo = sysDictDetailService.getByDetailId(id);
        return ApiResponse.ok(detailVo);
    }

    /**
     * 根据查询条件获取字典明细
     */
    @PostMapping("/list")
    public ApiResponse<PageInfo<DictDetailVo>> list(@RequestBody DictDetailQueryDto queryDto) {
        log.info("根据查询条件获取字典明细-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<DictDetailVo> pageInfo = sysDictDetailService.list(queryDto);
        return ApiResponse.ok(pageInfo);
    }

}
