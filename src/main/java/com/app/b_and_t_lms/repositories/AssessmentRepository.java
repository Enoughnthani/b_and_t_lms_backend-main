package com.app.b_and_t_lms.repositories;

import com.app.b_and_t_lms.models.Assessment;
import com.app.b_and_t_lms.models.UnitStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByUnitStandard(UnitStandard unitStandard);
    List<Assessment> findByUnitStandardUnitStandardId(Long unitStandardId);
}