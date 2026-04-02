package com.app.b_and_t_lms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.b_and_t_lms.models.Role;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.models.Role.RoleName;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdNumber(String idNumber);

    List<User> findByRoles_Name(RoleName roleName);

}
