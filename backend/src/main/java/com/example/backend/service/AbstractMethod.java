package com.example.backend.service;

import com.example.backend.uttils.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractMethod {
    /**
     * Если отключён, должны быть проверки в check()
     * Словарь с именами props, не требующих выполнения sanitize()
     * Такие props должны обязательно проверяться в check()
     */

    // Перечень props, которые должны быть проверены
    protected final Map<String, Boolean> SANITIZE_BY_NAME = Map.of(
            "example_prop_name", false
    );

    /**
     * Метод уже был вызван
     */

    protected boolean isCalled = false;

    /**
     * Запросить выполнение API функции
     */

    public List<Optional<?>> call() {
        new Log().info("Вызов метода call");
        if (isCalled) {
            IllegalStateException e = new IllegalStateException("Метод был уже вызван. Создайте копию объекта для повторного вызова.");

            // Логирование исключения
            new Log().error(e.getMessage(), e);

            throw e;
        }
        isCalled = true;

        return exec();
    }

    protected abstract List<Optional<?>> exec();
}
