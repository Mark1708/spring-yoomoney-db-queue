package com.example.dbqueue.internal.runner;

import com.example.dbqueue.api.QueueConsumer;
import com.example.dbqueue.api.TaskRecord;
import com.example.dbqueue.internal.processing.QueueProcessingStatus;
import com.example.dbqueue.internal.processing.TaskPicker;
import com.example.dbqueue.internal.processing.TaskProcessor;
import com.example.dbqueue.settings.ProcessingMode;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Исполнитель задач очереди в режиме
 * {@link ProcessingMode#SEPARATE_TRANSACTIONS}
 */
class QueueRunnerInSeparateTransactions implements QueueRunner {

    @Nonnull
    private final TaskPicker taskPicker;
    @Nonnull
    private final TaskProcessor taskProcessor;

    /**
     * Конструктор
     *
     * @param taskPicker    выборщик задачи
     * @param taskProcessor обработчик задачи
     */
    QueueRunnerInSeparateTransactions(@Nonnull TaskPicker taskPicker,
                                      @Nonnull TaskProcessor taskProcessor) {
        this.taskPicker = requireNonNull(taskPicker);
        this.taskProcessor = requireNonNull(taskProcessor);

    }

    @Override
    @Nonnull
    public QueueProcessingStatus runQueue(@Nonnull QueueConsumer queueConsumer) {
        List<TaskRecord> taskRecords = taskPicker.pickTasks();
        if (taskRecords.isEmpty()) {
            return QueueProcessingStatus.SKIPPED;
        }
        taskRecords.forEach(taskRecord -> taskProcessor.processTask(queueConsumer, taskRecord));
        return QueueProcessingStatus.PROCESSED;
    }

}
