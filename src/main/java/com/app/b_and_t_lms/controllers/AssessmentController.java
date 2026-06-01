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
@PreAuthorize("hasRole('FACILITATOR,ASSESSOR,')")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping("/unit-standard/{unitStandardId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR','LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentsByUnitStandard(@PathVariable Long unitStandardId,
            Authentication authentication) {
        ApiResponse<?> response = assessmentService.getAssessmentsByUnitStandard(unitStandardId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentById(@PathVariable Long id) {
        ApiResponse<?> response = assessmentService.getAssessmentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<ApiResponse<?>> createAssessment(@ModelAttribute AssessmentRequestDTO dto)
            throws IOException {
        ApiResponse<?> response = assessmentService.createAssessment(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<ApiResponse<?>> updateAssessment(@PathVariable Long id,
            @ModelAttribute AssessmentRequestDTO dto) throws IOException {

        ApiResponse<?> response = assessmentService.updateAssessment(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<ApiResponse<?>> deleteAssessment(@PathVariable Long id) {
        ApiResponse<?> response = assessmentService.deleteAssessment(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{assessmentId}/submissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<ApiResponse<?>> getSubmissions(@PathVariable Long assessmentId) {
        ApiResponse<?> response = assessmentService.getSubmissions(assessmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{filename}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR', 'LEARNER')")
    public ResponseEntity<?> downloadAssessmentFile(@PathVariable String filename) throws IOException {
        return assessmentService.downloadAssessmentFile(filename);
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> submitAssessment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("assessmentId") Long assessmentId, Authentication authentication) throws IOException {

        ApiResponse<?> response = assessmentService.submitAssessment(file, assessmentId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{assessmentId}/submission")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> getMySubmission(
            @PathVariable Long assessmentId,
            Authentication authentication) {
        ApiResponse<?> response = assessmentService.getUserSubmission(assessmentId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/learner/unit-standard/{unitStandardId}")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentsForLearner(
            @PathVariable Long unitStandardId,
            Authentication authentication) {
        ApiResponse<?> response = assessmentService
                .getAssessmentsByUnitStandardForLearner(unitStandardId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/learner")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentForLearner(@PathVariable Long id) {
        ApiResponse<?> response = assessmentService.getAssessmentByIdForLearner(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{assessmentId}/submit-test")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> submitTest(@PathVariable Long assessmentId,
            @RequestBody TestSubmissionDTO submission,
            Authentication authentication) {
        ApiResponse<?> response = assessmentService.submitTest(submission, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasRole('FACILITATOR')")
    public ResponseEntity<ApiResponse<?>> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Object> gradeData,
            Authentication authentication) {
        ApiResponse<?> response = assessmentService.gradeSubmission(submissionId, gradeData, authentication);
        return ResponseEntity.ok(response);
    }
}