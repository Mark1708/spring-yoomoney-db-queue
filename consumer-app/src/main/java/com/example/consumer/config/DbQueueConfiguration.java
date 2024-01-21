package com.example.consumer.config;

import com.example.common.MessageDto;
import com.example.consumer.job.MessageConsumer;
import com.example.dbqueue.api.QueueConsumer;
import com.example.dbqueue.settings.QueueConfig;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class DbQueueConfiguration {

    private final RabbitTemplate rabbitTemplate;

    @Bean
    public QueueConsumer<MessageDto> queueConsumer(
            QueueConfig queueConfig,
            @Qualifier("taskExecutor") @Nonnull Executor taskExecutor
    ) {
        return MessageConsumer.builder()
                .queueConfig(queueConfig)
                .taskExecutor(taskExecutor)
                .rabbitTemplate(rabbitTemplate)
                .build();
    }
}
