package com.hackyle.blog.admin.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.mapper.SysConfigMapper;
import com.hackyle.blog.admin.module.system.model.dto.ConfigAddDto;
import com.hackyle.blog.admin.module.system.model.dto.ConfigQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.ConfigUpdateDto;
import com.hackyle.blog.admin.module.system.model.entity.SysConfigEntity;
import com.hackyle.blog.admin.module.system.model.vo.ConfigVo;
import com.hackyle.blog.admin.module.system.service.CacheConfigService;
import com.hackyle.blog.admin.module.system.service.SysConfigService;
import com.hackyle.blog.common.enums.StatusEnum;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfigEntity>
    implements SysConfigService {

    @Autowired
    private CacheConfigService cacheConfigService;

    @Override
    public boolean add(ConfigAddDto addDto) {
        //检查范围包括已逻辑删除和未生效的
        long countKey = this.count(Wrappers.<SysConfigEntity>lambdaQuery().eq(SysConfigEntity::getConfigKey, addDto.getConfigKey()));
        if (countKey > 0) {
            throw new BizException("参数Key已存在，请重新定义");
        }

        SysConfigEntity configEntity = BeanCopyUtils.copy(addDto, SysConfigEntity.class);
        boolean saved = this.save(configEntity);
        if (saved) {
            cacheConfigService.putCache(configEntity);
        }

        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean del(Set<Long> idSet) {
        //这一步必须先查，不然会被删掉
        List<SysConfigEntity> configs = this.listByIds(idSet);

        boolean removed = this.removeBatchByIds(idSet);
        if (removed) {
            configs.stream().map(SysConfigEntity::getConfigKey).forEach(configKey -> {
                cacheConfigService.delCache(configKey);
            });
        }

        return removed;
    }

    @Override
    public boolean update(ConfigUpdateDto updateDto) {
        SysConfigEntity configEntity = BeanCopyUtils.copy(updateDto, SysConfigEntity.class);

        boolean updated = this.updateById(configEntity);
        if (updated) { //生效
            SysConfigEntity config = this.getById(updateDto.getId());
            cacheConfigService.putCache(config);
        }

        return updated;
    }

    @Override
    public ConfigVo get(Long id) {
        SysConfigEntity config = cacheConfigService.getCache(id);
        if(config == null) {
            config = this.getById(id);
            cacheConfigService.putCache(config);
        }
        return BeanCopyUtils.copy(config, ConfigVo.class);
    }

    @Override
    public ConfigVo get(String configKey) {
        SysConfigEntity config = cacheConfigService.getCache(configKey);
        if(config == null) {
            config = this.getOne(Wrappers.<SysConfigEntity>lambdaQuery()
                    .eq(SysConfigEntity::getConfigKey, configKey)
                    .eq(SysConfigEntity::getStatus, StatusEnum.YES.getCode())
                    .eq(SysConfigEntity::getDeleted, Boolean.FALSE));
        }

        return BeanCopyUtils.copy(config, ConfigVo.class);
    }

    @Override
    public PageInfo<ConfigVo> list(ConfigQueryDto queryDto) {
        LambdaQueryWrapper<SysConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(queryDto.getConfigKey())) {
            queryWrapper.like(SysConfigEntity::getConfigKey, queryDto.getConfigKey());
        }
        if(StringUtils.isNotBlank(queryDto.getConfigName())){
            queryWrapper.like(SysConfigEntity::getConfigName, queryDto.getConfigName());
        }
        if(StringUtils.isNotBlank(queryDto.getConfigValue())) {
            queryWrapper.like(SysConfigEntity::getConfigValue, queryDto.getConfigValue());
        }
        if(queryDto.getStatus() != null) {
            queryWrapper.eq(SysConfigEntity::getStatus, queryDto.getStatus());
        }
        if(queryDto.getDeleted() != null) {
            queryWrapper.eq(SysConfigEntity::getDeleted, queryDto.getDeleted());
        }

        PageHelper.startPage(queryDto.pageNum, queryDto.pageSize);
        List<SysConfigEntity> configs = this.list(queryWrapper);

        return PageHelperUtils.getPageInfo(configs, ConfigVo.class);
    }

    /**
     * 刷新缓存，可供定时任务、API手动触发调用
     */
    @Override
    public boolean refreshCache() {
        cacheConfigService.clearCache();

        //逻辑删、未生效的是否放入缓存：是，具体判定交给业务逻辑，缓存类只负责缓存
        List<SysConfigEntity> list = this.list(Wrappers.<SysConfigEntity>lambdaQuery());
                //.eq(SysConfigEntity::getStatus, StatusEnum.YES.getCode())
                //.eq(SysConfigEntity::getDeleted, Boolean.FALSE));
        for (SysConfigEntity config : list) {
            cacheConfigService.putCache(config);
        }

        return true;
    }


    /**
     * 项目初始化时，加载所有配置信息到Redis缓存
     */
    @PostConstruct
    public void init() {
        log.info("======开始加载系统配置信息sys_config到Redis缓存======");
        refreshCache();
        log.info("======加载系统配置信息sys_config到Redis缓存完成======");
    }

}
