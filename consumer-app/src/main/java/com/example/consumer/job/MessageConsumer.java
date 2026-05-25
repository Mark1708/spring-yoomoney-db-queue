package com.example.consumer.job;

import com.example.common.MessageDto;
import com.example.common.Stat;
import com.example.common.transformer.MessageTransformer;
import com.example.dbqueue.api.QueueConsumer;
import com.example.dbqueue.api.Task;
import com.example.dbqueue.api.TaskExecutionResult;
import com.example.dbqueue.api.TaskPayloadTransformer;
import com.example.dbqueue.settings.QueueConfig;
import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
@Builder
@RequiredArgsConstructor
public class MessageConsumer implements QueueConsumer<MessageDto> {

    @Nonnull
    private final QueueConfig queueConfig;

    @Nonnull
    @Qualifier("taskExecutor")
    private final Executor taskExecutor;

    private final RabbitTemplate rabbitTemplate;

    @Nonnull
    private static final TaskPayloadTransformer<MessageDto> transformer = MessageTransformer.getInstance();

    @Nonnull
    @Override
    public TaskExecutionResult execute(@Nonnull Task<MessageDto> task) {
        Stat stat = new Stat(
                task.getPayloadOrThrow().getTestId(), task.getCreatedAt().toLocalDateTime(), LocalDateTime.now());
        rabbitTemplate.convertAndSend("testExchange", "testRoutingKey", stat);
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
