package com.hackyle.blog.customer.module.article.controller;

import com.github.pagehelper.PageInfo;
import com.hackyle.blog.customer.infrastructure.threadpool.LogTaskThreadPool;
import com.hackyle.blog.customer.module.article.model.dto.ArticleQueryDto;
import com.hackyle.blog.customer.module.article.model.entity.VisitLogEntity;
import com.hackyle.blog.customer.module.article.model.vo.ArticleVo;
import com.hackyle.blog.customer.module.article.model.vo.CommentVo;
import com.hackyle.blog.customer.module.article.model.vo.MetaVo;
import com.hackyle.blog.customer.module.article.service.ArticleService;
import com.hackyle.blog.customer.module.article.service.CommentService;
import com.hackyle.blog.customer.module.article.service.VisitLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private VisitLogService visitLogService;
    @Autowired
    private LogTaskThreadPool logTaskThreadPool;


    /**
     * 分页获取所有文章
     */
    @RequestMapping(value = {"/page/{pageNum}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView pageByNum(ModelAndView modelAndView, @PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        ArticleQueryDto articleQueryDto = new ArticleQueryDto();

        articleQueryDto.setPageNum(pageNum == null || pageNum < 1 ? 1 : pageNum);
        String pageSize = request.getParameter("pageSize");
        int pageSizeInt = 15;
        if(StringUtils.isNotBlank(pageSize)) {
            try {
                pageSizeInt = Integer.parseInt(pageSize);
            } catch (NumberFormatException e) {
                log.error("pageByNum方法格式化pageSize失败，使用默认值，入参pageSize={}", pageSize);
            }
        }
        articleQueryDto.setPageSize(pageSizeInt);

        //多个关键字，使用逗号分割
        String queryKeywords = request.getParameter("query");
        articleQueryDto.setKeywords(queryKeywords);
        modelAndView.addObject("queryKeywords", queryKeywords);

        PageInfo<ArticleVo> pageInfo = articleService.list(articleQueryDto);

        modelAndView.addObject("pageInfo", pageInfo);
        modelAndView.setViewName("index");
        return modelAndView;
    }


    /**
     * 如果文章没有categoryCode则走这里
     */
    @GetMapping("/{articleCode}")
    public ModelAndView articleDetail(ModelAndView modelAndView, HttpServletRequest request,
                                      @PathVariable("articleCode") String articleCode) {
        if(StringUtils.isBlank(articleCode)) {
            modelAndView.setViewName("common/404");
            return modelAndView;
        }
        long startTime = System.currentTimeMillis();

        //查文章主体内容
        ArticleVo articleVo = articleService.get(articleCode);
        if(articleVo == null) {
            modelAndView.setViewName("common/404");
            return modelAndView;
        }
        modelAndView.addObject("articleVo", articleVo);

        MetaVo metaVo = new MetaVo();
        metaVo.setTitle(articleVo.getTitle());
        metaVo.setDescription(articleVo.getSummary());
        modelAndView.addObject("metaVo", metaVo);

        //查文章顶级评论
        List<CommentVo> commentVos = commentService.getTopByArticle(articleVo.getId());
        modelAndView.addObject("commentVos", commentVos);

        //保存访问日志。为什么不在service做？统计耗时时更精确
        VisitLogEntity visitLogEntity = new VisitLogEntity();
        visitLogEntity.setArticleId(articleVo.getId());
        visitLogEntity.setTimeUse((int) (System.currentTimeMillis() - startTime));
        //提交任务到线程池，异步保存日志，注意：将耗时的操作放在异步线程中完成，例如解析IP地址
        logTaskThreadPool.execute(() -> visitLogService.add(visitLogEntity));


        modelAndView.setViewName("article");
        return modelAndView;
    }

    /**
     * 文章详情
     * 路径格式：/{categoryCode}/{articleCode}
     * 兼容以前的老格式，便于SEO
     */
    @GetMapping("/{categoryCode}/{articleCode}")
    public ModelAndView articleDetail(ModelAndView modelAndView, HttpServletRequest request,
                                      @PathVariable("categoryCode") String categoryCode, @PathVariable("articleCode") String articleCode) {
        if(StringUtils.isBlank(categoryCode) || StringUtils.isBlank(articleCode)) {
            modelAndView.setViewName("index");
            return modelAndView;
        }
        long startTime = System.currentTimeMillis();

        //查文章主体内容，文章完整path为/{categoryCode}/{articleCode}
        ArticleVo articleVo = articleService.get(categoryCode, articleCode);
        modelAndView.addObject("articleVo", articleVo);

        MetaVo metaVo = new MetaVo();
        metaVo.setTitle(articleVo.getTitle());
        metaVo.setDescription(articleVo.getSummary());
        metaVo.setKeywords(articleVo.getKeywords());
        modelAndView.addObject("metaVo", metaVo);

        //查文章顶级评论
        List<CommentVo> commentVos = commentService.getTopByArticle(articleVo.getId());
        modelAndView.addObject("commentVos", commentVos);

        //保存访问日志。为什么不在service做？统计耗时时更精确
        VisitLogEntity visitLogEntity = new VisitLogEntity();
        visitLogEntity.setArticleId(articleVo.getId());
        visitLogEntity.setTimeUse((int) (System.currentTimeMillis() - startTime));
        //提交任务到线程池，异步保存日志，注意：将耗时的操作放在异步线程中完成，例如解析IP地址
        logTaskThreadPool.execute(() -> visitLogService.add(visitLogEntity));

        modelAndView.setViewName("article");
        return modelAndView;
    }

}
