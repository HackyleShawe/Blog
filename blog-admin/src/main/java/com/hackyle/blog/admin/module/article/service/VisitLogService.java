package com.hackyle.blog.admin.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.article.model.dto.VisitLogQueryDto;
import com.hackyle.blog.admin.module.article.model.entity.VisitLogEntity;
import com.hackyle.blog.admin.module.article.model.vo.VisitLogVo;

public interface VisitLogService extends IService<VisitLogEntity> {

    PageInfo<VisitLogVo> list(VisitLogQueryDto queryDto);

}
