package com.example.starter;

import com.example.dbqueue.dao.QueueDao;
import com.example.dbqueue.dao.QueuePickTaskDao;
import com.example.dbqueue.settings.FailureSettings;
import com.example.dbqueue.settings.PollSettings;
import com.example.dbqueue.settings.QueueLocation;
import com.example.starter.dao.PostgresQueueDao;
import com.example.starter.dao.PostgresQueuePickTaskDao;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.support.TransactionOperations;
import com.example.dbqueue.config.DatabaseAccessLayer;
import com.example.dbqueue.config.QueueTableSchema;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Class for interacting with database via Spring JDBC
 */
public class SpringDatabaseAccessLayer implements DatabaseAccessLayer {

    @Nonnull
    private final JdbcOperations jdbcOperations;
    @Nonnull
    private final TransactionOperations transactionOperations;
    @Nonnull
    private final QueueTableSchema queueTableSchema;
    @Nonnull
    private final QueueDao queueDao;


    /**
     * Constructor
     *
     * @param queueTableSchema      Queue table scheme.
     * @param jdbcOperations        Reference to Spring JDBC template.
     * @param transactionOperations Reference to Spring transaction template.
     */
    public SpringDatabaseAccessLayer(@Nonnull QueueTableSchema queueTableSchema,
                                     @Nonnull JdbcOperations jdbcOperations,
                                     @Nonnull TransactionOperations transactionOperations) {
        this.queueTableSchema = requireNonNull(queueTableSchema);
        this.jdbcOperations = requireNonNull(jdbcOperations);
        this.transactionOperations = requireNonNull(transactionOperations);
        this.queueDao = createQueueDao(queueTableSchema, jdbcOperations);
    }

    @Override
    @Nonnull
    public QueueDao getQueueDao() {
        return queueDao;
    }

    private QueueDao createQueueDao(@Nonnull QueueTableSchema queueTableSchema,
                                    @Nonnull JdbcOperations jdbcOperations) {
        requireNonNull(jdbcOperations);
        requireNonNull(queueTableSchema);
        return  new PostgresQueueDao(jdbcOperations, queueTableSchema);
    }

    @Override
    @Nonnull
    public QueuePickTaskDao createQueuePickTaskDao(
            @Nonnull QueueLocation queueLocation,
            @Nonnull FailureSettings failureSettings,
            @Nonnull PollSettings pollSettings
    ) {
        requireNonNull(queueTableSchema);
        requireNonNull(queueLocation);
        requireNonNull(failureSettings);
        requireNonNull(pollSettings);
        return  new PostgresQueuePickTaskDao(jdbcOperations, queueTableSchema,
                    queueLocation, failureSettings, pollSettings);
    }

    @Nonnull
    @Override
    public QueueTableSchema getQueueTableSchema() {
        return queueTableSchema;
    }

    @Override
    public <T> T transact(@Nonnull Supplier<T> supplier) {
        requireNonNull(supplier);
        return transactionOperations.execute(status -> supplier.get());
    }

    @Override
    public void transact(@Nonnull Runnable runnable) {
        requireNonNull(runnable);
        transact(() -> {
            runnable.run();
            return null;
        });
    }


    /**
     * Get reference to Spring JDBC template.
     *
     * @return Reference to Spring JDBC template.
     */
    @Nonnull
    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    /**
     * Get reference to Spring transaction template.
     *
     * @return Reference to Spring transaction template.
     */
    @Nonnull
    public TransactionOperations getTransactionOperations() {
        return transactionOperations;
    }
}
