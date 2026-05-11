package com.app.b_and_t_lms.repositories;

import com.app.b_and_t_lms.models.Assessment;
import com.app.b_and_t_lms.models.AssessmentSubmission;
import com.app.b_and_t_lms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, Long> {
    List<AssessmentSubmission> findByAssessment(Assessment assessment);
    List<AssessmentSubmission> findByAssessmentId(Long assessmentId);
    Optional<AssessmentSubmission> findByAssessmentAndUser(Assessment assessment, User user);
}