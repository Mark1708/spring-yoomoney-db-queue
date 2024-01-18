package com.example.consumer.service;

import com.example.common.MessageDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.yoomoney.tech.dbqueue.api.QueueConsumer;
import ru.yoomoney.tech.dbqueue.config.QueueService;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.config.impl.LoggingTaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.config.impl.LoggingThreadLifecycleListener;

import java.time.Duration;
import java.util.List;

@Service
public class MessageQueueService extends QueueService {


    private final QueueConsumer<MessageDto> consumer;

    public MessageQueueService(List<QueueShard<?>> queueShards, QueueConsumer<MessageDto> consumer) {
        super(queueShards, new LoggingThreadLifecycleListener(),
                new LoggingTaskLifecycleListener());
        this.consumer = consumer;
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
