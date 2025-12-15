package com.hackyle.blog.customer.module.article.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.hackyle.blog.customer.module.article.model.dto.CategoryQueryDto;
import com.hackyle.blog.customer.module.article.model.entity.CategoryEntity;
import com.hackyle.blog.customer.module.article.model.vo.ArticleVo;
import com.hackyle.blog.customer.module.article.model.vo.CategoryVo;
import com.hackyle.blog.customer.module.article.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类：读取所有的文章分类，渲染到页面category.html。
 * 点击分类，获取该目录下的所有文章，category/分类名/页码
 */
@Slf4j
@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    /**
     * 文章分类页
     */
    @GetMapping
    public ModelAndView queryCategory(ModelAndView modelAndView) {
        List<CategoryVo> categoryVoList = categoryService.getCategoryPage();

        modelAndView.addObject("categoryVoList", categoryVoList);
        modelAndView.setViewName("category");
        return modelAndView;
    }

    /**
     * 分页获取某分类下的所有文章
     */
    @GetMapping("/{categoryCode}/{pageNum}")
    public ModelAndView categoryArticle(ModelAndView modelAndView, @PathVariable("categoryCode") String categoryCode,
                                        @PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        CategoryQueryDto queryDto = new CategoryQueryDto();
        pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        String pageSizeStr = request.getParameter("pageSize");
        Integer pageSize = StringUtils.isBlank(pageSizeStr) ? 15 : Integer.parseInt(pageSizeStr);
        queryDto.setPageSize(pageSize);
        queryDto.setPageNum(pageNum);
        queryDto.setCategoryCode(categoryCode);

        //多个关键字，使用逗号分割
        String categoryKeys = request.getParameter("categoryKeys");
        if(StringUtils.isNotBlank(categoryKeys)) {
            queryDto.setQueryKeywords(categoryKeys);
            modelAndView.addObject("categoryKeys", categoryKeys);
        }
        log.info("分页获取某分类下的所有文章-controller入参-queryDto={}", JSON.toJSONString(queryDto));

        CategoryEntity category = categoryService.getOne(Wrappers.<CategoryEntity>lambdaQuery().eq(CategoryEntity::getCode, categoryCode)
                .eq(CategoryEntity::getStatus, Boolean.TRUE)
                .eq(CategoryEntity::getDeleted, Boolean.FALSE)
                .select(CategoryEntity::getName).select(CategoryEntity::getId));

        PageInfo<ArticleVo> pageInfo = null;
        if(category != null) {
            queryDto.setCategoryId(category.getId());
            pageInfo = categoryService.selectCategoryArticles(queryDto);
        }

        modelAndView.addObject("pageInfo", pageInfo);
        modelAndView.addObject("categoryCode", categoryCode);
        modelAndView.addObject("categoryName", category == null ? "" : category.getName());

        modelAndView.setViewName("category");
        return modelAndView;
    }

}
