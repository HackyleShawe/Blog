package com.hackyle.blog.consumer.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.consumer.common.constant.ResponseEnum;
import com.hackyle.blog.consumer.common.pojo.ApiResponse;
import com.hackyle.blog.consumer.dto.CommentAddDto;
import com.hackyle.blog.consumer.dto.OrderItemDto;
import com.hackyle.blog.consumer.dto.PageRequestDto;
import com.hackyle.blog.consumer.dto.PageResponseDto;
import com.hackyle.blog.consumer.entity.CommentEntity;
import com.hackyle.blog.consumer.mapper.CommentMapper;
import com.hackyle.blog.consumer.qo.CommentQo;
import com.hackyle.blog.consumer.service.CommentService;
import com.hackyle.blog.consumer.util.BeanCopyUtils;
import com.hackyle.blog.consumer.util.IDUtils;
import com.hackyle.blog.consumer.util.PaginationUtils;
import com.hackyle.blog.consumer.vo.CommentVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity>
        implements CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentMapper commentMapper;


    @Override
    public ApiResponse<String> add(CommentAddDto addDto) {
        CommentEntity commentEntity = BeanCopyUtils.copy(addDto, CommentEntity.class);
        commentEntity.setTargetId(IDUtils.decryptByAES(addDto.getTargetId()));
        commentEntity.setId(IDUtils.timestampID());

        if(StringUtils.isNotBlank(addDto.getParentId())) {
            commentEntity.setParentId(IDUtils.decryptByAES(addDto.getParentId()));

            String replyWhoId = addDto.getReplyWhoId();
            CommentEntity commentReplyWho = commentMapper.selectById(IDUtils.decryptByAES(replyWhoId));
            commentEntity.setReplyWho(commentReplyWho.getName());
        }

        int insert = commentMapper.insert(commentEntity);

        if(insert != 1) {
            throw new RuntimeException("??????????????????");
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public List<CommentVo> fetchListByHierarchy(long targetId) {
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CommentEntity::getTargetId, targetId)
                .eq(CommentEntity::getDeleted, 0)
                .eq(CommentEntity::getReleased, 1);
        List<CommentEntity> comments = commentMapper.selectList(queryWrapper);

        if(CollectionUtils.isEmpty(comments)) {
            return null;
        }

        //Map????????????ID??????????????????????????????
        Map<Long, List<CommentEntity>> groupByParentIdMap = comments.stream().collect(Collectors.groupingBy(CommentEntity::getParentId));

        //???????????????????????????????????????????????????
        List<CommentEntity> parentComments = groupByParentIdMap.get(-1L).stream().sorted(Comparator.comparing(CommentEntity::getUpdateTime).reversed()).collect(Collectors.toList());
        List<CommentVo> resultComments = new ArrayList<>(parentComments.size());
        for (CommentEntity parentComment : parentComments) {
            CommentVo commentVo = BeanCopyUtils.copy(parentComment, CommentVo.class);
            commentVo.setId(IDUtils.encryptByAES(parentComment.getId()));

            List<CommentEntity> subComments = groupByParentIdMap.get(parentComment.getId());
            if(subComments != null && subComments.size() > 0) {
                List<CommentVo> replyList = BeanCopyUtils.copyList(subComments, CommentVo.class);
                IDUtils.batchEncrypt(subComments, replyList); //?????????ID????????????
                commentVo.setReplyList(replyList);
            }

            resultComments.add(commentVo);
        }

        return resultComments;
    }

    public PageResponseDto<CommentVo> fetchListByHierarchy(PageRequestDto<CommentQo> pageRequestDto) {
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CommentEntity::getParentId, -1L); //???????????????????????????????????????
        //?????????????????????????????????????????????????????????
        queryWrapper.lambda().eq(CommentEntity::getDeleted, 0);
        queryWrapper.lambda().eq(CommentEntity::getReleased, 1);

        //????????????????????????
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setColumn("update_time");
        orderItemDto.setAsc(false);
        List<OrderItemDto> orders = new ArrayList<>();
        orders.add(orderItemDto);
        pageRequestDto.setOrders(orders);

        //?????????????????????
        Page<CommentEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, CommentEntity.class);
        Page<CommentEntity> resultPage = commentMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("???????????????????????????????????????-??????-pageRequestDto={},??????-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        List<CommentEntity> parentCommentEntityList = resultPage.getRecords();
        PageResponseDto<CommentVo> commentVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, CommentVo.class);
        //???????????????????????????
        if(null == parentCommentEntityList || parentCommentEntityList.isEmpty()) {
            return commentVoPageResponseDto;
        }

        //?????????????????????ID
        List<Long> parentIdList = parentCommentEntityList.stream()
                .map(CommentEntity::getId).collect(Collectors.toList());

        //????????????????????????????????????
        List<CommentEntity> childCommentList = commentMapper.selectByParentIds(parentIdList);

        Map<Long, List<CommentEntity>> groupByParentIdMap = childCommentList.stream().collect(Collectors.groupingBy(CommentEntity::getParentId));

        List<CommentVo> resultList = new ArrayList<>();
        for (CommentEntity parentEntity : parentCommentEntityList) {
            CommentVo commentVo = BeanCopyUtils.copy(parentEntity, CommentVo.class);
            //???????????????
            List<CommentEntity> tmpReplyList = groupByParentIdMap.get(parentEntity.getId());
            //List<CommentEntity> tmpReplyList = parentCommentEntityList.stream()
            //        .filter(ele -> Objects.equals(ele.getParentId(), parentEntity.getId())).collect(Collectors.toList());
            List<CommentVo> replyList = BeanCopyUtils.copyList(tmpReplyList, CommentVo.class);

            commentVo.setReplyList(replyList);

            resultList.add(commentVo);
        }

        commentVoPageResponseDto.setRows(resultList);
        return commentVoPageResponseDto;

        ////Map????????????ID??????????????????????????????
        //Map<Long, List<CommentEntity>> commentMap = commentRecords.stream().collect(Collectors.groupingBy(CommentEntity::getParentId, Collectors.toList()));
        ////???????????????????????????????????????????????????
        //List<CommentEntity> parentComments = commentMap.get(-1L).stream().sorted(Comparator.comparing(CommentEntity::getUpdateTime).reversed()).collect(Collectors.toList());
        //
        //List<CommentVo> resultComments = new ArrayList<>(parentComments.size());
        //for (CommentEntity parentComment : parentComments) {
        //    CommentVo commentVo = BeanCopyUtils.copy(parentComment, CommentVo.class);
        //
        //    List<CommentEntity> subComments = commentMap.get(parentComment.getId());
        //    if(subComments != null && subComments.size() > 0) {
        //        List<CommentVo> replyList = BeanCopyUtils.copyList(subComments, CommentVo.class);
        //        commentVo.setReplyList(replyList);
        //    }
        //
        //    resultComments.add(commentVo);
        //}
        //
        //return new PageResponseDto<>(resultComments, resultPage.getTotal());
    }

}
