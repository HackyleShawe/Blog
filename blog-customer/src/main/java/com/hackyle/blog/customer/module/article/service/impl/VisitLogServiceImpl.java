package com.hackyle.blog.customer.module.article.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.common.ip.IpUtils;
import com.hackyle.blog.common.ip.PconlineIpRegionDto;
import com.hackyle.blog.common.ip.PconlineIpRegionUtils;
import com.hackyle.blog.customer.module.article.mapper.VisitLogMapper;
import com.hackyle.blog.customer.module.article.model.entity.VisitLogEntity;
import com.hackyle.blog.customer.module.article.service.VisitLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class VisitLogServiceImpl extends ServiceImpl<VisitLogMapper, VisitLogEntity>
    implements VisitLogService {

    @Override
    public void add(VisitLogEntity visitLogEntity) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userAgent = request.getHeader("User-Agent");
            visitLogEntity.setUserAgent(userAgent);

        }
        String publicIp = IpUtils.getPublicIp();
        visitLogEntity.setIp(publicIp);
        PconlineIpRegionDto ipRegion = PconlineIpRegionUtils.getIpRegion(publicIp);
        if(ipRegion != null) {
            visitLogEntity.setIpLocation(JSON.toJSONString(ipRegion)); //记录IP的位置信息
        }

        boolean saved = this.save(visitLogEntity);
        log.info("保存访问日志-visitLogEntity:{}, saved:{}", JSON.toJSONString(visitLogEntity), saved);
    }
}




