package com.example.dbqueue.dao;

import com.example.dbqueue.api.TaskRecord;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Database access object to pick up tasks in the queue.
 */
public interface QueuePickTaskDao {

    /**
     * Pick tasks from a queue
     *
     * @return list of tasks data or empty if not found
     */
    @Nonnull
    List<TaskRecord> pickTasks();
}
