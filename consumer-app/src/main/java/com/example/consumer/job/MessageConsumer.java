package com.example.consumer.job;

import com.example.common.MessageDto;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yoomoney.tech.dbqueue.api.QueueConsumer;
import ru.yoomoney.tech.dbqueue.api.Task;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.Executor;

@Slf4j
@Builder
@RequiredArgsConstructor
public class MessageConsumer implements QueueConsumer<MessageDto> {

    @Nonnull
    private final QueueConfig queueConfig;
    @Nonnull
    @Qualifier("taskExecutor")
    private final Executor taskExecutor;
    @Nonnull
    private final TaskPayloadTransformer<MessageDto> transformer;

    @Nonnull
    @Override
    public TaskExecutionResult execute(@Nonnull Task<MessageDto> task) {
        log.info("payload={}", task.getPayloadOrThrow());
        ZonedDateTime sentAt = task.getCreatedAt();
        ZonedDateTime receivedAt = ZonedDateTime.now(sentAt.getZone());
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
        return transformer;
    }

    @Override
    public Optional<Executor> getExecutor() {
        return Optional.of(taskExecutor);
    }
}
