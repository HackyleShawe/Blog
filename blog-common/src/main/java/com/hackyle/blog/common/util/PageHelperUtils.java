package com.hackyle.blog.common.util;

import com.github.pagehelper.PageInfo;

import java.util.List;

public class PageHelperUtils {
    /**
     * PageHelper分页中，直接new PageInfo传入的list泛型必须是紧挨着PageHelper.startPage列表查询返回的泛型
     * 例如紧挨着PageHelper.startPage查询List的泛型为XxxEntity，那么返回时的new PageInfo<>中的泛型必须为XxxEntity
     * 否则total值为list的大小，而不为数据库中的总量
     * @param sourceList 例如List<XxxEntity>
     * @param targetClazz 例如List<XxxVo>
     * @return
     * @param <S> Source
     * @param <T> Target
     */
    public static <S, T> PageInfo<T> getPageInfo(List<S> sourceList, Class<T> targetClazz) {
        List<T> targetList = BeanCopyUtils.copyList(sourceList, targetClazz);

        PageInfo<S> sourcePage = new PageInfo<>(sourceList);
        PageInfo<T> targetPage = new PageInfo<>(targetList);
        BeanCopyUtils.copyProperties(sourcePage, targetPage);
        targetPage.setList(targetList);
        return targetPage;
    }
}
