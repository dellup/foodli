package com.example.backend.utils;

import com.example.backend.exceptions.GatewayException;
import com.example.backend.service.AbstractMethod;
import com.example.backend.service.types.OperationType;
import com.example.backend.service.users.auth.Get;
import com.example.backend.service.utils.UniversalMethodFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversalMethodFactoryTest {

    @Mock
    ApplicationContext ctx;

    @InjectMocks
    UniversalMethodFactory factory;

    @Test
    void createMethodValidUsersAuthGet() {
        // Arrange
        Get getBean = mock(Get.class);
        when(ctx.getBean(Get.class)).thenReturn(getBean);

        // Act
        AbstractMethod method = factory.createMethod("users", "auth", OperationType.GET);

        // Assert
        assertThat(method).isSameAs(getBean);
        verify(ctx, times(1)).getBean(Get.class);
        verifyNoMoreInteractions(ctx);
    }

    @Test
    void createMethodClassNotFoundThrowsGatewayException_INTERNAL() {
        // Arrange
        var methodName = "nope";

        // Act
        GatewayException ex = assertThrows(GatewayException.class,
                () -> factory.createMethod("users", methodName, OperationType.GET));

        // Assert
        assertEquals(10001, ex.getCode());
    }
}
