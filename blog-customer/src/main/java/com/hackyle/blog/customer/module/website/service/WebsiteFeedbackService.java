package com.hackyle.blog.customer.module.website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackAddDto;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackQueryDto;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackUpdateDto;
import com.hackyle.blog.customer.module.website.model.entity.WebsiteFeedbackEntity;
import com.hackyle.blog.customer.module.website.model.vo.FeedbackVo;


import java.util.Set;

public interface WebsiteFeedbackService extends IService<WebsiteFeedbackEntity> {

    boolean add(FeedbackAddDto addDto);

    boolean del(Set<Long> idSet);

    boolean update(FeedbackUpdateDto updateDto);

    FeedbackVo get(Long id);

    PageInfo<FeedbackVo> list(FeedbackQueryDto queryDto);

}
