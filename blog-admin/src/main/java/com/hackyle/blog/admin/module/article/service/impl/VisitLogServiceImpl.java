package com.hackyle.blog.admin.module.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.mapper.VisitLogMapper;
import com.hackyle.blog.admin.module.article.model.dto.VisitLogQueryDto;
import com.hackyle.blog.admin.module.article.model.entity.VisitLogEntity;
import com.hackyle.blog.admin.module.article.model.vo.VisitLogVo;
import com.hackyle.blog.admin.module.article.service.VisitLogService;
import com.hackyle.blog.common.util.PageHelperUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitLogServiceImpl extends ServiceImpl<VisitLogMapper, VisitLogEntity>
    implements VisitLogService {

    @Override
    public PageInfo<VisitLogVo> list(VisitLogQueryDto queryDto) {
        LambdaQueryWrapper<VisitLogEntity> query = Wrappers.<VisitLogEntity>lambdaQuery();
        if(queryDto.getArticleId()!= null) {
            query.eq(VisitLogEntity::getArticleId, queryDto.getArticleId());
        }
        if(StringUtils.isNotBlank(queryDto.getStartTime())) {
            query.ge(VisitLogEntity::getVisitTime, queryDto.getStartTime());
        }
        if(StringUtils.isNotBlank(queryDto.getEndTime())) {
            query.le(VisitLogEntity::getVisitTime, queryDto.getEndTime());
        }

        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        List<VisitLogEntity> visitLogList = this.list(query);

        return PageHelperUtils.getPageInfo(visitLogList, VisitLogVo.class);
    }
}




