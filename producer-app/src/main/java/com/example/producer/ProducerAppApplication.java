package com.example.producer;

import com.example.dbqueue.settings.QueueConfig;
import com.example.producer.job.MessageProducer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.starter", "com.example.producer"})
@RequiredArgsConstructor
public class ProducerAppApplication {

    private final QueueConfig queueConfig;

    @Qualifier("queueProducers")
    private final Map<String, MessageProducer> queueProducers;

    public static void main(String[] args) {
        SpringApplication.run(ProducerAppApplication.class, args);
    }
}
