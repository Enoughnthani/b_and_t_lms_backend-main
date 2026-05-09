package com.app.b_and_t_lms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.b_and_t_lms.models.Activity;

public interface ActivityRepository extends JpaRepository<Activity,Long> {

    int deleteByCreatedAtBefore(LocalDateTime cutoffDate);

    List<Activity> findAllByOrderByCreatedAtDesc();
    
} 