package com.example.producer.job;

import com.example.common.MessageDto;
import com.example.common.transformer.MessageTransformer;
import com.example.dbqueue.api.EnqueueParams;
import com.example.dbqueue.api.EnqueueResult;
import com.example.dbqueue.api.TaskPayloadTransformer;
import com.example.dbqueue.api.impl.MonitoringQueueProducer;
import com.example.dbqueue.api.impl.ShardingQueueProducer;
import com.example.dbqueue.api.impl.SingleQueueShardRouter;
import com.example.dbqueue.config.QueueShard;
import com.example.dbqueue.settings.QueueConfig;
import com.example.dbqueue.settings.QueueId;
import com.example.starter.SpringDatabaseAccessLayer;

import javax.annotation.Nonnull;
import java.util.List;

public class MessageProducer extends MonitoringQueueProducer<MessageDto> {

    private final static TaskPayloadTransformer<MessageDto> transformer = MessageTransformer.getInstance();

    public MessageProducer(
            QueueId queueId,
            QueueConfig config,
            QueueShard<?> queueShard
    ) {
        super(
                new ShardingQueueProducer<>(
                        config,
                        transformer,
                        new SingleQueueShardRouter<>(queueShard)
                ),
                queueId
        );
    }

    public EnqueueResult enqueueMessage(@Nonnull MessageDto messageDto) {
        return super.enqueue(EnqueueParams.create(messageDto));
    }

    public void enqueueMessageBatch(List<MessageDto> messageDtos) {
        super.enqueueBatch(
                messageDtos.stream()
                        .map(EnqueueParams::create)
                        .toList()
        );
    }

    @Nonnull
    @Override
    public TaskPayloadTransformer<MessageDto> getPayloadTransformer() {
        return transformer;
    }
}
