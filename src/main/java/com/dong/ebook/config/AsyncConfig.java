package com.dong.ebook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 只要要@EnableAsync就可以使用多线程。使用@Async就可以定义一个线程任务。
 * 通过spring给我们提供的ThreadPoolTaskExecutor就可以使用线程池。
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    private int corePoolSize = 4;
    private int maxPoolSize = 16;
    private int queueCapacity = 8;
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }

}
