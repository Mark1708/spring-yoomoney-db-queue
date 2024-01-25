package com.example.producer.controller;

import com.example.common.MessageDto;
import com.example.producer.job.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class TestController {

    private final MessageProducer producer;

    public TestController(
            @Qualifier("queueProducers") Map<String, MessageProducer> queueProducers
    ) {
        this.producer = queueProducers.get("main");;
    }

    private final List<MessageDto> batch = new ArrayList<>();

    @PostMapping
    public void sendMessage(@RequestBody MessageDto messageDto) {
        batch.add(messageDto);

        if (batch.size() == 10) {
            producer.enqueueMessageBatch(batch);
            batch.clear();
        }
    }
}
