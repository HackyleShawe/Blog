package com.hackyle.blog.customer.handler;

import com.hackyle.blog.common.domain.ApiResponse;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.exception.Page404Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

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
    /**
     * 捕获参数校验注解失败抛出的异常：MethodArgumentNotValidException-Spring封装的参数验证异常处理
     * <p>MethodArgumentNotValidException：作用于 @Validated @Valid 注解，接收参数加上@RequestBody注解（json格式）才会有这种异常。</p>
     *
     * @param e MethodArgumentNotValidException异常信息
     * @return 响应数据
     */
    @ResponseBody
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(n -> String.format("%s: %s", n.getField(), n.getDefaultMessage()))
                .reduce((x, y) -> String.format("%s; %s", x, y))
                .orElse("参数输入有误");
        log.error("MethodArgumentNotValidException异常，参数校验异常：{}", msg);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    /**
     * 捕获参数校验注解失败抛出的异常：ConstraintViolationException-jsr规范中的验证异常，嵌套检验问题
     * <p>ConstraintViolationException：作用于 @NotBlank @NotNull @NotEmpty 注解，校验单个String、Integer、Collection等参数异常处理。</p>
     * <p>注：Controller类上必须添加@Validated注解，否则接口单个参数校验无效</p>
     *
     * @param e ConstraintViolationException异常信息
     * @return 响应数据
     */
    @ResponseBody
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ApiResponse<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String msg = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.error("ConstraintViolationException，参数校验异常：{}", msg);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }
    @ResponseBody
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BindException.class)
    public ApiResponse<?> BindExceptionHandler(BindException e) {
        String msg = e.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("BindException异常：", e);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    //@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(BizException.class)
    public ModelAndView bizExceptionHandler(HttpServletRequest request, BizException exception) {
        log.error("出现BizException异常：", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", HttpStatus.NOT_FOUND);
        modelAndView.addObject("message", exception.getMessage());
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.setViewName("common/404");
        return modelAndView;
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
