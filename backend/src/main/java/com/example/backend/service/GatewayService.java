package com.example.backend.service;

import com.example.backend.dto.request.ApiRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.exceptions.ErrorCode;
import com.example.backend.exceptions.GatewayException;
import com.example.backend.service.utils.UniversalMethodFactory;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;


@Service
@RequiredArgsConstructor
public class GatewayService {
    private final UniversalMethodFactory universalMethodFactory;

    /**
     * Метод вызывает call() у API метода, переданного в request с параметрами params внутри request
     * @param request - Параметры запроса
     * @return - Возвращает ApiResponse
     */
    public ApiResponse call(ApiRequest request) {
        var operation = request.getOperation();
        var methodName = request.getMethodName();
        var serviceName = request.getServiceName();
        var params = request.getParams();
        AbstractMethod method = universalMethodFactory.createMethod(serviceName, methodName, operation);
        if (method == null) {
            throw createAndLogGatewayException(ErrorCode.SERVICE_UNAVAILABLE, "Service '%s' not found".formatted(serviceName), null);
        }

        Map<String, Object> selectorParams = request.getSelectorParams();

        //todo: сделать систему фильтров
        // List filters = [['id', 'NOT_EQals', [1]], ['username', 'LIKE', ['%mario%']]]


        try {
            var response = new ApiResponse();
            List<Optional<?>> result = method.call(params, selectorParams);

            Integer total = result.size();
            response.setTotal(total);
            response.setResult(result);

            return response;

        } catch (GatewayException e) {
            throw e;
        }
        catch (ValidationException e) {
            throw createAndLogGatewayException(ErrorCode.REQUEST_VALUE, "Validation exception. Message: %s".formatted(e.getMessage()), e);
        }
        catch (Exception e) {
            throw createAndLogGatewayException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Failed to create class instance for %s".formatted(method.getClass().getName()), e);
        }

    }
}
