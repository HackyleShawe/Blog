package com.hackyle.blog.admin.infrastructure.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IO型任务线程池：异步日志记录 —— 登录日志、操作日志的记录
 */
@Component
@Slf4j
public class LogTaskThreadPool {
    /**
     * CPU核心数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * IO型任务线程池
     */
    private ThreadPoolExecutor ioTaskThreadPool;

    public LogTaskThreadPool() {
        ioTaskThreadPool = new ThreadPoolExecutor(
                CPU_COUNT *2,
                CPU_COUNT *2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),  //todo 设计合理的阻塞队列
                new NamedThreadFactory("blog-admin-log-task"), //线程名称前缀，例如专门用于异步保存日志的命名为log-pool
                new ThreadPoolExecutor.CallerRunsPolicy()  //todo 设计合理的拒绝策略
        );
    }

    /**
     * 自定义线程工厂：命名每个线程
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger counter = new AtomicInteger(1);

        public NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + "-" + counter.getAndIncrement());
            thread.setDaemon(false);
            thread.setUncaughtExceptionHandler((th, ex) -> {
                log.error("LogTask线程 {} 发生异常：", th.getName(), ex);
            });

            return thread;
        }
    }

    /**
     * 执行一个没有返回值的任务
     */
    public void execute(Runnable task) {
        //获取当前线程的 MDC 上下文，便于线程打日志时也能获取到主线程的tranceId
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        ioTaskThreadPool.execute(() -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                task.run();
            } finally {
                MDC.clear(); // 避免线程复用时污染
            }
        });
    }

    /**
     * 执行一个有返回值的任务
     */
    public <T> Future<T> submit(Callable<T> task) {
        //获取当前线程的 MDC 上下文，便于线程打日志时也能获取到主线程的tranceId
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return ioTaskThreadPool.submit(() -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                return task.call();
            } finally {
                MDC.clear(); // 避免线程复用时污染
            }
        });
    }

    /**
     * 实例销毁时关闭线程池
     */
    @PreDestroy
    public void destroy() {
        ioTaskThreadPool.shutdown();
    }
}
