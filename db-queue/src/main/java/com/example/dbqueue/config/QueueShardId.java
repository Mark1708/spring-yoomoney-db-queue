package com.example.dbqueue.config;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Storage for shard information.
 */
public final class QueueShardId {

    @Nonnull
    private final String id;

    /**
     * Constructor
     *
     * @param id Shard identifier.
     */
    public QueueShardId(@Nonnull String id) {
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Get shard identifier.
     *
     * @return Shard identifier.
     */
    @Nonnull
    public String asString() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        QueueShardId shardId = (QueueShardId) obj;
        return Objects.equals(id, shardId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

}
