package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.CommentAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.CommentEntity;
import com.hackyle.blog.business.qo.CommentQo;
import com.hackyle.blog.business.vo.CommentVo;

public interface CommentService extends IService<CommentEntity>  {
    ApiResponse<String> add(CommentAddDto addDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String> delReal(String ids);

    ApiResponse<String> update(CommentAddDto updateDto);

    PageResponseDto<CommentVo> fetchListByHierarchy(PageRequestDto<CommentQo> pageRequestDto);

    PageResponseDto<CommentVo> fetchList(PageRequestDto<CommentQo> pageRequestDto);
}
