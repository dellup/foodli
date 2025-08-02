package com.example.backend.service.users.methods;

import com.example.backend.model.User;
import com.example.backend.service.AbstractMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Add extends AbstractMethod {
    public Add() {}
    @Override
    protected List<Optional<?>> exec() {
        var user1 = Optional.of(new User());
        user1.get().setUsername("admin");
        var user2 = Optional.of(new User());
        user2.get().setUsername("gfds");
        return new ArrayList<>(List.of(user1, user2));
    }
}
