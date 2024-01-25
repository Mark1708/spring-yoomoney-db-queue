package com.example.producer.config;

import com.example.dbqueue.config.DatabaseAccessLayer;
import com.example.dbqueue.config.QueueShard;
import com.example.dbqueue.settings.QueueConfig;
import com.example.dbqueue.settings.QueueId;
import com.example.producer.job.MessageProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableAsync
@Configuration
public class DbQueueConfiguration {

    @Bean("queueProducers")
    public Map<String, MessageProducer> queueProducers(
            QueueId queueId,
            QueueConfig queueConfig,
            List<QueueShard<DatabaseAccessLayer>> queueShards
    ) {
        return queueShards.stream()
                .collect(Collectors.toMap(
                        shard -> shard.getShardId().asString(),
                        shard -> new MessageProducer(queueId, queueConfig, shard)
                ));
    }
}
