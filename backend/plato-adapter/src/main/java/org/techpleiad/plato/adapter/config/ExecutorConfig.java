package org.techpleiad.plato.adapter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@RefreshScope
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = ExecutorConfig.PREFIX)
public class ExecutorConfig {

    public static final String PREFIX = "plato.multithreading.config.executor-service";
    public static final String ASYNC_EXECUTOR = "AsyncExecutor";

    private Integer corePoolSize = 1;
    private Integer maxPoolSize = 1;
    private Integer queueCapacity = 10;

    @Bean(name = ASYNC_EXECUTOR)
    public Executor createAsyncExecutorBean() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(ASYNC_EXECUTOR + "-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
