package com.hackyle.blog.admin.module.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.mapper.SysDictDetailMapper;
import com.hackyle.blog.admin.module.system.mapper.SysDictMapper;
import com.hackyle.blog.admin.module.system.model.dto.DictAddDto;
import com.hackyle.blog.admin.module.system.model.dto.DictQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.DictUpdateDto;
import com.hackyle.blog.admin.module.system.model.entity.SysDictDetailEntity;
import com.hackyle.blog.admin.module.system.model.entity.SysDictEntity;
import com.hackyle.blog.admin.module.system.model.vo.DictDetailVo;
import com.hackyle.blog.admin.module.system.model.vo.DictVo;
import com.hackyle.blog.admin.module.system.service.CacheDictService;
import com.hackyle.blog.admin.module.system.service.SysDictDetailService;
import com.hackyle.blog.admin.module.system.service.SysDictService;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictEntity>
    implements SysDictService {
    @Autowired
    private CacheDictService cacheDictService;
    @Autowired
    private SysDictDetailService sysDictDetailService;
    @Autowired
    private SysDictDetailMapper sysDictDetailMapper;

    @Transactional
    @Override
    public boolean add(DictAddDto addDto) {
        long countCode = this.count(Wrappers.<SysDictEntity>lambdaQuery()
                .eq(SysDictEntity::getCode, addDto.getCode()));
                //.eq(SysDictEntity::getDeleted, Boolean.FALSE));
        if (countCode > 0) {
            throw new BizException("字典Code已存在，请重新定义");
        }

        SysDictEntity dictEntity = BeanCopyUtils.copy(addDto, SysDictEntity.class);
        boolean saved = this.save(dictEntity);
        if(saved) {
            List<SysDictDetailEntity> dictDetails = null;
            if(CollectionUtil.isNotEmpty(addDto.getDetails())) {
                List<DictAddDto.DictAddDetail> details = addDto.getDetails();
                details.forEach(detail -> {
                    detail.setDictId(dictEntity.getId());
                });
                dictDetails = BeanCopyUtils.copyList(details, SysDictDetailEntity.class);
                boolean savedDetail = sysDictDetailService.saveBatch(dictDetails);
                if(!savedDetail) {
                    throw new BizException("保存字典明细失败");
                }
                dictEntity.setDetails(dictDetails);
            }

            cacheDictService.putCache(dictEntity);
        }

        return saved;
    }

    @Transactional
    @Override
    public boolean del(Set<Long> idSet) {
        //先查出来，不然删了，就获取不到明细了
        List<SysDictEntity> sysDictEntities = this.listByIds(idSet);

        boolean removed = this.removeBatchByIds(idSet);
        if(removed) {
            boolean removedDetail = sysDictDetailService.remove(Wrappers.<SysDictDetailEntity>lambdaQuery()
                    .in(SysDictDetailEntity::getDictId, idSet));
            if(!removedDetail) {
                throw new BizException("删除字典明细失败");
            }

            sysDictEntities.stream().map(SysDictEntity::getCode).forEach(code -> {
                cacheDictService.delCache(code);
            });
        }

        return removed;
    }

    /**
     * 更新主表，明细表先删再插入
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(DictUpdateDto updateDto) {
        SysDictEntity existsDict = this.getById(updateDto.getId());
        if(existsDict == null) {
            throw new BizException("字典不存在");
        }

        SysDictEntity dictEntity = BeanCopyUtils.copy(updateDto, SysDictEntity.class);
        boolean updated = this.updateById(dictEntity);
        if(updated) {
            List<DictUpdateDto.DictUpdateDetail> dictDetails = updateDto.getDetails();
            //删除明细表
            boolean removedDetail = sysDictDetailService.remove(Wrappers.<SysDictDetailEntity>lambdaQuery()
                    .eq(SysDictDetailEntity::getDictId, existsDict.getId()));
            if(!removedDetail) {
                throw new BizException("删除字典明细失败");
            }

            if(CollectionUtil.isNotEmpty(dictDetails)) {
                dictDetails.forEach(detail -> {
                    detail.setDictId(existsDict.getId());
                });
                List<SysDictDetailEntity> dictDetailEntityList = BeanCopyUtils.copyList(dictDetails, SysDictDetailEntity.class);
                boolean savedDetail = sysDictDetailService.saveBatch(dictDetailEntityList);
                if(!savedDetail) {
                    throw new BizException("保存字典明细失败");
                }
                dictEntity.setDetails(dictDetailEntityList);
            }
            cacheDictService.putCache(dictEntity);
        }

        return updated;
    }

    @Override
    public DictVo get(Long id) {
        SysDictEntity dict = cacheDictService.getCache(id);
        if(dict != null) {
            DictVo dictVo = BeanCopyUtils.copy(dict, DictVo.class);
            List<SysDictDetailEntity> details = dict.getDetails();
            List<DictDetailVo> detailVos = BeanCopyUtils.copyList(details, DictDetailVo.class);
            dictVo.setDetails(detailVos);
            return dictVo;
        }

        dict = this.getById(id);
        if(dict == null) {
            return null;
        }
        List<SysDictDetailEntity> details = sysDictDetailService.list(Wrappers.<SysDictDetailEntity>lambdaQuery()
                .eq(SysDictDetailEntity::getDictId, id));
        DictVo dictVo = BeanCopyUtils.copy(dict, DictVo.class);
        dictVo.setDetails(BeanCopyUtils.copyList(details, DictDetailVo.class));
        return dictVo;
    }

    @Override
    public PageInfo<DictVo> list(DictQueryDto queryDto) {
        LambdaQueryWrapper<SysDictEntity> queryWrapper = Wrappers.<SysDictEntity>lambdaQuery();
        if(StringUtils.isNotBlank(queryDto.getCode())) {
            queryWrapper.like(SysDictEntity::getCode, queryDto.getCode());
        }
        if(StringUtils.isNotBlank(queryDto.getName())) {
            queryWrapper.like(SysDictEntity::getName, queryDto.getName());
        }
        if(queryDto.getStatus() != null) {
            queryWrapper.eq(SysDictEntity::getStatus, queryDto.getStatus());
        }
        if(queryDto.getDeleted() != null) {
            queryWrapper.eq(SysDictEntity::getDeleted, queryDto.getDeleted());
        }

        PageHelper.startPage(queryDto.pageNum, queryDto.pageSize);
        List<SysDictEntity> dicts = this.list(queryWrapper);

        if(CollectionUtil.isEmpty(dicts)) {
            return new PageInfo<>();
        }

        Set<Long> dictIds = dicts.stream().map(SysDictEntity::getId).collect(Collectors.toSet());
        List<SysDictDetailEntity> detailList = sysDictDetailService.list(Wrappers.<SysDictDetailEntity>lambdaQuery()
                .in(SysDictDetailEntity::getDictId, dictIds));
        Map<Long, List<SysDictDetailEntity>> detailMap = detailList.stream().collect(Collectors.groupingBy(SysDictDetailEntity::getDictId));

        PageInfo<DictVo> pageInfo = PageHelperUtils.getPageInfo(dicts, DictVo.class);
        List<DictVo> list = pageInfo.getList();
        list.forEach(dict -> {
            List<SysDictDetailEntity> detailEntities = detailMap.get(dict.getId());
            if(CollectionUtil.isNotEmpty(detailEntities)) {
                dict.setDetails(BeanCopyUtils.copyList(detailEntities, DictDetailVo.class));
            }
        });
        pageInfo.setList(list);

        return pageInfo;
    }

    /**
     * 刷新缓存，可供定时任务、API手动触发调用
     */
    @Override
    public void refreshCache() {
        cacheDictService.clearCache();

        //逻辑删、未生效的是否放入缓存：是，具体判定交给业务逻辑，缓存类只负责缓存
        List<SysDictEntity> dicts = this.list(Wrappers.<SysDictEntity>lambdaQuery());
                //.eq(SysDictEntity::getStatus, StatusEnum.YES.getCode())
                //.eq(SysDictEntity::getDeleted, Boolean.FALSE));
        if(CollectionUtil.isEmpty(dicts)) {
            return;
        }

        Set<Long> dictIds = dicts.stream().map(SysDictEntity::getId).collect(Collectors.toSet());
        List<SysDictDetailEntity> details = sysDictDetailMapper.selectList(Wrappers.<SysDictDetailEntity>lambdaQuery()
                .in(SysDictDetailEntity::getDictId, dictIds));
        Map<Long, List<SysDictDetailEntity>> dictIdMap = details.stream().collect(Collectors.groupingBy(SysDictDetailEntity::getDictId));

        for (SysDictEntity dict : dicts) {
            dict.setDetails(dictIdMap.get(dict.getId()));
            cacheDictService.putCache(dict);
        }
    }

    /**
     * 项目初始化时，加载所有字典信息到Redis缓存
     */
    @PostConstruct
    public void init() {
        log.info("======开始加载字典信息sys_dict到Redis缓存======");
        refreshCache();
        log.info("======加载字典信息sys_dict到Redis缓存完成======");
    }
}

