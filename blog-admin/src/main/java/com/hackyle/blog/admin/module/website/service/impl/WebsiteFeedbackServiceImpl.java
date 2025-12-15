package com.hackyle.blog.admin.module.website.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.admin.module.website.model.dto.FeedbackAddDto;
import com.hackyle.blog.admin.module.website.model.dto.FeedbackQueryDto;
import com.hackyle.blog.admin.module.website.model.dto.FeedbackUpdateDto;
import com.hackyle.blog.admin.module.website.model.entity.WebsiteFeedbackEntity;
import com.hackyle.blog.admin.module.website.model.vo.FeedbackVo;
import com.hackyle.blog.admin.module.website.service.WebsiteFeedbackService;
import com.hackyle.blog.admin.module.website.mapper.WebsiteFeedbackMapper;
import com.hackyle.blog.common.ip.IpUtils;
import com.hackyle.blog.common.ip.PconlineIpRegionDto;
import com.hackyle.blog.common.ip.PconlineIpRegionUtils;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Service
public class WebsiteFeedbackServiceImpl extends ServiceImpl<WebsiteFeedbackMapper, WebsiteFeedbackEntity>
    implements WebsiteFeedbackService {

    @Override
    public boolean add(FeedbackAddDto addDto) {
        WebsiteFeedbackEntity feedbackEntity = BeanCopyUtils.copy(addDto, WebsiteFeedbackEntity.class);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userAgent = request.getHeader("User-Agent");
            feedbackEntity.setUserAgent(userAgent);

        }
        String publicIp = IpUtils.getPublicIp();
        feedbackEntity.setIp(publicIp);
        PconlineIpRegionDto ipRegion = PconlineIpRegionUtils.getIpRegion(publicIp);
        if(ipRegion != null) {
            feedbackEntity.setIpLocation(JSON.toJSONString(ipRegion)); //记录IP的位置信息
        }

        return this.save(feedbackEntity);
    }

    @Transactional
    @Override
    public boolean del(Set<Long> idSet) {
        return this.removeBatchByIds(idSet);
    }

    @Override
    public boolean update(FeedbackUpdateDto updateDto) {
        WebsiteFeedbackEntity feedbackEntity = BeanCopyUtils.copy(updateDto, WebsiteFeedbackEntity.class);
        return this.updateById(feedbackEntity);
    }

    @Override
    public FeedbackVo get(Long id) {
        WebsiteFeedbackEntity feedbackEntity = this.getById(id);
        return BeanCopyUtils.copy(feedbackEntity, FeedbackVo.class);
    }

    @Override
    public PageInfo<FeedbackVo> list(FeedbackQueryDto queryDto) {
        LambdaQueryWrapper<WebsiteFeedbackEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(queryDto.getName())) {
            queryWrapper.like(WebsiteFeedbackEntity::getName, queryDto.getName());
        }
        if(StringUtils.isNotBlank(queryDto.getEmail())){
            queryWrapper.like(WebsiteFeedbackEntity::getEmail, queryDto.getEmail());
        }
        if(StringUtils.isNotBlank(queryDto.getPhone())) {
            queryWrapper.like(WebsiteFeedbackEntity::getPhone, queryDto.getPhone());
        }
        if(StringUtils.isNotBlank(queryDto.getLink())) {
            queryWrapper.like(WebsiteFeedbackEntity::getLink, queryDto.getLink());
        }
        if(StringUtils.isNotBlank(queryDto.getContent())) {
            queryWrapper.like(WebsiteFeedbackEntity::getContent, queryDto.getContent());
        }
        if(queryDto.getStatus() != null) {
            queryWrapper.eq(WebsiteFeedbackEntity::getStatus, queryDto.getStatus());
        }
        //if(queryDto.getDeleted() != null) {
        //    queryWrapper.eq(WebsiteFeedbackEntity::getDeleted, queryDto.getDeleted());
        //}

        PageHelper.startPage(queryDto.pageNum, queryDto.pageSize);
        List<WebsiteFeedbackEntity> feedbacks = this.list(queryWrapper);

        return PageHelperUtils.getPageInfo(feedbacks, FeedbackVo.class);
    }
}
