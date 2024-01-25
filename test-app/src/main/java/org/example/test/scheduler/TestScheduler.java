package org.example.test.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.test.domain.Test;
import org.example.test.domain.TestMessageStat;
import org.example.test.repository.TestMessageStatRepository;
import org.example.test.repository.TestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TestScheduler {

    private final TestRepository testRepository;
    private final TestMessageStatRepository messageStatRepository;

    @Scheduled(fixedDelay = 1000)
    public void test() {
        Optional<Test> test = testRepository.findByStartAt(null);
        test.ifPresent(value -> {
            Test test1 = value;
            Optional<TestMessageStat> message = messageStatRepository.findFirstMessage(test1.getId());
            message.ifPresent(testMessageStat -> test1.setStartAt(testMessageStat.getSentAt()));
            testRepository.save(test1);
        });
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void test2() {
        Optional<Test> test = testRepository.findByEndAt(null);
        test.ifPresent(value -> {
            Test test1 = value;
            Optional<TestMessageStat> message = messageStatRepository.findLastMessage(test1.getId());
            message.ifPresent(testMessageStat -> {
                if (10 > Duration.between(LocalDateTime.now(), testMessageStat.getReceivedAt()).toMinutes()) {
                    test1.setEndAt(testMessageStat.getReceivedAt());
                    test1.setStatus(1);
                    test1.setMessageCount(messageStatRepository.countByTestId(test1.getId()));

                    testRepository.save(test1);
                }
            });
        });
    }
}
