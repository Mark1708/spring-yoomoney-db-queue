package com.example.dbq;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yoomoney.tech.dbqueue.api.QueueConsumer;
import ru.yoomoney.tech.dbqueue.api.Task;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;

public class TaskRecordConsumer implements QueueConsumer<MessageDto> {

    private static final Logger log = LoggerFactory.getLogger(TaskRecordConsumer.class);

    @Nonnull
    private final QueueConfig queueConfig;
    @Nonnull
    private final AtomicInteger taskConsumedCount;

    public TaskRecordConsumer(@Nonnull QueueConfig queueConfig,
                               @Nonnull AtomicInteger taskConsumedCount) {
        this.queueConfig = requireNonNull(queueConfig);
        this.taskConsumedCount = requireNonNull(taskConsumedCount);
    }

    @Nonnull
    @Override
    public TaskExecutionResult execute(Task<MessageDto> task) {
        log.info("payload={}", task.getPayloadOrThrow());
        taskConsumedCount.incrementAndGet();
        return TaskExecutionResult.finish();
    }

    @Nonnull
    @Override
    public QueueConfig getQueueConfig() {
        return queueConfig;
    }

    @Nonnull
    @Override
    public TaskPayloadTransformer<MessageDto> getPayloadTransformer() {
        return TaskRecordTransformer.getInstance();
    }

    @Override
    public Optional<Executor> getExecutor() {
        return QueueConsumer.super.getExecutor();
    }
}
