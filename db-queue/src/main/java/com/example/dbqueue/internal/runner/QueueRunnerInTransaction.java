package com.example.dbqueue.internal.runner;

import com.example.dbqueue.api.QueueConsumer;
import com.example.dbqueue.api.TaskRecord;
import com.example.dbqueue.config.QueueShard;
import com.example.dbqueue.internal.processing.QueueProcessingStatus;
import com.example.dbqueue.internal.processing.TaskPicker;
import com.example.dbqueue.internal.processing.TaskProcessor;
import com.example.dbqueue.settings.ProcessingMode;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Исполнитель задач очереди в режиме
 * {@link ProcessingMode#WRAP_IN_TRANSACTION}
 */
class QueueRunnerInTransaction implements QueueRunner {

    @Nonnull
    private final TaskPicker taskPicker;
    @Nonnull
    private final TaskProcessor taskProcessor;
    @Nonnull
    private final QueueShard<?> queueShard;

    /**
     * Конструктор
     *
     * @param taskPicker    выборщик задачи
     * @param taskProcessor обработчик задачи
     * @param queueShard    шард на котором обрабатываются задачи
     */
    QueueRunnerInTransaction(@Nonnull TaskPicker taskPicker,
                             @Nonnull TaskProcessor taskProcessor,
                             @Nonnull QueueShard<?> queueShard) {
        this.taskPicker = requireNonNull(taskPicker);
        this.taskProcessor = requireNonNull(taskProcessor);
        this.queueShard = requireNonNull(queueShard);

    }

    @Override
    @Nonnull
    public QueueProcessingStatus runQueue(@Nonnull QueueConsumer queueConsumer) {
        List<TaskRecord> taskRecords = taskPicker.pickTasks();
        if (taskRecords.isEmpty()) {
            return QueueProcessingStatus.SKIPPED;
        }
        taskRecords.forEach(taskRecord -> queueShard.getDatabaseAccessLayer().transact(
                () -> taskProcessor.processTask(queueConsumer, taskRecord)));
        return QueueProcessingStatus.PROCESSED;
    }
}