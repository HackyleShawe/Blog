package com.hackyle.blog.admin.infrastructure.aspect;

import com.hackyle.blog.admin.infrastructure.threadpool.LogTaskThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 记录操作日志的切面
 */
@Aspect
@Component
@Slf4j
public class OperateLogAspect {
    //@Autowired
    //private SysOperateLogService sysOperateLogService;
    @Autowired
    private LogTaskThreadPool logTaskThreadPool;


    /**
     * 定义每个controller方法为切入点
     * Pointcut：切入点
     * execution内指定的表达式就是'Join point（连接点）'
     */
    @Pointcut("execution(public * com.hackyle.blog.admin.module..controller.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        //SysOperateLogEntity operateLog = new SysOperateLogEntity();
        //operateLog.setUserId(); todo 从Security上下文中获取
        //operateLog.setIp();
        //operateLog.setIpLocation();

        //// 获取请求相关信息
        //ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        //if(request != null) {
        //    operateLog.setUrl(request.getRequestURI());
        //}
        //
        //// 获取方法签名
        //Signature signature =  joinPoint.getSignature();
        //String className = signature.getDeclaringTypeName();
        //String methodName = signature.getName();
        //operateLog.setCalledMethod(className + "#" + methodName);
        //
        //// 获取入参
        //Object[] args = joinPoint.getArgs();
        //operateLog.setRequestParam(JSON.toJSONString(args));

        // 执行目标方法
        Object result = joinPoint.proceed();

        //long endTime = System.currentTimeMillis();
        //operateLog.setResponseBody(JSON.toJSONString(result));
        //operateLog.setTimeUse((int)(endTime - startTime));
        //
        ////提交任务到线程池，异步保存日志，注意：将耗时的操作放在异步线程中完成，例如解析IP地址
        //logTaskThreadPool.execute(() -> sysOperateLogService.add(operateLog));

        return result;
    }

}
