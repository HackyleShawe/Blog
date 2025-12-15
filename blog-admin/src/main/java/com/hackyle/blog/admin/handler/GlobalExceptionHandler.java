package com.hackyle.blog.admin.handler;

import com.hackyle.blog.common.domain.ApiResponse;
import com.hackyle.blog.common.exception.AuthenticationException;
import com.hackyle.blog.common.exception.BizException;
import com.hackyle.blog.common.exception.NotFoundException;
import com.hackyle.blog.common.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * ControllerAdvice表示这是一个控制器增强类，当控制器发生异常且符合类中定义的拦截异常类，将会被拦截。
 * 在捕获异常时是按照异常方法的顺序依次捕获(类似于catch关键字后的捕获顺序)，所以需要将顶级的异常放在最后
 * 注意：各个异常处理方法要同名，采取重载的形式
 *
 * IllegalArgumentException：参数错误，400
 * AuthenticationException：认证错误，401
 * PermissionException：权限错误，403
 * NotFoundException：资源不存在，404
 * BizException：业务异常，422，例如：用户名密码错误、库存不足
 * Exception、Error：500
 */
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ApiResponse<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String msg = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.error("ConstraintViolationException，参数校验异常：{}", msg);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), msg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Exception> badRequestExceptionHandler(HttpServletRequest request, IllegalArgumentException exception) {
        log.error("出现IllegalArgumentException异常：", exception);
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), "参数错误");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<Exception> unauthorizedExceptionHandler(HttpServletRequest request, AuthenticationException exception) {
        log.error("出现AuthenticationException异常：", exception);
        return ApiResponse.fail(HttpStatus.UNAUTHORIZED.value(), "认证失败");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    @ExceptionHandler(PermissionException.class)
    public ApiResponse<Exception> permissionExceptionHandler(HttpServletRequest request, PermissionException exception) {
        log.error("出现PermissionException异常：", exception);
        return ApiResponse.fail(HttpStatus.FORBIDDEN.value(), "权限不足");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<Exception> notFoundExceptionHandler(HttpServletRequest request, NotFoundException exception) {
        log.error("出现NotFoundException异常：", exception);
        return ApiResponse.fail(HttpStatus.NOT_FOUND.value(), "资源不存在");
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    @ExceptionHandler(BizException.class)
    public ApiResponse<Exception> bizExceptionHandler(HttpServletRequest request, BizException exception) {
        log.error("出现BizException异常：", exception);
        return ApiResponse.fail(HttpStatus.UNPROCESSABLE_ENTITY.value(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ApiResponse<Exception> exceptionHandler(HttpServletRequest request, Exception exception) {
        log.error("出现Exception异常：", exception);
        //不要直接把exception.getMessage()抛给前端，可能包含整个异常栈信息，造成信息泄露
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
    }

}
