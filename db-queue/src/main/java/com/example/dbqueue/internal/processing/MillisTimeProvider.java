package com.example.dbqueue.internal.processing;

import java.util.concurrent.TimeUnit;

/**
 * Поставщик текущего времени в миллисекундах.
 */
@FunctionalInterface
public interface MillisTimeProvider {

    /**
     * Получить время в миллисекундах.
     *
     * @return время в миллисекундах
     */
    long getMillis();

    /**
     * Поставщик системного времени
     */
    class SystemMillisTimeProvider implements MillisTimeProvider {

        @Override
        public long getMillis() {
            return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        }
    }
}
