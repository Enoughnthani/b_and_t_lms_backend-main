package com.app.b_and_t_lms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.b_and_t_lms.models.ProgramStaff;

public interface ProgramStaffRepository extends JpaRepository<ProgramStaff, Long> {
    Optional<ProgramStaff> findByProgramIdAndUserId(Long programId, Long userId);


  
}
