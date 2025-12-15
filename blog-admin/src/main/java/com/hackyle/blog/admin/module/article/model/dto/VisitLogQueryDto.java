package com.hackyle.blog.admin.module.article.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;

@Data
public class VisitLogQueryDto extends PageQueryBase {
    private Long articleId;

    private String startTime;

    private String endTime;

}
