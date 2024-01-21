package org.example.test.job;

import com.example.common.Stat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.test.domain.TestMessageStat;
import org.example.test.repository.TestMessageStatRepository;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final TestMessageStatRepository testMessageStatRepository;

    @RabbitListener(queues = "queue1")
    public void processMyQueue(Stat message) {
        testMessageStatRepository.save(
                TestMessageStat.builder()
                        .sentAt(message.getSentAt())
                        .receivedAt(message.getReceivedAt())
                        .testId(message.getId())
                        .build()
        );
    }
}
