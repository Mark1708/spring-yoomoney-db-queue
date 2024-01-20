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
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageQueueService extends QueueService {

    private final AtomicLong messageCounter;
    private final AtomicLong spentTimeCounter;
    private final List<QueueConsumer<MessageDto>> consumers;

    public MessageQueueService(List<QueueShard<?>> queueShards, AtomicLong messageCounter, AtomicLong spentTimeCounter, List<QueueConsumer<MessageDto>> consumers) {
        super(queueShards, new LoggingThreadLifecycleListener(),
                new LoggingTaskLifecycleListener());
        this.messageCounter = messageCounter;
        this.spentTimeCounter = spentTimeCounter;
        this.consumers = consumers;
    }

    @PostConstruct
    public void init() {
        consumers.forEach(this::registerQueue);
        this.start();
    }

    @PreDestroy
    public void destroy() {
        System.out.println("====================================  Start Statistics  ====================================");
        System.out.println("Messages: " + messageCounter.get());
        System.out.println("Time: " + spentTimeCounter.get());
        System.out.println("RPS: " +  messageCounter.get() /  spentTimeCounter.get() * 1000L);
        System.out.println("====================================  End Statistics  ====================================");
        this.shutdown();
        this.awaitTermination(Duration.ofSeconds(10));
    }
}
