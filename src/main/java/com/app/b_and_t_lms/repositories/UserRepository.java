package com.app.b_and_t_lms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.b_and_t_lms.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdNumber(String idNumber);

}
