package com.example.producer.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.yoomoney.tech.dbqueue.config.QueueService;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.config.impl.LoggingTaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.config.impl.LoggingThreadLifecycleListener;

import java.time.Duration;
import java.util.List;

@Service
public class MessageQueueService extends QueueService {

    public MessageQueueService(List<QueueShard<?>> queueShards) {
        super(queueShards, new LoggingThreadLifecycleListener(),
                new LoggingTaskLifecycleListener());
    }

    @PostConstruct
    public void init() {
        this.start();
    }

    @PreDestroy
    public void destroy() {
        this.shutdown();
        this.awaitTermination(Duration.ofSeconds(10));
    }
}
