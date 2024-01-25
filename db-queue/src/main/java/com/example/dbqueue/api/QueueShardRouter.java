package com.example.dbqueue.api;

import com.example.dbqueue.config.DatabaseAccessLayer;
import com.example.dbqueue.config.QueueShard;

/**
 * Dispatcher for sharding support.
 * <p>
 * It evaluates designated shard based on task parameters.
 *
 * @param <PayloadT>             The type of the payload in the task
 * @param <DatabaseAccessLayerT> The type of the database access layer
 */
public interface QueueShardRouter<PayloadT, DatabaseAccessLayerT extends DatabaseAccessLayer> {

    /**
     * Get designated shard for task parameters
     *
     * @param enqueueParams Parameters with typed payload to enqueue the task
     * @return Shard where task will be processed on
     */
    QueueShard<DatabaseAccessLayerT> resolveShard(EnqueueParams<PayloadT> enqueueParams);
}
