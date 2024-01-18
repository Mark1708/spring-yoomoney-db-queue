package com.example.consumer.config;

import com.example.common.MessageDto;
import com.example.common.MessageTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
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

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static java.util.Collections.singletonList;

@Configuration
public class QueueConfiguration {

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
                                .withThreadCount(1).
                                build()
                )
                .withPollSettings(
                        PollSettings.builder()
                                .withBetweenTaskTimeout(Duration.ofMillis(100))
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
                .withTableName("queue_tasks")
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
        return new ForkJoinPool();
    }
}
