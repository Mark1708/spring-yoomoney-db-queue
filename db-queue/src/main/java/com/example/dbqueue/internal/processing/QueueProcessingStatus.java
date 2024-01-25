package com.example.dbqueue.internal.processing;

/**
 * Тип результата обработки задачи в очереди
 */
public enum QueueProcessingStatus {

    /**
     * Задача была обрабатана
     */
    PROCESSED,
    /**
     * Задача не была найдена и обработка не состоялась
     */
    SKIPPED
}
