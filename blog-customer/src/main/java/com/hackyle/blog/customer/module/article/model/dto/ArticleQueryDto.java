package com.hackyle.blog.customer.module.article.model.dto;

import com.hackyle.blog.common.base.PageQueryBase;
import lombok.Data;

@Data
public class ArticleQueryDto extends PageQueryBase {

    private String keywords;

}
