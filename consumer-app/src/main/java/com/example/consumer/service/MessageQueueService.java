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


    private final List<QueueConsumer<MessageDto>> consumers;

    public MessageQueueService(List<QueueShard<?>> queueShards, List<QueueConsumer<MessageDto>> consumers) {
        super(queueShards, new LoggingThreadLifecycleListener(),
                new LoggingTaskLifecycleListener());
        this.consumers = consumers;
    }

    @PostConstruct
    public void init() {
        consumers.forEach(this::registerQueue);
        this.start();
    }

    @PreDestroy
    public void destroy() {
        this.shutdown();
        this.awaitTermination(Duration.ofSeconds(10));
    }
}
