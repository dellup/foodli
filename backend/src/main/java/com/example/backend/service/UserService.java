package com.example.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
// CustomUserService
@Service
public class UserService {
// Чет типа такого

//    public User getUser(Map<String, Object> params) {
//        Long userId = Long.parseLong(params.get("userId").toString());
//        // Логика получения пользователя...
//        return userRepository.findById(userId).orElseThrow();
//    }

    /// ////////////////////////

    public String getUser() {

        return "Get user 1";
    }
}
