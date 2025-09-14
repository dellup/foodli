package com.example.backend.service;

import com.example.backend.exceptions.ErrorCode;
import com.example.backend.model.role.Role;
import com.example.backend.service.utils.auth.jwt.JwtAuthGuard;
import com.example.backend.service.utils.auth.jwt.JwtAuthenticated;
import com.example.backend.service.utils.auth.role.RequireRoles;
import com.example.backend.service.utils.auth.role.RoleGuard;
import com.example.backend.utils.Log;
import com.example.backend.utils.SanitationUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

/**
 * Все http(crud) методы должны наследоваться от этого класса
 */
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
     * Инжект класса для проверки необходимости аутентификации
     */
    @Autowired
    private JwtAuthGuard jwtAuthGuard;

    /**
     * Инжект класса для проверки доступа по роли
     */
    @Autowired
    private RoleGuard roleGuard;

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

        // --- Авторизация и роли ---
        // 1) если есть @JwtAuthenticated → просто enforce
        if (requiresAuth()) {
            jwtAuthGuard.enforce();
        }

        // 2) если есть @RequireRoles → enforce + проверка ролей
        Role[] roles = requiredRoles();
        if (roles.length > 0) {
            roleGuard.require(roles);
        }

        // --- Вызов бизнес-логики ---
        return exec();
    }

    /**
     * Проверяет, стоит ли @JwtAuthenticated на классе или на exec()
     */
    private boolean requiresAuth() {
        return this.getClass().isAnnotationPresent(JwtAuthenticated.class);
    }

    /**
     * Достаёт список ролей из @RequireRoles
     */
    private Role[] requiredRoles() {
        // проверяем аннотацию на классе
        if (this.getClass().isAnnotationPresent(RequireRoles.class)) {
            return this.getClass().getAnnotation(RequireRoles.class).value();
        }
        return new Role[0];
    }

    /**
     * Подставляет входящие параметры в поля текущего инстанса
     */
    private void fillParams(AbstractMethod instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (params.containsKey(fieldName)) {
                try {
                    if (field.getName().equals("roles")) {
                        field.set(instance, new HashSet<>((Collection) params.get(fieldName)));
                    } else {
                        field.set(instance, params.get(fieldName));
                    }
                } catch (IllegalAccessException e) {
                    throw createAndLogGatewayException(ErrorCode.INTERNAL, "An error occurred when using reflection", e);
                }
            }
            // todo: потом сделаю нормально
            // else if (!JpaRepository.class.isAssignableFrom(field.getType())) {
            // throw createAndLogGatewayException("ERROR_CREATING_INSTANCE",
            //            "An empty field '%s'".formatted(fieldName), null);
            // }
        }
    }

    protected abstract List<Optional<?>> exec();
}
