package com.app.b_and_t_lms.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.AssessmentRequestDTO;
import com.app.b_and_t_lms.dto.TestSubmissionDTO;
import com.app.b_and_t_lms.services.AssessmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'ASSESSOR', 'MODERATOR')")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping("/unit-standard/{unitStandardId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'ASSESSOR', 'MODERATOR', 'LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentsByUnitStandard(
            @PathVariable Long unitStandardId,
            Authentication authentication) {
        return ResponseEntity.ok(
                assessmentService.getAssessmentsByUnitStandard(unitStandardId, authentication));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'ASSESSOR', 'MODERATOR', 'LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentById(@PathVariable Long id) {
        return ResponseEntity.ok(
                assessmentService.getAssessmentById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createAssessment(
            @ModelAttribute AssessmentRequestDTO dto) throws IOException {
        return ResponseEntity.ok(
                assessmentService.createAssessment(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateAssessment(
            @PathVariable Long id,
            @ModelAttribute AssessmentRequestDTO dto) throws IOException {
        return ResponseEntity.ok(
                assessmentService.updateAssessment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteAssessment(@PathVariable Long id) {
        return ResponseEntity.ok(
                assessmentService.deleteAssessment(id));
    }

    @GetMapping("/{assessmentId}/submissions")
    public ResponseEntity<ApiResponse<?>> getSubmissions(
            @PathVariable Long assessmentId) {
        return ResponseEntity.ok(
                assessmentService.getSubmissions(assessmentId));
    }

    @GetMapping("/download/{filename}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'ASSESSOR', 'MODERATOR', 'LEARNER')")
    public ResponseEntity<?> downloadAssessmentFile(
            @PathVariable String filename) throws IOException {
        return assessmentService.downloadAssessmentFile(filename);
    }

    // Learner-only endpoints
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> submitAssessment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("assessmentId") Long assessmentId,
            Authentication authentication) throws IOException {

        return ResponseEntity.ok(
                assessmentService.submitAssessment(file, assessmentId, authentication));
    }

    @GetMapping("/{assessmentId}/submission")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> getMySubmission(
            @PathVariable Long assessmentId,
            Authentication authentication) {

        return ResponseEntity.ok(
                assessmentService.getUserSubmission(assessmentId, authentication));
    }

    @GetMapping("/learner/unit-standard/{unitStandardId}")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentsForLearner(
            @PathVariable Long unitStandardId,
            Authentication authentication) {

        return ResponseEntity.ok(
                assessmentService.getAssessmentsByUnitStandardForLearner(
                        unitStandardId, authentication));
    }

    @GetMapping("/{id}/learner")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentForLearner(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                assessmentService.getAssessmentByIdForLearner(id));
    }

    @PostMapping("/{assessmentId}/submit-test")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> submitTest(
            @PathVariable Long assessmentId,
            @RequestBody TestSubmissionDTO submission,
            Authentication authentication) {

        return ResponseEntity.ok(
                assessmentService.submitTest(submission, authentication));
    }

    // Grading
    @PostMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('FACILITATOR', 'ASSESSOR', 'MODERATOR')")
    public ResponseEntity<ApiResponse<?>> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Object> gradeData,
            Authentication authentication) {

        return ResponseEntity.ok(
                assessmentService.gradeSubmission(
                        submissionId, gradeData, authentication));
    }
}