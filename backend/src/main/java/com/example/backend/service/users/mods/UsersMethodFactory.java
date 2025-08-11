package com.example.backend.service.users.mods;

import com.example.backend.service.AbstractMethod;
import com.example.backend.service.MethodFactory;
import com.example.backend.service.types.OperationType;
import com.example.backend.service.users.methods.Add;
import com.example.backend.service.users.methods.Edit;
import com.example.backend.service.users.methods.Get;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@RequiredArgsConstructor
@Component("users")
public class UsersMethodFactory implements MethodFactory {
    private final ApplicationContext context;
    @Override
    public AbstractMethod createMethod(OperationType operation) {
        return switch (operation) {
            // todo: упростить и выенсти в gateaway сервис
            case GET -> context.getBean(Get.class);
            case ADD -> context.getBean(Add.class);
            case EDIT -> context.getBean(Edit.class);
            default -> throw createAndLogGatewayException("UNKNOWN_OPERATION_TYPE", "Operation type %s is unknown".formatted(operation.getOperation()), null);
        };
    }
}
