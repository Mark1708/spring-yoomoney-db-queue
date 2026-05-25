package com.example.consumer.service;

import com.example.common.MessageDto;
import com.example.dbqueue.api.QueueConsumer;
import com.example.dbqueue.config.QueueService;
import com.example.dbqueue.config.QueueShard;
import com.example.dbqueue.config.impl.LoggingTaskLifecycleListener;
import com.example.dbqueue.config.impl.LoggingThreadLifecycleListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessageQueueService extends QueueService {

    private final QueueConsumer<MessageDto> consumer;

    public MessageQueueService(List<QueueShard<?>> queueShards, QueueConsumer<MessageDto> queueConsumer) {
        super(queueShards, new LoggingThreadLifecycleListener(), new LoggingTaskLifecycleListener());
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
