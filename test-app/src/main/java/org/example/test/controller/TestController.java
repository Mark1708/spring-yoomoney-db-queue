package org.example.test.controller;

import com.example.common.MessageDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.test.domain.Test;
import org.example.test.dto.TestResult;
import org.example.test.repository.TestRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequiredArgsConstructor
public class TestController {

    private static boolean isRunning = false;

    @Qualifier("applicationTaskExecutor")
    private final Executor taskExecutor;

    private final TestRepository testRepository;

    @Value("#{'${producers}'.split(',')}")
    private final List<String> producers;

    @GetMapping
    public List<TestResult> tests() {
        return testRepository.findAll().stream().map(TestResult::new).toList();
    }

    @PostMapping
    public String startTest(Integer testDataCount) {
        List<RestClient> restClients = producers.stream()
                .map(url -> RestClient.builder()
                        .baseUrl(url)
                        .build())
                .toList();
        if (isRunning) {
            return "Test is already running";
        }
        isRunning = true;

        Test test = saveTest(Test.builder().status(0).build());
        AtomicInteger count = new AtomicInteger(0);
        restClients.forEach(client -> {
            taskExecutor.execute(() -> {
                while (count.incrementAndGet() <= testDataCount) {
                    client.post()
                            .contentType(APPLICATION_JSON)
                            .body(
                                    new MessageDto("Hello " + LocalDateTime.now(), test.getId())
                            )
                            .retrieve()
                            .toEntity(Void.class);
                }
                isRunning = false;
            });
        });
        return "Successfully started test with id - " + test.getId();
    }

    @Transactional
    public Test saveTest(Test test) {
        return testRepository.saveAndFlush(test);
    }
}
