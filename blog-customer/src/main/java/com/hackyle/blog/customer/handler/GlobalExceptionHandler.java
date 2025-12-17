package com.hackyle.blog.customer.handler;

import com.hackyle.blog.common.domain.ApiResponse;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.exception.Page404Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * ControllerAdvice、RestControllerAdvice表示这是一个控制器增强类，当控制器发生异常且符合类中定义的拦截异常类，将会被拦截。
 * 在捕获异常时是按照异常方法的顺序依次捕获(类似于catch关键字后的捕获顺序)，所以需要将顶级的异常放在最后
 * 注意：
 * RestControllerAdvice修饰的类，并不一定捕获@RestController修饰的controller类抛出的异常
 * ControllerAdvice修饰的类，并不一定捕获@Controller修饰的controller类抛出的异常。
 *
 * 需要跳转到页面的异常
 * Page404Exception：跳转到404页面
 * Page500Exception：跳转到500页面
 *
 * 需要返回JSON串的异常
 * IllegalArgumentException：参数错误，400
 * AuthenticationException：认证错误，401
 * PermissionException：权限错误，403
 * NotFoundException：资源不存在，404
 * BizException：业务异常，422，例如：用户名密码错误、库存不足
 * Exception、Error：500
 */
@ControllerAdvice
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    @ExceptionHandler(BizException.class)
    public ApiResponse<Exception> bizExceptionHandler(HttpServletRequest request, BizException exception) {
        log.error("出现BizException异常：", exception);
        return ApiResponse.fail(HttpStatus.UNPROCESSABLE_ENTITY.value(), exception.getMessage());
    }

    //@ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = Page404Exception.class)
    public ModelAndView page404ExceptionHandler(HttpServletRequest request, Page404Exception exception) {
        log.error("出现Page404Exception异常：", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", HttpStatus.NOT_FOUND);
        modelAndView.addObject("message", exception.getMessage());
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.setViewName("common/404");
        return modelAndView;
    }

    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ModelAndView page500ExceptionHandler(HttpServletRequest request, Exception exception) {
        log.error("出现Exception异常：exception=", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("message", "服务器内部错误");
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.setViewName("common/500");
        return modelAndView;
    }


}
