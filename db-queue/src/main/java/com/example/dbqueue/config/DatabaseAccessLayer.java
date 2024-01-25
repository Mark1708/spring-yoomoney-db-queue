package com.example.dbqueue.config;

import com.example.dbqueue.dao.QueueDao;
import com.example.dbqueue.dao.QueuePickTaskDao;
import com.example.dbqueue.settings.FailureSettings;
import com.example.dbqueue.settings.PollSettings;
import com.example.dbqueue.settings.QueueLocation;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Interface for interacting with database
 */
public interface DatabaseAccessLayer {

    /**
     * Get an instance of database-specific DAO based on database type and table schema.
     *
     * @return database-specific DAO instance.
     */
    @Nonnull
    QueueDao getQueueDao();

    /**
     * Create an instance of database-specific DAO based on database type and table schema.
     *
     * @param queueLocation   queue location
     * @param failureSettings settings for handling failures
     * @param pollSettings    settings for polling
     * @return database-specific DAO instance.
     */
    @Nonnull
    QueuePickTaskDao createQueuePickTaskDao(
            @Nonnull QueueLocation queueLocation,
            @Nonnull FailureSettings failureSettings,
            @Nonnull PollSettings pollSettings
    );

    /**
     * Perform an operation in transaction
     *
     * @param <ResultT> result type
     * @param supplier  operation
     * @return result of operation
     */
    <ResultT> ResultT transact(@Nonnull Supplier<ResultT> supplier);

    /**
     * Perform an operation in transaction
     *
     * @param runnable operation
     */
    void transact(@Nonnull Runnable runnable);

    /**
     * Get queue table schema for that database.
     *
     * @return Queue table schema.
     */
    @Nonnull
    QueueTableSchema getQueueTableSchema();

}
