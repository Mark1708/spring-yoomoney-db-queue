package com.example.consumer.config;

import com.example.common.MessageDto;
import com.example.common.transformer.MessageTransformer;
import com.example.consumer.job.MessageConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionTemplate;
import ru.yoomoney.tech.dbqueue.api.QueueConsumer;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.config.DatabaseAccessLayer;
import ru.yoomoney.tech.dbqueue.config.DatabaseDialect;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.config.QueueShardId;
import ru.yoomoney.tech.dbqueue.config.QueueTableSchema;
import ru.yoomoney.tech.dbqueue.settings.ExtSettings;
import ru.yoomoney.tech.dbqueue.settings.FailRetryType;
import ru.yoomoney.tech.dbqueue.settings.FailureSettings;
import ru.yoomoney.tech.dbqueue.settings.PollSettings;
import ru.yoomoney.tech.dbqueue.settings.ProcessingMode;
import ru.yoomoney.tech.dbqueue.settings.ProcessingSettings;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.settings.QueueId;
import ru.yoomoney.tech.dbqueue.settings.QueueLocation;
import ru.yoomoney.tech.dbqueue.settings.QueueSettings;
import ru.yoomoney.tech.dbqueue.settings.ReenqueueRetryType;
import ru.yoomoney.tech.dbqueue.settings.ReenqueueSettings;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;

@EnableAsync
@Configuration
public class QueueConfiguration {

    @Value("${system.corePoolSize}")
    private int corePoolSize;

    @Bean
    public AtomicLong messageCounter() {
        return new AtomicLong();
    }

    @Bean
    public AtomicLong spentTimeCounter() {
        return new AtomicLong();
    }

    @Bean
    public SpringDatabaseAccessLayer databaseAccessLayer(
            JdbcTemplate jdbcTemplate,
            TransactionTemplate transactionTemplate
    ) {
        return new SpringDatabaseAccessLayer(
                DatabaseDialect.POSTGRESQL,
                QueueTableSchema.builder().build(),
                jdbcTemplate,
                transactionTemplate
        );
    }

    @Bean
    public List<QueueShard<DatabaseAccessLayer>> queueShards(DatabaseAccessLayer databaseAccessLayer) {
        return singletonList(
                new QueueShard<>(new QueueShardId("main"), databaseAccessLayer)
        );
    }

    @Bean
    public QueueId queueId() {
        return new QueueId("create_order");
    }

    @Bean
    public QueueSettings queueSettings() {
        return QueueSettings.builder()
                .withProcessingSettings(
                        ProcessingSettings.builder()
                                .withProcessingMode(ProcessingMode.USE_EXTERNAL_EXECUTOR)
                                .withThreadCount(corePoolSize).
                                build()
                )
                .withPollSettings(
                        PollSettings.builder()
                                .withBetweenTaskTimeout(Duration.ofMillis(1))
                                .withNoTaskTimeout(Duration.ofMillis(100))
                                .withFatalCrashTimeout(Duration.ofSeconds(1))
                                .build()
                )
                .withFailureSettings(
                        FailureSettings.builder()
                                .withRetryType(FailRetryType.GEOMETRIC_BACKOFF)
                                .withRetryInterval(Duration.ofMinutes(1))
                                .build()
                )
                .withReenqueueSettings(
                        ReenqueueSettings.builder()
                                .withRetryType(ReenqueueRetryType.MANUAL)
                                .build()
                )
                .withExtSettings(
                        ExtSettings.builder()
                                .withSettings(new LinkedHashMap<>())
                                .build()
                )
                .build();
    }

    @Bean
    public QueueLocation queueLocation(QueueId queueId) {
        return QueueLocation.builder()
                .withTableName("queue_tasks_h_8")
                .withQueueId(queueId)
                .build();
    }

    @Bean
    public QueueConfig queueConfig(
            QueueLocation queueLocation,
            QueueSettings queueSettings
    ) {
        return new QueueConfig(queueLocation, queueSettings);
    }

    @Bean
    public TaskPayloadTransformer<MessageDto> transformer() {
        return MessageTransformer.getInstance();
    }

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(100);
        executor.setMaxPoolSize(corePoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setThreadNamePrefix("poolThread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public List<QueueConsumer<MessageDto>> consumers(
            @Nonnull QueueConfig queueConfig,
            @Qualifier("taskExecutor") @Nonnull Executor taskExecutor,
            @Nonnull TaskPayloadTransformer<MessageDto> transformer
    ) {
        return IntStream.range(0, corePoolSize + 1).boxed()
                .map(i ->
                        (QueueConsumer<MessageDto>) MessageConsumer.builder()
                                .queueConfig(queueConfig)
                                .taskExecutor(taskExecutor)
                                .transformer(transformer)
                                .messageCounter(messageCounter())
                                .spentTimeCounter(spentTimeCounter())
                                .build()
                )
                .toList();
    }
}
