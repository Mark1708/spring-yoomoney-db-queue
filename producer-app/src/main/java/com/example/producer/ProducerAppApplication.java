package com.example.producer;

import com.example.common.MessageDto;
import com.example.producer.job.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;

@RequiredArgsConstructor
@SpringBootApplication
public class ProducerAppApplication {

    private final MessageProducer producer;

    public static void main(String[] args) {
        SpringApplication.run(ProducerAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            for (int i = 0; i < 2000; i++) {
                producer.enqueue(
                        EnqueueParams.create(
                                new MessageDto("Hello World " + i)
                        )
                );
            }
        };
    }
}
