package com.example.consumer.service;

import com.example.common.MessageDto;
import com.example.consumer.job.MessageConsumer;
import com.example.dbqueue.api.QueueConsumer;
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

    private final QueueConsumer<MessageDto> consumer;

    public MessageQueueService(List<QueueShard<?>> queueShards, QueueConsumer<MessageDto> queueConsumer) {
        super(queueShards, new LoggingThreadLifecycleListener(),
                new LoggingTaskLifecycleListener());
        this.consumer = queueConsumer;
    }

    @PostConstruct
    public void init() {
        this.registerQueue(consumer);
        this.start();
    }

    @PreDestroy
    public void destroy() {
        this.shutdown();
        this.awaitTermination(Duration.ofSeconds(10));
    }
}
