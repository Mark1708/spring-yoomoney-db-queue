package com.example.dbq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;
import ru.yoomoney.tech.dbqueue.api.QueueProducer;
import ru.yoomoney.tech.dbqueue.api.Task;
import ru.yoomoney.tech.dbqueue.api.TaskRecord;
import ru.yoomoney.tech.dbqueue.config.DatabaseAccessLayer;
import ru.yoomoney.tech.dbqueue.config.QueueService;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.dao.QueuePickTaskDao;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.settings.QueueSettings;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class SpringYoomoneyDbQueueApplication {

    private static final Logger log = LoggerFactory.getLogger(SpringYoomoneyDbQueueApplication.class);

    private final QueueConfig queueConfig;
    private final QueueService queueService;
    private final QueueProducer<MessageDto> producer;
    private final SpringDatabaseAccessLayer databaseAccessLayer;
    private final QueueShard<DatabaseAccessLayer> queueShard;
    private final QueueSettings queueSettings;

    public SpringYoomoneyDbQueueApplication(
            QueueConfig queueConfig,
            QueueService queueService,
            QueueProducer<MessageDto> producer,
            SpringDatabaseAccessLayer databaseAccessLayer,
            QueueShard<DatabaseAccessLayer> queueShard, QueueSettings queueSettings
    ) {
        this.queueConfig = queueConfig;
        this.queueService = queueService;
        this.producer = producer;
        this.databaseAccessLayer = databaseAccessLayer;
        this.queueShard = queueShard;
        this.queueSettings = queueSettings;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringYoomoneyDbQueueApplication.class, args);
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            AtomicInteger taskConsumedCount1 = new AtomicInteger(0);
            AtomicInteger taskConsumedCount2 = new AtomicInteger(0);
            TaskRecordConsumer consumer1 = new TaskRecordConsumer(queueConfig, taskConsumedCount1);
            TaskRecordConsumer consumer2 = new TaskRecordConsumer(queueConfig, taskConsumedCount2);

            queueService.start();
            queueService.registerQueue(consumer1);

            producer.enqueue(
                    EnqueueParams.create(
                            new MessageDto("Hello")
                    )
            );
            Thread.sleep(500);
            queueService.pause();
            producer.enqueue(
                    EnqueueParams.create(
                            new MessageDto("World")
                    )
            );
            Thread.sleep(500);

            queueService.unpause();
            Thread.sleep(500);
            QueuePickTaskDao queuePickTaskDao = databaseAccessLayer
                    .createQueuePickTaskDao(queueConfig.getLocation(), queueSettings.getFailureSettings());
            TaskRecord taskRecord1 = queuePickTaskDao.pickTask();
            consumer1.execute(
                    Task.<MessageDto> builder(queueShard.getShardId())
                            .withCreatedAt(taskRecord1.getCreatedAt())
                            .withAttemptsCount(taskRecord1.getAttemptsCount())
                            .withPayload(consumer1.getPayloadTransformer().toObject(taskRecord1.getPayload()))
                            .withExtData(taskRecord1.getExtData())
                            .withReenqueueAttemptsCount(taskRecord1.getReenqueueAttemptsCount())
                            .withTotalAttemptsCount(taskRecord1.getTotalAttemptsCount())
                            .build()
            );
            TaskRecord taskRecord2 = queuePickTaskDao.pickTask();
            consumer2.execute(
                    Task.<MessageDto> builder(queueShard.getShardId())
                            .withCreatedAt(taskRecord2.getCreatedAt())
                            .withAttemptsCount(taskRecord2.getAttemptsCount())
                            .withPayload(consumer1.getPayloadTransformer().toObject(taskRecord2.getPayload()))
                            .withExtData(taskRecord2.getExtData())
                            .withReenqueueAttemptsCount(taskRecord2.getReenqueueAttemptsCount())
                            .withTotalAttemptsCount(taskRecord2.getTotalAttemptsCount())
                            .build()
            );
            log.info("Consumer 1: " + taskConsumedCount1.get());
            log.info("Consumer 2: " + taskConsumedCount1.get());
            queueService.shutdown();
            queueService.awaitTermination(Duration.ofSeconds(10));
        };
    }
}
