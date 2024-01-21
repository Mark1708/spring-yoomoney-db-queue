package com.example.dbqueue.internal.processing;

import com.example.dbqueue.api.TaskExecutionResult;
import com.example.dbqueue.api.TaskRecord;
import com.example.dbqueue.config.QueueShard;
import com.example.dbqueue.settings.QueueLocation;
import com.example.dbqueue.settings.ReenqueueSettings;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Обработчик результат выполенения задачи
 */
public class TaskResultHandler {

    @Nonnull
    private final QueueLocation location;
    @Nonnull
    private final QueueShard<?> queueShard;
    @Nonnull
    private ReenqueueRetryStrategy reenqueueRetryStrategy;

    /**
     * Конструктор
     *
     * @param location          местоположение очереди
     * @param queueShard        шард на котором происходит обработка задачи
     * @param reenqueueSettings настройки переоткладывания задач
     */
    public TaskResultHandler(@Nonnull QueueLocation location,
                             @Nonnull QueueShard<?> queueShard,
                             @Nonnull ReenqueueSettings reenqueueSettings) {
        this.location = requireNonNull(location);
        this.queueShard = requireNonNull(queueShard);
        this.reenqueueRetryStrategy = ReenqueueRetryStrategy.Factory.create(reenqueueSettings);
        reenqueueSettings.registerObserver((oldValue, newValue) ->
                reenqueueRetryStrategy = ReenqueueRetryStrategy.Factory.create(newValue));
    }

    /**
     * Обработать результат выполнения задачи
     *
     * @param taskRecord      обработанная задача
     * @param executionResult результат обработки
     */
    public void handleResult(@Nonnull TaskRecord taskRecord, @Nonnull TaskExecutionResult executionResult) {
        requireNonNull(taskRecord);
        requireNonNull(executionResult);

        switch (executionResult.getActionType()) {
            case FINISH -> queueShard.getDatabaseAccessLayer().transact(() -> queueShard.getDatabaseAccessLayer().getQueueDao()
                    .deleteTask(location, taskRecord.getId()));
            case REENQUEUE -> queueShard.getDatabaseAccessLayer().transact(() -> queueShard.getDatabaseAccessLayer().getQueueDao()
                    .reenqueue(location, taskRecord.getId(),
                            executionResult.getExecutionDelay().orElseGet(
                                    () -> reenqueueRetryStrategy.calculateDelay(taskRecord))));
            case FAIL -> {
            }
            default -> throw new IllegalStateException("unknown action type: " + executionResult.getActionType());
        }
    }
}
