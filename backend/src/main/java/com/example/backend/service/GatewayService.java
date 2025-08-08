package com.example.backend.service;

import com.example.backend.dto.request.ApiRequest;
import com.example.backend.exceptions.GatewayException;
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
     * Метод вызывает call() у API метода, переданного в request с параметрами params внутри request
     * @param request - Параметры запроса
     * @return - Возвращает мапу(JSON) с обязательным result
     *  И необязательными offset, limit, total, nextOffset
     */
    public static Map<String, Object> call(ApiRequest request) {
        var oper = request.getOperation();
        var methodName = request.getMethodName();
        var serviceName = request.getServiceName();
        var limit = request.getSelectorParams().get("limit");
        var offset = request.getSelectorParams().get("offset");

        String className = String.format("com.example.backend.service.%s.%s.%s",
                serviceName, methodName, oper);
        if (request.getParams() == null) {
            throw new GatewayException("NULL_PARAMS", "Parameters should not be null", null);
        }
        try {
            // Получение пути к классу
            className = getMethodClass(oper.getOperation(), serviceName, methodName);
            Class<?> clazz = Class.forName(className);

            // Проверка типа класса
            if (!AbstractMethod.class.isAssignableFrom(clazz)) {
                throw new GatewayException("INVALID_CLASS_TYPE", "Class " + className + " does not extend AbstractMethod", null);
            }

            // Создание экземпляра класса
            AbstractMethod instance = (AbstractMethod) clazz.getDeclaredConstructor().newInstance();

            List<Optional<?>> result = instance.call();

            List<?> resultList = result.stream()
                    .map(optional -> optional.orElseThrow(() ->
                            new GatewayException("CALL_FAILED", "Object value is empty", null)))
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
            throw new GatewayException("CLASS_NOT_FOUND", "Class not found: " + className, e);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new GatewayException("INSTANCE_CREATION_FAILED", "Failed to instantiate class: " + className, e);
        } catch (Exception e) {
            throw new GatewayException("ERROR_CREATING_INSTANCE", "Failed to create class instance for " + className, e);
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
            throw new GatewayException("UNKNOWN_OPERATION_TYPE", "Operation " + oper + " is unknown", null);
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
            throw new GatewayException("UNKNOWN_METHOD_NAME", "Method path " + classPath.toString() + " is wrong", null);
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
            // TODO: Залогировать ошибку
            return false;
        }
    }

    private static Map<String, Object> convertToJson(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(obj), Map.class);
        } catch (JsonProcessingException e) {
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
