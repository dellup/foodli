package com.example.backend.component;

import com.example.backend.model.User;
import com.example.backend.model.role.Role;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${init.user.email}")
    private String userEmail;

    @Value("${init.admin.email}")
    private String adminEmail;

    @Value("${init.user.password}")
    private String userPassword;

    @Value("${init.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        initUsers();
    }

    private void initUsers() {
        // Проверяем, есть ли уже пользователи в базе
        if (userRepository.count() == 0) {
            createAdminUser();
            createRegularUser();
        }
    }

    private void createAdminUser() {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(Set.of(Role.ADMIN, Role.USER));

            userRepository.save(admin);
        }
    }

    private void createRegularUser() {
        if (userRepository.findByEmail(userEmail).isEmpty()) {
            User user = new User();
            user.setEmail(userEmail);
            user.setPassword(passwordEncoder.encode(userPassword));
            user.setRoles(Set.of(Role.USER));

            userRepository.save(user);
        }
    }
}