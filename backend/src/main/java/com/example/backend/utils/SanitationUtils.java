package com.example.backend.utils;

import com.example.backend.service.AbstractMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.util.HtmlUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

/**
 * Класс предназначен для санитаризации всех полей переданного объекта, кроме тех, которые
 * указаны в SANITIZE_BY_NAME как false (если объект это наследник класса AbstractMethod).
 * <p>
 * Санитаризация подразумевает преобразование специальных символов на их HTML представление.
 */
public class SanitationUtils {
    public static void sanitize(Object instance) {
        if (instance == null) {
            return;
        }

        Field[] fields = instance.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Даем доступ к приватным полям
            if (JpaRepository.class.isAssignableFrom(field.getType())) { // Поля репозиториев не санитайзим
                continue;
            }
            try {
                if (instance instanceof AbstractMethod) {
                    AbstractMethod abstractMethod = (AbstractMethod) instance;
                    if (abstractMethod.getSANITIZE_BY_NAME().getOrDefault(field.getName(), true)) {
                        sanitizeField(abstractMethod, field);
                    }
                } else {
                    sanitizeField(instance, field);
                }
            } catch (IllegalAccessException e) {
                throw createAndLogGatewayException("REFLECTION_ERROR", "An error occurred when using reflection", e);
            }
        }
    }

    private static void sanitizeField(Object instance, Field field) throws IllegalAccessException {
        Object value = field.get(instance);
        if (value == null) {
            return;
        }

        if (value instanceof String) {
            String stringValue = (String) value;
            String sanitizedValue = HtmlUtils.htmlEscape(stringValue.trim());
            field.set(instance, sanitizedValue);
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            Collection<Object> sanitizedCollection = new ArrayList<>();
            for (Object item : collection) {
                if (item instanceof String) {
                    String sanitizedItem = HtmlUtils.htmlEscape(((String) item).trim());
                    sanitizedCollection.add(sanitizedItem);
                } else if (item != null) {
                    sanitize(item);
                }
            }
            field.set(instance, sanitizedCollection);
        } else if (value instanceof Number ||
                value instanceof Boolean ||
                value instanceof Character ||
                value instanceof Date) {
            return;
        } else if (value != null) {
            // Рекурсивно вызываем санацию для вложенных объектов
            sanitize(value);
        }
    }
}
