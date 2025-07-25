package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GatewayService {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    public Object routeRequest(String method, Map<String, Object> params) throws Exception {
        return switch (method) {
            // Users
            case "getUser" -> userService.getUser();
//            case "createUser" -> userService.createUser(params);

            // Orders
//            case "createOrder" -> orderService.createOrder(params);
            case "getOrders" -> orderService.getOrders(params);

            // Payments
//            case "processPayment" -> paymentService.process(params);

//            default -> throw new BusinessException("UNKNOWN_METHOD", "Method not supported");\
            default -> throw new Exception("UNKNOWN_METHOD");
        };
    }
}
