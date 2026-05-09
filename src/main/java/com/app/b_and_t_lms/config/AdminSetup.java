package com.app.b_and_t_lms.config;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.b_and_t_lms.models.Role;
import com.app.b_and_t_lms.models.Status;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.models.Role.RoleName;
import com.app.b_and_t_lms.repositories.UserRepository;

@Configuration
public class AdminSetup {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@lms.com")) {
                User admin = new User();
                admin.setFirstname("System");
                admin.setLastname("Admin");
                admin.setEmail("admin@lms.com");
                admin.setPassword(passwordEncoder.encode("Admin123!"));
                admin.setIdNumber("0006195395089");
                admin.setContactNumber("0767811223");
                admin.setGender("Male");
                admin.setRoles(List.of(new Role(RoleName.ADMIN, admin)));
                admin.setDob(LocalDate.of(2000, 06, 19));
                admin.setCreatedAt(Timestamp.from(Instant.now()));
                admin.setStatus(Status.ACTIVE);
                admin.setLastLogin(null);
                admin.setPrevLogin(null);
                admin.setSuperUser(true);

                userRepository.save(admin);
            }
        };
    }
}
