package com.hackyle.blog.admin.module.system.controller;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.model.dto.ConfigAddDto;
import com.hackyle.blog.admin.module.system.model.dto.ConfigQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.ConfigUpdateDto;
import com.hackyle.blog.admin.module.system.model.vo.ConfigVo;
import com.hackyle.blog.admin.module.system.service.SysConfigService;
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
@RequestMapping("/system/config")
public class ConfigController {
    @Autowired
    private SysConfigService sysConfigService;

    @PostMapping
    public ApiResponse<?> add(@Validated @RequestBody ConfigAddDto addDto) {
        log.info("新增参数配置-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        boolean status =  sysConfigService.add(addDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @DeleteMapping
    public ApiResponse<?> del(@RequestParam("ids")String ids) {
        log.info("删除参数配置-Controller层入参-ids={}", ids);
        if(StringUtils.isBlank(ids)) {
            throw new IllegalArgumentException("入参id错误，请检查");
        }

        String[] idSplit = ids.split(",");
        Set<Long> idSet = new HashSet<>();
        for (String idStr : idSplit) {
            idSet.add(Long.parseLong(idStr));
        }

        boolean status =  sysConfigService.del(idSet);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody ConfigUpdateDto updateDto) {
        log.info("修改参数配置-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));
        boolean status = sysConfigService.update(updateDto);
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

    @GetMapping
    public ApiResponse<ConfigVo> get(@RequestParam("id") Long id) {
        log.info("获取参数配置详情-Controller层入参-id={}", id);

        if(id == null || id < 1) {
            throw new IllegalArgumentException("入参不能为空");
        }

        ConfigVo configVo = sysConfigService.get(id);
        return ApiResponse.ok(configVo);
    }

    /**
     * 根据查询条件获取参数配置
     */
    @PostMapping("/list")
    public ApiResponse<PageInfo<ConfigVo>> list(@RequestBody ConfigQueryDto queryDto) {
        log.info("根据查询条件获取参数配置-Controller层入参-queryDto={}", JSON.toJSONString(queryDto));

        PageInfo<ConfigVo> pageInfo = sysConfigService.list(queryDto);
        return ApiResponse.ok(pageInfo);
    }

    @DeleteMapping("/refreshCache")
    public ApiResponse<String> refreshCache() {
        boolean status = sysConfigService.refreshCache();
        return status ? ApiResponse.ok() : ApiResponse.fail();
    }

}
