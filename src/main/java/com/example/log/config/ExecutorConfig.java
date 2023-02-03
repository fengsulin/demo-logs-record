package com.example.log.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 日志记录异步线程池配置类
 */
@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {
    /**核心线程数*/
    private int corePoolSize = 10;
    /**最大线程数*/
    private int maxPoolSize = 20;
    /**允许线程空闲时间*/
    private static final int keepAliveTime = 60;
    /**缓冲队列大小*/
    private int queueCapacity = 300;
    private String threadPrefix = "XLog-thread-";

    @Bean
    public Executor asyncServiceExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 配置队列大小
        executor.setQueueCapacity(corePoolSize);
        // 配置线程前缀名
        executor.setThreadNamePrefix(threadPrefix);
        // 配置线程空闲时间
        executor.setKeepAliveSeconds(keepAliveTime);
        // 当pool达到最大时，不在新线程中执行任务，而是由调用者所在的线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程池初始化
        executor.initialize();
        log.info("[asyncServiceExecutor异步线程池]:初始化完成");
        return executor;
    }
}
