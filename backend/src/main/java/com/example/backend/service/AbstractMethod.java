package com.example.backend.service;

import com.example.backend.utils.Log;
import com.example.backend.utils.SanitationUtils;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

public abstract class AbstractMethod {
    /**
     * Если отключён, должны быть проверки в check()
     * Словарь с именами props, не требующих выполнения sanitize()
     * Такие props должны обязательно проверяться в check()
     */
    Map<String, Object> params;

    // Перечень props, которые должны быть проверены
    @Getter
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

    public List<Optional<?>> call(Map<String, Object> params, Map<String, Object> selectorParams) {
        if (isCalled) {
            IllegalStateException e = new IllegalStateException("Метод был уже вызван." +
                    " Создайте копию объекта для повторного вызова.");

            // Логирование исключения
            new Log().error(e.getMessage(), e);

            throw e;
        }
        isCalled = true;

        this.params = params;
        fillParams(this);
        SanitationUtils.sanitize(this);

        return exec();
    }

    private void fillParams(AbstractMethod instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (params.containsKey(fieldName)) {
                try {
                    field.set(instance, params.get(fieldName));
                } catch (IllegalAccessException e) {
                    throw createAndLogGatewayException("REFLECTION_ERROR", "An error occurred when using reflection", e);
                }
            } else if (!JpaRepository.class.isAssignableFrom(field.getType())) {
                throw createAndLogGatewayException("ERROR_CREATING_INSTANCE",
                        "An empty field '%s'".formatted(fieldName), null);
            }
        }
    }

    protected abstract List<Optional<?>> exec();
}
