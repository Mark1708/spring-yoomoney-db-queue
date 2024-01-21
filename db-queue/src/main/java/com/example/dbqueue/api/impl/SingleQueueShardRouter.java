package com.example.dbqueue.api.impl;

import com.example.dbqueue.api.EnqueueParams;
import com.example.dbqueue.api.QueueShardRouter;
import com.example.dbqueue.config.DatabaseAccessLayer;
import com.example.dbqueue.config.QueueShard;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Shard router without sharding. Might be helpful if you have single database instance.
 *
 * @param <PayloadT>             The type of the payload in the task
 * @param <DatabaseAccessLayerT> The type of the database access layer
 */
public class SingleQueueShardRouter<PayloadT, DatabaseAccessLayerT extends DatabaseAccessLayer>
        implements QueueShardRouter<PayloadT, DatabaseAccessLayerT> {

    @Nonnull
    private final QueueShard<DatabaseAccessLayerT> queueShard;

    /**
     * Constructor
     *
     * @param queueShard queue shard
     */
    public SingleQueueShardRouter(@Nonnull QueueShard<DatabaseAccessLayerT> queueShard) {
        this.queueShard = Objects.requireNonNull(queueShard, "queueShard must not be null");
    }

    @Override
    public QueueShard<DatabaseAccessLayerT> resolveShard(EnqueueParams<PayloadT> enqueueParams) {
        return queueShard;
    }
}
