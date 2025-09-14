package com.example.backend.service.utils;

import com.example.backend.exceptions.ErrorCode;
import com.example.backend.service.AbstractMethod;
import com.example.backend.service.types.OperationType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@Component
public class UniversalMethodFactory implements MethodFactory {

    private final ApplicationContext context;

    public UniversalMethodFactory(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public AbstractMethod createMethod(String serviceName, String methodName, OperationType operation) {
        String className = "com.example.backend.service.%s.%s.%s".formatted(serviceName,
                methodName,
                StringUtils.capitalize(operation.getOperation()));
        try {
            Class<?> methodClass = Class.forName(className);

            // Проверяем, что класс является подклассом AbstractMethod
            if (AbstractMethod.class.isAssignableFrom(methodClass)) {
                return (AbstractMethod) context.getBean(methodClass);
            } else {
                throw createAndLogGatewayException(ErrorCode.INTERNAL,
                        "Class " + className + " is not a subclass of AbstractMethod", null);
            }
        } catch (ClassNotFoundException e) {
            throw createAndLogGatewayException(ErrorCode.INTERNAL,
                    "Failed to create class instance for %s".formatted(methodName), e);
        }
    }
}
