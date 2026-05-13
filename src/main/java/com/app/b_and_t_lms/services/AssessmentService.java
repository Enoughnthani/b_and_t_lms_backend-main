package com.app.b_and_t_lms.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.AssessmentResponseDTO;
import com.app.b_and_t_lms.dto.AssessmentSubmissionDTO;
import com.app.b_and_t_lms.models.Assessment;
import com.app.b_and_t_lms.models.AssessmentSubmission;
import com.app.b_and_t_lms.models.UnitStandard;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.repositories.AssessmentRepository;
import com.app.b_and_t_lms.repositories.AssessmentSubmissionRepository;
import com.app.b_and_t_lms.repositories.UnitStandardRepository;
import com.app.b_and_t_lms.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentSubmissionRepository submissionRepository;
    private final UnitStandardRepository unitStandardRepository;
    private final UserRepository userRepository;

    private static final String UPLOAD_BASE_DIR = "C:/uploads/";
    private static final String ASSESSMENT_DIR = "assessments/";
    private static final String SUBMISSION_DIR = "submissions/";

    public ApiResponse<?> getAssessmentsByUnitStandard(Long unitStandardId) {
        try {
            List<Assessment> assessments = assessmentRepository.findByUnitStandardUnitStandardId(unitStandardId);
            List<AssessmentResponseDTO> dtos = assessments.stream()
                    .map(AssessmentResponseDTO::new)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Assessments retrieved successfully", dtos);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve assessments: " + e.getMessage(), null);
        }
    }

    public ApiResponse<?> getAssessmentById(Long id) {
        try {
            Assessment assessment = assessmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));
            return new ApiResponse<>(true, "Assessment retrieved successfully", new AssessmentResponseDTO(assessment));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve assessment: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponse<?> createAssessment(String title, String description, String dueDateStr,
            Integer totalMarks, String type, Long unitStandardId,
            MultipartFile file) throws IOException {
        try {
            UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                    .orElseThrow(() -> new RuntimeException("Unit Standard not found"));

            Assessment assessment = new Assessment();
            assessment.setTitle(title);
            assessment.setDescription(description);
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                assessment.setDueDate(LocalDate.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE));
            }
            assessment.setTotalMarks(totalMarks);
            assessment.setType(Assessment.AssessmentType.valueOf(type));
            assessment.setUnitStandard(unitStandard);
            assessment.setAssessmentSubmission(new ArrayList<>());

            if (file != null && !file.isEmpty()) {
                String savedFileName = saveFile(file, ASSESSMENT_DIR);
                assessment.setFileUrl("/uploads/" + ASSESSMENT_DIR + savedFileName);
                assessment.setFileName(file.getOriginalFilename());
                assessment.setFileSize(file.getSize());
            }

            Assessment saved = assessmentRepository.save(assessment);
            return new ApiResponse<>(true, "Assessment created successfully", new AssessmentResponseDTO(saved));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to create assessment: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponse<?> updateAssessment(Long id, String title, String description, String dueDateStr,
            Integer totalMarks, String type, Long unitStandardId,
            MultipartFile file) throws IOException {
        try {
            Assessment assessment = assessmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            assessment.setTitle(title);
            assessment.setDescription(description);
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                assessment.setDueDate(LocalDate.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE));
            }
            assessment.setTotalMarks(totalMarks);
            assessment.setType(Assessment.AssessmentType.valueOf(type));

            if (file != null && !file.isEmpty()) {
                if (assessment.getFileUrl() != null) {
                    deleteOldFile(assessment.getFileUrl());
                }
                String savedFileName = saveFile(file, ASSESSMENT_DIR);
                assessment.setFileUrl("/uploads/" + ASSESSMENT_DIR + savedFileName);
                assessment.setFileName(file.getOriginalFilename());
                assessment.setFileSize(file.getSize());
            }

            Assessment saved = assessmentRepository.save(assessment);
            return new ApiResponse<>(true, "Assessment updated successfully", new AssessmentResponseDTO(saved));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to update assessment: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponse<?> deleteAssessment(Long id) {
        try {
            Assessment assessment = assessmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            if (assessment.getFileUrl() != null) {
                deleteOldFile(assessment.getFileUrl());
            }

            assessmentRepository.delete(assessment);
            return new ApiResponse<>(true, "Assessment deleted successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete assessment: " + e.getMessage(), null);
        }
    }

    public ApiResponse<?> getSubmissions(Long assessmentId) {
        try {
            List<AssessmentSubmission> submissions = submissionRepository.findByAssessmentId(assessmentId);
            return new ApiResponse<>(true, "Submissions retrieved successfully", submissions);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve submissions: " + e.getMessage(), null);
        }
    }

    public ResponseEntity<?> downloadAssessmentFile(String filename) throws IOException {
        try {
            Path filePath = Paths.get(UPLOAD_BASE_DIR + ASSESSMENT_DIR + filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Transactional
    public ApiResponse<?> submitAssessment(MultipartFile file, Long assessmentId, Authentication authentication) {
        try {
            Assessment assessment = assessmentRepository.findById(assessmentId)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            // Get the currently authenticated user
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String savedFileName = saveFile(file, SUBMISSION_DIR);

            AssessmentSubmission submission = new AssessmentSubmission();
            submission.setAssessment(assessment);
            submission.setUser(user);
            submission.setFileUrl("/uploads/" + SUBMISSION_DIR + savedFileName);
            submission.setFileName(file.getOriginalFilename());
            submission.setFileSize(file.getSize());
            submission.setStatus(AssessmentSubmission.SubmissionStatus.SUBMITTED);

            submissionRepository.save(submission);
            return new ApiResponse<>(true, "Assessment submitted successfully", null);
        } catch (Exception e) {

            Throwable root = e;

            while (root.getCause() != null) {
                root = root.getCause();
            }

            String message = root.getMessage();

            if (message.contains("Duplicate entry")) {
                message = "You have already submitted this assessment.";
            }

            return new ApiResponse<>(false, message, null);
        }
    }

    private String saveFile(MultipartFile file, String subDir) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_BASE_DIR + subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        Files.copy(file.getInputStream(), filePath);
        return filename;
    }

    private void deleteOldFile(String fileUrl) throws IOException {
        if (fileUrl != null && fileUrl.contains("/uploads/")) {
            String relativePath = fileUrl.substring(fileUrl.indexOf("/uploads/") + 9);
            Path filePath = Paths.get(UPLOAD_BASE_DIR + relativePath);
            Files.deleteIfExists(filePath);
        }
    }

    public ApiResponse<?> getUserSubmission(Long assessmentId, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Assessment assessment = assessmentRepository.findById(assessmentId)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));


            Optional<AssessmentSubmission> submission = submissionRepository.findByAssessmentAndUser(assessment, user);

            if (submission.isPresent()) {
                return new ApiResponse<>(true, "Submission found", new AssessmentSubmissionDTO(submission.get()));
            } else {
                return new ApiResponse<>(true, "No submission found", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to get submission: " + e.getMessage(), null);
        }
    }
}