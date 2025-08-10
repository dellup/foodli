package com.example.backend.service;

import com.example.backend.dto.request.ApiRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.utils.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;


@Service
@RequiredArgsConstructor
public class GatewayService {
    private final Map<String, MethodFactory> methodFactoryMap;

    /**
     * Метод вызывает call() у API метода, переданного в request с параметрами params внутри request
     * @param request - Параметры запроса
     * @return - Возвращает ApiResponse
     */
    public ApiResponse call(ApiRequest request) {
        new Log().info("Передача запроса в GatewayService");
        var operation = request.getOperation();
        var methodName = request.getMethodName();
        var serviceName = request.getServiceName();
        var params = request.getParams();
        // Получаем нужную фабрику для сервиса из контекста
        MethodFactory methodFactory = methodFactoryMap.get(serviceName);
        if (methodFactory == null) {
            throw createAndLogGatewayException("SERVICE_NOT_FOUND", "Service '%s' not found".formatted(serviceName), null);
        }

        AbstractMethod method = methodFactory.createMethod(operation);

        Map<String, Object> selectorParams = request.getSelectorParams();
        Integer limit = null;
        Integer offset = null;

        if (selectorParams != null) {
            limit = parseNumber(selectorParams.get("limit"), "limit");
            offset = parseNumber(selectorParams.get("offset"), "offset");
        }

        // Далее работаем как обычно
        if (limit != null && limit <= 0) {
            throw createAndLogGatewayException("INVALID_LIMIT", "Limit must be positive", null);
        }
        if (offset != null && offset < 0) {
            throw createAndLogGatewayException("INVALID_OFFSET", "OFFSET must be positive", null);
        }

        if (request.getParams() == null) {
            throw createAndLogGatewayException("NULL_PARAMS", "Parameters should not be null", null);
        }

        try {
            var response = new ApiResponse();

            // TODO: Пагинацию нужно добавить внутрь call и делать запросы к БД используя пагинацию
            List<Optional<?>> result = method.call(params);
            int total = result.size();

            // Обработка пагинации
            if (limit != null && offset != null) {
                int offsetInt = Integer.parseInt(offset.toString());
                int limitInt = Integer.parseInt(limit.toString());
                int toIndex = Math.min(offsetInt + limitInt, result.size());

                // Если offset выходит за пределы списка, возвращаем пустой результат
                if (offsetInt >= result.size()) {
                    throw createAndLogGatewayException("OFFSET_IS_OUT_OF_BOUNDS",
                            "Offset '%d' is out of bounds".formatted(offsetInt),
                            null);
                }

                result = result.subList(offsetInt, toIndex);
            }

            // Формируем ответ с мета-данными
            var nextOffset = getNextOffset(result, limit, offset, total);
            response.setResult(result);
            if (total > 0) {
                response.setTotal(total);
            }
            if (limit != null) {
                response.setLimit(limit);
            }
            if (offset != null) {
                response.setOffset(offset);
            }

            if (nextOffset != -1) {
                response.setNextOffset(nextOffset);
            }

            return response;

        } catch (Exception e) {
            throw createAndLogGatewayException("ERROR_CREATING_INSTANCE",
                    "Failed to create class instance for %s".formatted(method.getClass().getName()), e);
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

    private static Integer parseNumber(Object value, String paramName) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw createAndLogGatewayException("INVALID_" + paramName.toUpperCase(),
                    paramName + " must be a number or null",
                    e);
        }
    }
}
