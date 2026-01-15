package com.hackyle.blog.admin.module.system.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.model.dto.DictAddDto;
import com.hackyle.blog.admin.module.system.model.dto.DictQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.DictUpdateDto;
import com.hackyle.blog.admin.module.system.model.vo.DictVo;
import com.hackyle.blog.admin.module.system.service.SysDictService;
import com.hackyle.blog.common.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/system/dict")
public class DictController {
    @Autowired
    private SysDictService sysDictService;

    @PostMapping
    public ApiResponse<?> add(@Valid @RequestBody DictAddDto addDto) {
        log.info("新增字典-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        boolean status =  sysDictService.add(addDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @DeleteMapping
    public ApiResponse<?> del(@RequestParam("ids")String ids) {
        log.info("删除字典-Controller层入参-ids={}", ids);
        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }

        boolean status =  sysDictService.del(idSet);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @PutMapping
    public ApiResponse<String> update(@Valid @RequestBody DictUpdateDto updateDto) {
        log.info("修改字典-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));
        boolean status = sysDictService.update(updateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @GetMapping
    public ApiResponse<DictVo> get(@RequestParam("id") Long id) {
        log.info("获取字典详情-Controller层入参-id={}", id);

        if(id == null || id < 1) {
            throw new IllegalArgumentException("入参不能为空");
        }

        DictVo dictVo = sysDictService.get(id);
        return ApiResponse.ok(dictVo);
    }

    /**
     * 根据查询条件获取字典
     */
    @PostMapping("/list")
    public ApiResponse<PageInfo<DictVo>> list(@RequestBody DictQueryDto queryDto) {
        log.info("根据查询条件获取字典-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<DictVo> pageInfo = sysDictService.list(queryDto);
        return ApiResponse.ok(pageInfo);
    }

    @DeleteMapping("/refreshCache")
    public ApiResponse<String> refreshCache() {
        sysDictService.refreshCache();
        return ApiResponse.ok();
    }

}
