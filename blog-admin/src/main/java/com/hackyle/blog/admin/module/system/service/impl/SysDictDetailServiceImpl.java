package com.hackyle.blog.admin.module.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.system.mapper.SysDictDetailMapper;
import com.hackyle.blog.admin.module.system.mapper.SysDictMapper;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailAddDto;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailQueryDto;
import com.hackyle.blog.admin.module.system.model.dto.DictDetailUpdateDto;
import com.hackyle.blog.admin.module.system.model.entity.SysDictDetailEntity;
import com.hackyle.blog.admin.module.system.model.entity.SysDictEntity;
import com.hackyle.blog.admin.module.system.model.vo.DictDetailVo;
import com.hackyle.blog.admin.module.system.service.CacheDictService;
import com.hackyle.blog.admin.module.system.service.SysDictDetailService;
import com.hackyle.blog.common.enums.StatusEnum;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysDictDetailServiceImpl extends ServiceImpl<SysDictDetailMapper, SysDictDetailEntity>
    implements SysDictDetailService {
    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private CacheDictService cacheDictService;

    /**
     * 针对某个字典，添加字典项
     * 怎么保持DB和Cache一致性？先增加DB，再查，最后放Cache
     */
    @Override
    public boolean add(List<DictDetailAddDto> addDto) {
        Set<Long> dictIds = addDto.stream().map(DictDetailAddDto::getDictId).collect(Collectors.toSet());
        List<SysDictEntity> sysDictEntities = sysDictMapper.selectList(Wrappers.<SysDictEntity>lambdaQuery()
                .in(SysDictEntity::getId, dictIds)
                .eq(SysDictEntity::getDeleted, Boolean.FALSE)
                .eq(SysDictEntity::getStatus, StatusEnum.YES.getCode())
                .select(SysDictEntity::getCode, SysDictEntity::getId));
        if(dictIds.size() != sysDictEntities.size()) {
            throw new BizException("部分字典不存在，请检查");
        }

        List<SysDictDetailEntity> details = BeanCopyUtils.copyList(addDto, SysDictDetailEntity.class);
        boolean saved = this.saveBatch(details);
        if(saved) {
            for (SysDictEntity sysDictEntity : sysDictEntities) {
                List<SysDictDetailEntity> dictDetails = this.list(Wrappers.<SysDictDetailEntity>lambdaQuery().eq(SysDictDetailEntity::getDictId, sysDictEntity.getId()));
                sysDictEntity.setDetails(dictDetails);

                cacheDictService.putCache(sysDictEntity);
            }
        }

        return saved;
    }

    @Override
    public boolean del(Set<Long> idSet) {
        List<SysDictDetailEntity> dictDetailEntityList = this.listByIds(idSet);
        if(CollectionUtil.isEmpty(dictDetailEntityList)) {
            return true;
        }

        boolean removed = this.removeByIds(idSet);
        if(removed) {
            Set<Long> dictIds = dictDetailEntityList.stream().map(SysDictDetailEntity::getDictId).collect(Collectors.toSet());
            List<SysDictEntity> dicts = sysDictMapper.selectList(Wrappers.<SysDictEntity>lambdaQuery().in(SysDictEntity::getId, dictIds));

            List<SysDictDetailEntity> details = this.list(Wrappers.<SysDictDetailEntity>lambdaQuery()
                    .in(SysDictDetailEntity::getDictId, dictIds));
            Map<Long, List<SysDictDetailEntity>> dictIdMap = details.stream().collect(Collectors.groupingBy(SysDictDetailEntity::getDictId));
            for (SysDictEntity dict : dicts) {
                dict.setDetails(dictIdMap.get(dict.getId()));
                cacheDictService.putCache(dict);
            }
        }

        return removed;
    }

    @Override
    public boolean update(DictDetailUpdateDto updateDto) {
        SysDictDetailEntity detailExists = this.getById(updateDto.getId());
        if(detailExists == null) {
            return false;
        }

        boolean updated = this.updateById(BeanCopyUtils.copy(updateDto, SysDictDetailEntity.class));
        if(updated) {
            SysDictEntity sysDictEntity = sysDictMapper.selectById(detailExists.getDictId());
            List<SysDictDetailEntity> details = this.list(Wrappers.<SysDictDetailEntity>lambdaQuery()
                    .eq(SysDictDetailEntity::getDictId, detailExists.getDictId()));

            sysDictEntity.setDetails(details);
            cacheDictService.putCache(sysDictEntity);
        }

        return updated;
    }

    @Override
    public DictDetailVo getByDetailId(Long dictId) {
        SysDictDetailEntity detail = this.getById(dictId);
        return BeanCopyUtils.copy(detail, DictDetailVo.class);
    }

    @Override
    public PageInfo<DictDetailVo> list(DictDetailQueryDto queryDto) {
        LambdaQueryWrapper<SysDictDetailEntity> queryWrapper = Wrappers.<SysDictDetailEntity>lambdaQuery();
        queryWrapper.eq(SysDictDetailEntity::getDictId, queryDto.getDictId());
        if(StringUtils.isNotBlank(queryDto.getValue())) {
            queryWrapper.like(SysDictDetailEntity::getValue, queryDto.getValue());
        }
        if(StringUtils.isNotBlank(queryDto.getLabel())) {
            queryWrapper.like(SysDictDetailEntity::getLabel, queryDto.getLabel());
        }
        if(queryDto.getStatus() != null) {
            queryWrapper.eq(SysDictDetailEntity::getStatus, queryDto.getStatus());
        }
        if(queryDto.getDeleted() != null) {
            queryWrapper.eq(SysDictDetailEntity::getDeleted, queryDto.getDeleted());
        }

        PageHelper.startPage(queryDto.pageNum, queryDto.pageSize);
        List<SysDictDetailEntity> details = this.list(queryWrapper);

        return PageHelperUtils.getPageInfo(details, DictDetailVo.class);
    }
}




