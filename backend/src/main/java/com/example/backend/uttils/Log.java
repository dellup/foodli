package com.example.backend.uttils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import java.util.concurrent.TimeUnit;

/**
 * Обёртка для Log4j2 с дополнительными возможностями.
 */

public class Log {
    private static final Logger logger = LogManager.getLogger(Log.class);

    //=== Базовые методы ===//

    public void debug(String message) {
        logger.debug(message);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    /**
     * Логирует сообщение с контекстом (например, ID пользователя).
     */

    public void infoWithContext(String message, String key, String value) {
        ThreadContext.put(key, value); // Добавляем контекст
        logger.info(message);
        ThreadContext.remove(key); // Очищаем
    }

    /**
     * Логирует время выполнения блока кода.
     */
    public void logExecutionTime(String operationName, Runnable code) {
        long startTime = System.nanoTime();
        try {
            code.run();
        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            logger.info("Операция '{}' выполнена за {} мс", operationName, durationMs);
        }
    }
}