package com.example.dbqueue.internal.processing;

import com.example.dbqueue.api.TaskRecord;
import com.example.dbqueue.dao.QueuePickTaskDao;
import com.example.dbqueue.config.QueueShard;
import com.example.dbqueue.config.TaskLifecycleListener;
import com.example.dbqueue.settings.QueueLocation;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Класс, обеспечивающий выборку задачи из очереди
 */
public class TaskPicker {

    @Nonnull
    private final QueueShard<?> queueShard;
    @Nonnull
    private final QueueLocation queueLocation;
    @Nonnull
    private final TaskLifecycleListener taskLifecycleListener;
    @Nonnull
    private final MillisTimeProvider millisTimeProvider;

    private final QueuePickTaskDao pickTaskDao;

    /**
     * Constructor
     *
     * @param queueShard            shard to bound task picker to
     * @param queueLocation         queue location
     * @param taskLifecycleListener task listener
     * @param millisTimeProvider    current time provider
     * @param pickTaskDao           dao for picking up tasks
     */
    public TaskPicker(@Nonnull QueueShard<?> queueShard,
                      @Nonnull QueueLocation queueLocation,
                      @Nonnull TaskLifecycleListener taskLifecycleListener,
                      @Nonnull MillisTimeProvider millisTimeProvider,
                      @Nonnull QueuePickTaskDao pickTaskDao) {
        this.queueShard = requireNonNull(queueShard);
        this.queueLocation = requireNonNull(queueLocation);
        this.taskLifecycleListener = requireNonNull(taskLifecycleListener);
        this.millisTimeProvider = requireNonNull(millisTimeProvider);
        this.pickTaskDao = requireNonNull(pickTaskDao);
    }

    /**
     * Выбрать батч задач из очереди
     *
     * @return список задач или пустой список если задачи отсутствуют
     */
    @Nonnull
    public List<TaskRecord> pickTasks() {
        long startPickTaskTime = millisTimeProvider.getMillis();
        List<TaskRecord> pickedTasks = queueShard.getDatabaseAccessLayer().transact(pickTaskDao::pickTasks);
        long pickTaskTime = millisTimeProvider.getMillis() - startPickTaskTime;
        for (TaskRecord taskRecord : pickedTasks) {
            taskLifecycleListener.picked(queueShard.getShardId(), queueLocation, taskRecord, pickTaskTime);
        }
        return pickedTasks;
    }
}
