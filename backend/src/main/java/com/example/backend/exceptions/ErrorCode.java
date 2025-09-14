package com.example.backend.exceptions;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ErrorCode {
    // Авторизация/доступ
    AUTH(53),                 // общие проблемы аутентификации/токена
    RIGHTS(54),               // недостаточно прав/доступ запрещён
    FORBIDDEN(403),           // 403 от HTTP-уровня (например, AccessDeniedHandler)
    NOT_FOUND(404),           // ресурс не найден

    // Валидация/входные данные
    REQUEST_DATA(2000),       // неверные/неконсистентные данные
    REQUEST_REQUIRED(2001),   // обязательное поле отсутствует
    REQUEST_TYPE(2002),       // тип не тот
    REQUEST_VALUE(2003),      // значение невалидно
    REQUEST_FILTER(2004),     // фильтры невалидны (пригодится для списков)
    REQUEST_PAGING(2005),     // пагинация невалидна
    REQUEST_ORDER(2006),      // порядок сортировки невалиден

    // Лимиты/нагрузка
    TOO_MANY_REQUESTS(429),   // троттлинг/рейткэп

    // Состояние сервиса
    SERVICE_UNAVAILABLE(503), // сервер/сервис выключен/недоступен
    INTERNAL_SERVER_ERROR(500), // общий 500

    // Внутренние/низкоуровневые
    INTERNAL(10001),          // общая внутренняя
    INTERNAL_MYSQL(10002),    // ошибка БД (ты используешь реляционную — пригодится)
    INTERNAL_EMAIL(10005),    // ошибки почты (если есть рассылки)
    INTERNAL_LOGIC(10006);    // логическая ошибка — баг в коде

    private final int code;
    ErrorCode(int code) { this.code = code; }

    public static Optional<ErrorCode> fromName(String name) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
