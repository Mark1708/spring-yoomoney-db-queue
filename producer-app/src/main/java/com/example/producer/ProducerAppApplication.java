package com.example.producer;

import com.example.common.MessageDto;
import com.example.dbqueue.settings.QueueConfig;
import com.example.producer.job.MessageProducer;
import com.example.starter.properties.PollSettingsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.starter",
                "com.example.producer"
        }
)
@RequiredArgsConstructor
public class ProducerAppApplication {

    private final QueueConfig queueConfig;
    @Qualifier("queueProducers")
    private final Map<String, MessageProducer> queueProducers;

    public static void main(String[] args) {
        SpringApplication.run(ProducerAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
//            queueConfig.getSettings();
//            MessageProducer producer = queueProducers.get("main");
//            int messagesCount = 100;
//            int messagesInBatch = 10;
//
//            int batchCount = messagesCount / messagesInBatch;
//            IntStream.range(0, batchCount).boxed()
//                    .forEach(batchNum -> {
//                        List<MessageDto> messages = IntStream.range(0, messagesInBatch).boxed()
//                                .map(messageInBatchNum -> {
//                                    String message = "Message " + (batchNum + 1) + "." + (messageInBatchNum + 1) + " / total: " + (batchNum * 10 + (messageInBatchNum) + 1);
//                                    if (batchNum == 0 && messageInBatchNum == 0) {
//                                        return new MessageDto(message, true, false);
//                                    } else if (batchNum == batchCount && messageInBatchNum == messagesInBatch) {
//                                        return new MessageDto(message, false, true);
//                                    } else {
//                                        return new MessageDto(message);
//                                    }
//                                })
//                                .toList();
//                        producer.enqueueMessageBatch(messages);
//                    });
        };
    }
}
