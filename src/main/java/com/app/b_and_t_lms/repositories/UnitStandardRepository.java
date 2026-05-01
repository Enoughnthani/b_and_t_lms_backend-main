package com.app.b_and_t_lms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.b_and_t_lms.models.UnitStandard;

@Repository
public interface UnitStandardRepository extends JpaRepository<UnitStandard, Long> {

    // Find by ID (already provided by JpaRepository, but explicit for clarity)
    Optional<UnitStandard> findByUnitStandardId(Long unitStandardId);
    
    // Find all unit standards for a program
    @Query("SELECT u FROM UnitStandard u WHERE u.program.id = :programId")
    List<UnitStandard> findByProgramId(@Param("programId") Long programId);
    
    // Find by program and type
    @Query("SELECT u FROM UnitStandard u WHERE u.program.id = :programId AND u.type = :type")
    List<UnitStandard> findByProgramIdAndType(@Param("programId") Long programId, @Param("type") String type);
    
    // Find unit standard with its contents (eager loading)
    @Query("SELECT u FROM UnitStandard u LEFT JOIN FETCH u.contents WHERE u.unitStandardId = :unitStandardId")
    Optional<UnitStandard> findByIdWithContents(@Param("unitStandardId") Long unitStandardId);
    
    // Search by title (contains)
    @Query("SELECT u FROM UnitStandard u WHERE u.program.id = :programId AND u.title LIKE %:keyword%")
    List<UnitStandard> searchByProgramIdAndKeyword(@Param("programId") Long programId, @Param("keyword") String keyword);
    
    // Get total credits for a program
    @Query("SELECT COALESCE(SUM(u.credits), 0) FROM UnitStandard u WHERE u.program.id = :programId")
    Integer getTotalCreditsByProgramId(@Param("programId") Long programId);
    
    // Get unit standards with no content
    @Query("SELECT u FROM UnitStandard u WHERE u.program.id = :programId AND u.contents IS EMPTY")
    List<UnitStandard> findEmptyUnitStandardsByProgramId(@Param("programId") Long programId);
    
    // Get unit standards that have content
    @Query("SELECT DISTINCT u FROM UnitStandard u JOIN u.contents c WHERE u.program.id = :programId")
    List<UnitStandard> findUnitStandardsWithContentsByProgramId(@Param("programId") Long programId);
}