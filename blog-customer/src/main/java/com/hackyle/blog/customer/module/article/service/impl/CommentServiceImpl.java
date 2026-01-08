package com.hackyle.blog.customer.module.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.common.enums.DeletedEnum;
import com.hackyle.blog.common.enums.StatusEnum;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.ip.IpUtils;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.SnowFlakeIdUtils;
import com.hackyle.blog.customer.infrastructure.redis.CacheKey;
import com.hackyle.blog.customer.module.article.mapper.ArticleMapper;
import com.hackyle.blog.customer.module.article.mapper.CommentMapper;
import com.hackyle.blog.customer.module.article.model.dto.CommentAddDto;
import com.hackyle.blog.customer.module.article.model.entity.ArticleEntity;
import com.hackyle.blog.customer.module.article.model.entity.CommentEntity;
import com.hackyle.blog.customer.module.article.model.vo.CommentVo;
import com.hackyle.blog.customer.module.article.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ValueOperations<String, String> valueOperations;


    @Override
    public boolean add(CommentAddDto addDto) {
        //恶意提交判定
        badRequestCheck(addDto);

        //检查被评论的文章是否存在
        ArticleEntity articleEntity = articleMapper.selectById(addDto.getArticleId());
        if(articleEntity == null) {
            throw new BizException("文章不存在");
        }

        //如果存在rootId和pid，则需要检查
        if(addDto.getPid() != null) {
            CommentEntity exists = commentMapper.selectById(addDto.getPid());
            if(exists == null) {
                throw new BizException("pid不存在");
            }
            addDto.setPname(exists.getCommentatorName());
        }

        CommentEntity CommentEntity = BeanCopyUtils.copy(addDto, CommentEntity.class);
        CommentEntity.setId(SnowFlakeIdUtils.getInstance().nextId());
        int insert = commentMapper.insert(CommentEntity);
        if(insert != 1) {
            throw new BizException("新增评论失败");
        }

        return true;
    }


    private void badRequestCheck(CommentAddDto commentAddDto) {
        String publicIp = IpUtils.getPublicIp(); //注意，这里可能获取到的ip是unknown
        //5小时内对某article限制提交5次
        String articleIpKey = CacheKey.PREFIX + commentAddDto.getArticleId() + ":" +publicIp;
        String articleIpVal = valueOperations.get(articleIpKey);
        if(StringUtils.isBlank(articleIpVal)) {
            valueOperations.set(articleIpKey, "1", 5, TimeUnit.HOURS);
        } else {
            int articleIpInt = Integer.parseInt(articleIpVal);
            if(articleIpInt > 5) {
                log.info("对文章的恶意评论-articleIpKey={},articleIpVal={}", articleIpKey, articleIpVal);
                throw new BizException("您对该文章的评论过于频繁，请稍后再试！");
            }
            valueOperations.increment(articleIpKey, 1);
        }

        //5小时内对某父评论限制提交5次
        if(commentAddDto.getPid() != null) {
            String pidIpKey = CacheKey.PREFIX + commentAddDto.getPid() + ":" +publicIp;
            String pidIpVal = valueOperations.get(pidIpKey);
            if(StringUtils.isBlank(pidIpVal)) {
                valueOperations.set(pidIpKey, "1", 5, TimeUnit.HOURS);
            } else {
                int reqValInt = Integer.parseInt(pidIpVal);
                if(reqValInt > 5) {
                    log.info("对父评论的恶意评论-pidIpKey={},pidIpVal={}", pidIpKey, pidIpVal);
                    throw new BizException("您对该评论的评论过于频繁，请稍后再试！");
                }
                valueOperations.increment(pidIpKey, 1);
            }
        }
    }

    /**
     * 获取文章的顶级评论
     * @param articleId 文章id
     * @return 顶级评论
     */
    @Override
    public List<CommentVo> getTopByArticle(long articleId) {
        if(articleId <= 0) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<CommentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentEntity::getArticleId, articleId)
                //.eq(CommentEntity::getRootId, 0) //顶级评论
                .eq(CommentEntity::getDeleted, DeletedEnum.FALSE.getCode())
                .eq(CommentEntity::getReleased, 1)
                .orderByDesc(CommentEntity::getCreateTime); //按照创建时间逆序，最新发表的评论在最前面
        List<CommentEntity> commentEntities = commentMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(commentEntities)) {
            return new ArrayList<>();
        }

        List<CommentEntity> topComments = commentEntities.stream().filter(ele -> 0L == ele.getRootId()).toList();
        Map<Long, List<CommentEntity>> rootCommentMap = commentEntities.stream().filter(ele -> 0L != ele.getRootId())
                .collect(Collectors.groupingBy(CommentEntity::getRootId));

        List<CommentVo> commentVos = new ArrayList<>();
        for (CommentEntity comment : topComments) {
            CommentVo commentVo = BeanCopyUtils.copy(comment, CommentVo.class);
            commentVo.setCreateTime(comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            List<CommentEntity> rootComments = rootCommentMap.get(comment.getId());
            List<CommentVo> childComments = BeanCopyUtils.copyList(rootComments, CommentVo.class);
            for (CommentVo childComment : childComments) {
                childComment.setCreateTime(comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            commentVo.setChildren(childComments);

            commentVos.add(commentVo);
        }

        return commentVos;
    }

    @Override
    public List<CommentVo> getByRootId(Long rootId) {
        LambdaQueryWrapper<CommentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentEntity::getRootId, rootId)
                .eq(CommentEntity::getDeleted, DeletedEnum.FALSE.getCode())
                .eq(CommentEntity::getReleased, StatusEnum.YES.getCode());
        List<CommentEntity> comments = commentMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(comments)) {
            return new ArrayList<>();
        }

        //按照创建时间逆序，最新发表的评论在最前面
        comments.sort(Comparator.comparing(CommentEntity::getCreateTime).reversed());
        return BeanCopyUtils.copyList(comments, CommentVo.class);
    }


}
