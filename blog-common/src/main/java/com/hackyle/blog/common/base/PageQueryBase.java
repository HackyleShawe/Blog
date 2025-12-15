package com.hackyle.blog.common.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageQueryBase implements Serializable {
    public Integer pageNum = 1;

    public Integer pageSize = 10;

}
