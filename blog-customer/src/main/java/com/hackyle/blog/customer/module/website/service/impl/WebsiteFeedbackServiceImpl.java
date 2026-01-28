package com.hackyle.blog.customer.module.website.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.ip.IpUtils;
import com.hackyle.blog.common.ip.PconlineIpRegionDto;
import com.hackyle.blog.common.ip.PconlineIpRegionUtils;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PageHelperUtils;
import com.hackyle.blog.customer.infrastructure.redis.CacheKey;
import com.hackyle.blog.customer.module.article.model.dto.CommentAddDto;
import com.hackyle.blog.customer.module.website.mapper.WebsiteFeedbackMapper;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackAddDto;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackQueryDto;
import com.hackyle.blog.customer.module.website.model.dto.FeedbackUpdateDto;
import com.hackyle.blog.customer.module.website.model.entity.WebsiteFeedbackEntity;
import com.hackyle.blog.customer.module.website.model.vo.FeedbackVo;
import com.hackyle.blog.customer.module.website.service.WebsiteFeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WebsiteFeedbackServiceImpl extends ServiceImpl<WebsiteFeedbackMapper, WebsiteFeedbackEntity>
    implements WebsiteFeedbackService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public boolean add(FeedbackAddDto addDto) {
        //恶意提交检查
        badRequestCheck();

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

    /**
     * 一个IP+UA，一天只允许提交5次
     */
    private void badRequestCheck() {
        String publicIp = IpUtils.getPublicIp(); //注意，这里可能获取到的ip是unknown
        String userAgent = "";
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            userAgent = request.getHeader("User-Agent");
        }
        userAgent = StringUtils.isBlank(userAgent) ? "" : userAgent.replaceAll(" ", "");

        String feedbackKey = CacheKey.PREFIX + "feedback:" + publicIp + ":" + userAgent;

        Object feedbackObj = redisTemplate.opsForValue().get(feedbackKey);
        if(feedbackObj == null) {
            redisTemplate.opsForValue().set(feedbackKey, 1, 24, TimeUnit.HOURS);
        } else {
            int feedbackCount = Integer.parseInt(String.valueOf(feedbackObj));
            if(feedbackCount > 5) {
                log.info("对文章的恶意评论-key={},feedbackCount={}", feedbackKey, feedbackCount);
                throw new BizException("您的反馈过于频繁，请稍后再试！");
            }
            redisTemplate.opsForValue().increment(feedbackKey, 1);
        }
    }

}
