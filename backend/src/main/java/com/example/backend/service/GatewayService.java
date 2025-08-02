package com.example.backend.service;

import com.example.backend.dto.request.ApiRequest;
import com.example.backend.exceptions.GatewayException;
import com.example.backend.uttils.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.StringUtils.capitalize;


@Service
public class GatewayService {

    /**
     * Метед вызывает call() у API метода, переданного в request с параметрами params внутри request
     * @param request - Параметры запроса
     * @return - Возвращает мапу(JSON) с обязательным result
     *  И необязательными offset, limit, total, nextOffset
     */
    public static Map<String, Object> call(ApiRequest request) {
        new Log().info("Передача запроса в GatewayService");
        var oper = request.getOperation();
        var methodName = request.getMethodName();
        var serviceName = request.getServiceName();
        var limit = request.getSelectorParams().get("limit");
        var offset = request.getSelectorParams().get("offset");

        String className = String.format("com.example.backend.service.%s.%s.%s",
                serviceName, methodName, oper);
        if (request.getParams() == null) {
 
            // Логирование исключения
            GatewayException gatewayException = new GatewayException("NULL_PARAMS", "Parameters should not be null", null);
            new Log().error(gatewayException.getMessage(), gatewayException);

            throw gatewayException;
        }

        try {
            // Получение пути к классу
            className = getMethodClass(oper.getOperation(), serviceName, methodName);
            Class<?> clazz = Class.forName(className);

            // Проверка типа класса
            if (!AbstractMethod.class.isAssignableFrom(clazz)) {

                // Логирование исключения
                GatewayException gatewayException = new GatewayException("INVALID_CLASS_TYPE", "Class " + className + " does not extend AbstractMethod", null);
                new Log().error(gatewayException.getMessage(), gatewayException);

                throw gatewayException;
            }

            // Создание экземпляра класса
            AbstractMethod instance = (AbstractMethod) clazz.getDeclaredConstructor().newInstance();

            List<Optional<?>> result = instance.call();

            List<?> resultList = result.stream()
                    .map(optional -> optional.orElseThrow(() -> {
                        // Логирование исключения
                        GatewayException gatewayException = new GatewayException("CALL_FAILED", "Object value is empty", null);
                        new Log().error(gatewayException.getMessage(), gatewayException);
                        return gatewayException;
                    }))
                    .toList();
            int total = resultList.size();

            // Обработка пагинации
            if (limit != null && offset != null) {
                int offsetInt = Integer.parseInt(offset.toString());
                int limitInt = Integer.parseInt(limit.toString());
                int toIndex = Math.min(offsetInt + limitInt, resultList.size());

                // Если offset выходит за пределы списка, возвращаем пустой результат
                if (offsetInt >= resultList.size()) {
                    return new HashMap<>();
                }

                resultList = resultList.subList(offsetInt, toIndex);
            }

            // Формируем ответ с мета-данными
            var nextOffset = getNextOffset(resultList, limit, offset, total);
            Map<String, Object> response = convertToJson(resultList);
            if (total > 0) {
                response.put("total", total);
            }
            if (limit != null) {
                response.put("limit", limit);
            }
            if (offset != null) {
                response.put("offset", offset);
            }

            if (nextOffset != -1) {
                response.put("nextOffset", nextOffset);
            }

            return response;

        } catch (ClassNotFoundException e) {

            // Логирование исключения
            GatewayException gatewayException = new GatewayException("CLASS_NOT_FOUND", "Class not found: " + className, e);
            new Log().error(gatewayException.getMessage(), gatewayException);

            throw gatewayException;

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {

            // Логирование исключения
            GatewayException gatewayException = new GatewayException("INSTANCE_CREATION_FAILED", "Failed to instantiate class: " + className, e);
            new Log().error(gatewayException.getMessage(), gatewayException);

            throw gatewayException;
        } catch (Exception e) {

            // Логирование исключения
            GatewayException gatewayException = new GatewayException("ERROR_CREATING_INSTANCE", "Failed to create class instance for " + className, e);
            new Log().error(gatewayException.getMessage(), gatewayException);

            throw gatewayException;
        }

    }

    /**
     * Метод получает полный путь нужного класса по параметрам запроса
     * @param oper - Операция
     * @param serviceName - Имя сервиса
     * @param methodName - Имя метода
     * @return - Выводит полный путь к методу нужного сервиса.
     * Если такого метода не существует, то выводит ошибку.
     */
    private static String getMethodClass(String oper, String serviceName, String methodName) {
        if (!oper.equals("add") && !oper.equals("get") && !oper.equals("edit") && !oper.equals("del")) {

            // Логирование исключения
            GatewayException gatewayException = new GatewayException("UNKNOWN_OPERATION_TYPE", "Operation " + oper + " is unknown", null);
            new Log().error(gatewayException.getMessage(), gatewayException);

            throw gatewayException;
        }
        StringBuilder classPath = new StringBuilder();
        classPath.append("com.example.backend.");
        classPath.append("service.");
        classPath.append(serviceName).append(".");
        classPath.append(methodName).append(".");
        classPath.append(capitalize(oper));
        if (isClassExists(classPath.toString())) {
            return classPath.toString();
        } else {

            // Логирование исключения
            GatewayException gatewayException = new GatewayException("UNKNOWN_METHOD_NAME", "Method path " + classPath.toString() + " is wrong", null);
            new Log().error(gatewayException.getMessage(), gatewayException);

            throw gatewayException;
        }
    }

    public static boolean isClassExists(String className) {
        if (className == null || className.trim().isEmpty()) {
            return false;
        }
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {

            // Логирование исключения
            new Log().error("Заданный метод не найден", e);

            return false;
        }
    }

    private static Map<String, Object> convertToJson(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(obj), Map.class);
        } catch (JsonProcessingException e) {

            // Логирование исключения
            new Log().error("Ошибка создания Json", e);

            return new HashMap<>();
        }
    }

    private static int getNextOffset(List<?> resultList, Object limit, Object offset, int total) {
        Integer limitInt;
        Integer offsetInt;

        if (limit == null || offset == null) {
            return -1;
        }
        limitInt = (Integer) limit;
        offsetInt = (Integer) offset;
        if (resultList.size() + offsetInt < total) {
            return offsetInt + limitInt;
        }
        return -1;  // Возвращаем -1, если это последняя страница
    }
}
