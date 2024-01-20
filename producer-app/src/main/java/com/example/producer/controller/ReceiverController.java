package com.example.producer.controller;

import com.example.common.MessageDto;
import com.example.producer.job.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;

@RestController
@RequiredArgsConstructor
public class ReceiverController {

    private final MessageProducer producer;

    @GetMapping
    public String receive() {
        producer.enqueue(
                EnqueueParams.create(
                        new MessageDto("Hello World!")
                )
        );
        return "Ok";
    }
}
