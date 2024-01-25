package com.example.dbqueue.config.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.dbqueue.config.QueueShardId;
import com.example.dbqueue.config.ThreadLifecycleListener;
import com.example.dbqueue.settings.QueueLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Thread listener with logging support
 */
public class LoggingThreadLifecycleListener implements ThreadLifecycleListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingThreadLifecycleListener.class);

    @Override
    public void crashed(@Nonnull QueueShardId shardId, @Nonnull QueueLocation location,
                        @Nullable Throwable exc) {
        log.error("fatal error in queue thread: shardId={}, location={}", shardId.asString(),
                location, exc);
    }
}
