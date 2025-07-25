//package com.example.backend.service;
//
//import com.example.backend.dto.response.SuccessResponse;
//import com.example.backend.exceptions.GatewayException;
//import com.example.backend.repository.entity.User;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class UsersModule {
//    public SuccessResponse<List<User>> getUsers(int limit, int offset) {
//        try {
//            List<User> users = userRepository.findUsers(limit, offset);
//            int total = userRepository.countUsers();
//            return new SuccessResponse<>(
//                    users,
//                    total,
//                    offset + limit < total ? offset + limit : null,
//                    limit,
//                    offset
//            );
//        } catch (Exception e) {
//            throw new GatewayException("USERS_ERROR", "Failed to fetch users", e.getMessage());
//        }
//}