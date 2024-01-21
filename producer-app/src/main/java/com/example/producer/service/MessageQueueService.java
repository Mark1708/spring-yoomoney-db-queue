package com.example.producer.service;

import com.example.dbqueue.config.QueueService;
import com.example.dbqueue.config.QueueShard;
import com.example.dbqueue.config.impl.LoggingTaskLifecycleListener;
import com.example.dbqueue.config.impl.LoggingThreadLifecycleListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

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
