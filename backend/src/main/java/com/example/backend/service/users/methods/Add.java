package com.example.backend.service.users.methods;

import com.example.backend.service.AbstractMethod;

import java.util.List;
import java.util.Optional;

class Add extends AbstractMethod {
    @Override
    protected List<Optional<?>> exec() {
        // Имитация успешного добавления пользователя в БД
        // Возвращает список с одним Optional, содержащим ID нового пользователя
        return List.of(Optional.of(12345)); // 12345 - пример ID нового пользователя

        // Альтернативный вариант - имитация добавления нескольких сущностей:
        // return List.of(
        //     Optional.of(12345),
        //     Optional.of(12346)
        // );

        // Для имитации ошибки можно вернуть пустой Optional:
        // return List.of(Optional.empty());
    }
}
