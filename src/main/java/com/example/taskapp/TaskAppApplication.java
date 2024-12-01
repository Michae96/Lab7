package com.example.taskapp;

import com.example.taskapp.config.UserRepository;
import com.example.taskapp.models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
public class TaskAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByUsername("admin").ifPresentOrElse(
                    admin -> {
                        System.out.println("Admin already exists");
                    },
                    () -> {
                        User admin = new User();
                        admin.setUsername("admin");
                        admin.setPassword(passwordEncoder.encode("admin"));
                        admin.setRole("ADMIN"); // Убедитесь, что поле role в User существует
                        admin.setCreatedAt(LocalDateTime.now());
                        userRepository.save(admin);
                        System.out.println("Admin created: admin/admin");
                    }
            );
        };
    }
}
