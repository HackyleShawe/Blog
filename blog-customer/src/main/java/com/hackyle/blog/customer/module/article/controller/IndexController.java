package com.hackyle.blog.customer.module.article.controller;

import com.github.pagehelper.PageInfo;
import com.hackyle.blog.customer.module.article.model.dto.ArticleQueryDto;
import com.hackyle.blog.customer.module.article.model.vo.ArticleVo;
import com.hackyle.blog.customer.module.article.service.ArticleService;
import com.hackyle.blog.customer.module.article.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class IndexController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 首页
     */
    @RequestMapping(value = {"/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index(ModelAndView modelAndView) {
        ArticleQueryDto articleQueryDto = new ArticleQueryDto();
        articleQueryDto.setPageSize(15);
        articleQueryDto.setPageNum(1);

        PageInfo<ArticleVo> pageInfo = articleService.list(articleQueryDto);

        modelAndView.addObject("pageInfo", pageInfo);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    /**
     * 问题反馈或者留言
     */
    @RequestMapping(value = {"/feedback"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView feedbackMessage(ModelAndView modelAndView) {
        modelAndView.setViewName("feedback");
        return modelAndView;
    }

    /**
     * 关于页
     */
    @RequestMapping(value = {"/about"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView about(ModelAndView modelAndView) {
        modelAndView.setViewName("about");
        return modelAndView;
    }
}
