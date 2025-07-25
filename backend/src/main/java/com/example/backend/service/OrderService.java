package com.example.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderService {

    public String getOrders(Map<String, Object> params) {

        return "This is your first order!";
    }
}
