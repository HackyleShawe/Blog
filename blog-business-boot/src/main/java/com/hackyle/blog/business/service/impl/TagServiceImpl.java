package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.TagAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.TagEntity;
import com.hackyle.blog.business.mapper.TagMapper;
import com.hackyle.blog.business.qo.TagQo;
import com.hackyle.blog.business.service.TagService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
import com.hackyle.blog.business.util.PaginationUtils;
import com.hackyle.blog.business.vo.TagVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class TagServiceImpl implements TagService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);

    @Autowired
    private TagMapper tagMapper;


    @Transactional
    @Override
    public ApiResponse<String> add(TagAddDto tagAddDto) {
        TagEntity tagEntity = BeanCopyUtils.copy(tagAddDto, TagEntity.class);

        //Code重复性检查
        QueryWrapper<TagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TagEntity::getCode, tagEntity.getCode());
        Integer count = tagMapper.selectCount(queryWrapper);
        if(count >= 1) {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "Code已存在，请重新的定义");
        }

        tagEntity.setId(IDUtils.timestampID());
        int inserted = tagMapper.insert(tagEntity);
        if(inserted == 1) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "新增文章标签失败");
        }
    }

    @Transactional
    @Override
    public ApiResponse<String> del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(Long.parseLong(idStr));
        }
        int deleted = tagMapper.logicDeleteByIds(idList);
        if(deleted == idSplit.length) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "删除文章标签失败");
        }
    }

    @Transactional
    @Override
    public ApiResponse<String>  update(TagAddDto addDto) {
        TagEntity tagEntity = BeanCopyUtils.copy(addDto, TagEntity.class);

        //Code重复性检查
        QueryWrapper<TagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TagEntity::getCode, tagEntity.getCode());
        TagEntity checkTagEntity = tagMapper.selectOne(queryWrapper);
        if(checkTagEntity != null && !tagEntity.getId().equals(checkTagEntity.getId())) {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "Code已存在，请重新的定义");
        }

        UpdateWrapper<TagEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(TagEntity::getId, addDto.getId());
        int updated = tagMapper.update(tagEntity, updateWrapper);

        if(updated == 1) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "更新文章标签失败");
        }
    }


    @Override
    public TagVo fetch(long id) {
        TagEntity tagEntity = tagMapper.selectById(id);
        return BeanCopyUtils.copy(tagEntity, TagVo.class);
    }

    @Override
    public PageResponseDto<TagVo> fetchList(PageRequestDto<TagQo> pageRequestDto) {
        QueryWrapper<TagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        //组装查询条件
        TagQo articleCategoryQo = pageRequestDto.getCondition();
        if(articleCategoryQo != null) {
            if(StringUtils.isNotBlank(articleCategoryQo.getName())) {
                queryWrapper.like("name", articleCategoryQo.getName());
            }
            if(StringUtils.isNotBlank(articleCategoryQo.getCode())) {
                queryWrapper.like("code", articleCategoryQo.getCode());
            }
        }

        //分页操作
        Page<TagEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, TagEntity.class);
        Page<TagEntity> resultPage = tagMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        return PaginationUtils.IPage2PageResponse(resultPage, TagVo.class);
    }

    @Override
    public List<TagVo> fetchAll() {
        QueryWrapper<TagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        List<TagEntity> articleCategoryEntityList = tagMapper.selectList(queryWrapper);
        LOGGER.info("获取所有文章分类-查询数据库出参-articleCategoryList={}", JSON.toJSONString(articleCategoryEntityList));

        return BeanCopyUtils.copyList(articleCategoryEntityList, TagVo.class);
    }

}