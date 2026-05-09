package com.app.b_and_t_lms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.b_and_t_lms.models.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByProgramIdAndUserId(Long programId, Long userId);

    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.program.id = :programId AND e.user.id IN :userIds")
    int deleteByProgramIdAndUserIds(@Param("programId") Long programId, @Param("userIds") List<Long> userIds);

    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.user.id = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}
