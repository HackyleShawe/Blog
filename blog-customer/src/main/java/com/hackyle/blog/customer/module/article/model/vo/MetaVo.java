package com.hackyle.blog.customer.module.article.model.vo;

import lombok.Data;

/**
 * 向HTML meta中传递数据
 */
@Data
public class MetaVo {
    private String title;

    private String keywords;

    private String description;

}
