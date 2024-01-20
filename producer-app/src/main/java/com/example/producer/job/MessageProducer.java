package com.example.producer.job;

import com.example.common.MessageDto;
import org.springframework.stereotype.Component;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.api.impl.MonitoringQueueProducer;
import ru.yoomoney.tech.dbqueue.api.impl.ShardingQueueProducer;
import ru.yoomoney.tech.dbqueue.api.impl.SingleQueueShardRouter;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.settings.QueueId;

import java.util.List;

@Component
public class MessageProducer extends MonitoringQueueProducer<MessageDto> {


    public MessageProducer(
            QueueId queueId,
            QueueConfig config,
            List<QueueShard<?>> queueShards,
            TaskPayloadTransformer<MessageDto> transformer
    ) {
        super(
                new ShardingQueueProducer<>(
                        config,
                        transformer,
                        new SingleQueueShardRouter<>(queueShards.get(0))
                ),
                queueId
        );
    }

}
