package com.app.b_and_t_lms.controllers;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.services.AssessmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACILITATOR,ASSESSOR,')")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping("/unit-standard/{unitStandardId}/facilitator")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<ApiResponse<?>> getAssessmentsByUnitStandard(@PathVariable Long unitStandardId) {
        ApiResponse<?> response = assessmentService.getAssessmentsByUnitStandard(unitStandardId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unit-standard/{unitStandardId}/learner")
    @PreAuthorize("hasAnyRole('LEARNER')")
    public ResponseEntity<ApiResponse<?>> getAssessmentsByUnitStandard(@PathVariable Long unitStandardId,Authentication authentication) {
        ApiResponse<?> response = assessmentService.getAssessmentsByUnitStandard(unitStandardId,authentication);
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
    public ResponseEntity<ApiResponse<?>> createAssessment(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "dueDate", required = false) String dueDate,
            @RequestParam("totalMarks") Integer totalMarks,
            @RequestParam("type") String type,
            @RequestParam("unitStandardId") Long unitStandardId,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        ApiResponse<?> response = assessmentService.createAssessment(title, description, dueDate, totalMarks, type,
                unitStandardId, file);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FACILITATOR')")
    public ResponseEntity<ApiResponse<?>> updateAssessment(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "dueDate", required = false) String dueDate,
            @RequestParam("totalMarks") Integer totalMarks,
            @RequestParam("type") String type,
            @RequestParam("unitStandardId") Long unitStandardId,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        ApiResponse<?> response = assessmentService.updateAssessment(id, title, description, dueDate, totalMarks, type,
                unitStandardId, file);
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
}