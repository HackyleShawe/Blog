package com.hackyle.blog.customer.module.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.customer.module.article.model.entity.VisitLogEntity;

public interface VisitLogService extends IService<VisitLogEntity> {

    void add(VisitLogEntity visitLogEntity);

}
