package com.hackyle.blog.customer.module.article.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Data
public class CategoryQueryDto extends PageQueryBase {

    private String categoryCode;

    @JsonIgnore
    private Long categoryId;

    private String categoryName;

    private String queryKeywords;


}
