package com.app.b_and_t_lms.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import com.app.b_and_t_lms.dto.AssessmentRequestDTO;
import com.app.b_and_t_lms.dto.AssessmentRequestDTO.MatchingPairDTO;
import com.app.b_and_t_lms.dto.AssessmentRequestDTO.OptionDTO;
import com.app.b_and_t_lms.dto.AssessmentRequestDTO.QuestionDTO;
import com.app.b_and_t_lms.dto.AssessmentResponseDTO;
import com.app.b_and_t_lms.dto.AssessmentSubmissionDTO;
import com.app.b_and_t_lms.models.Assessment;
import com.app.b_and_t_lms.models.AssessmentQuestion;
import com.app.b_and_t_lms.models.AssessmentSubmission;
import com.app.b_and_t_lms.models.MatchingPair;
import com.app.b_and_t_lms.models.QuestionOption;
import com.app.b_and_t_lms.models.UnitStandard;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.repositories.AssessmentRepository;
import com.app.b_and_t_lms.repositories.AssessmentSubmissionRepository;
import com.app.b_and_t_lms.repositories.UnitStandardRepository;
import com.app.b_and_t_lms.repositories.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public ApiResponse<?> getAssessmentsByUnitStandard(Long unitStandardId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            List<Assessment> assessments = assessmentRepository.findByUnitStandardUnitStandardId(unitStandardId);
            List<AssessmentResponseDTO> dtos = assessments.stream()
                    .map(assessment -> {
                        AssessmentResponseDTO dto = new AssessmentResponseDTO(assessment);

                        AssessmentSubmission submission = assessment.getSubmissions().stream()
                                .filter(s -> s.getUser().equals(user)).findFirst().orElse(null);

                        if (submission != null) {
                            dto.setSubmission(new AssessmentSubmissionDTO(submission));
                        }

                        return dto;

                    }).toList();

            return new ApiResponse<>(true, "Assessments retrieved successfully ", dtos);
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
            List<AssessmentSubmissionDTO> submissions = submissionRepository.findByAssessmentId(assessmentId).stream()
                    .map(AssessmentSubmissionDTO::new).toList();
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

            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AssessmentSubmission submission = submissionRepository.findByAssessmentAndUser(assessment, user)
                    .orElse(null);

            if (submission == null) {
                submission = new AssessmentSubmission();
                submission.setAssessment(assessment);
                submission.setUser(user);
            } else {

                deleteOldFile(assessment.getFileUrl());
            }

            String savedFileName = saveFile(file, SUBMISSION_DIR);

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

    @Transactional
    public ApiResponse<?> createAssessment(AssessmentRequestDTO dto) throws IOException {
        try {

            UnitStandard unitStandard = unitStandardRepository.findById(dto.getUnitStandardId())
                    .orElseThrow(() -> new RuntimeException("Unit Standard not found"));

            Assessment assessment = new Assessment();
            assessment.setTitle(dto.getTitle());
            assessment.setDescription(dto.getDescription());

            ApiResponse<?> dateValidationResponse = validateAndSetDates(assessment, dto);
            if (dateValidationResponse != null) {
                return dateValidationResponse;
            }

            assessment.setTotalMarks(dto.getTotalMarks());
            assessment.setType(Assessment.AssessmentType.valueOf(dto.getType()));
            assessment.setUnitStandard(unitStandard);

            if (dto.getDurationMinutes() != null) {
                assessment.setDurationMinutes(dto.getDurationMinutes());
            }

            if (dto.getPassingMarks() != null) {
                assessment.setPassingMarks(dto.getPassingMarks());
            }

            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                String savedFileName = saveFile(dto.getFile(), ASSESSMENT_DIR);
                assessment.setFileUrl("/uploads/" + ASSESSMENT_DIR + savedFileName);
                assessment.setFileName(dto.getFile().getOriginalFilename());
                assessment.setFileSize(dto.getFile().getSize());
            }

            if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
                List<AssessmentQuestion> assessmentQuestions = new ArrayList<>();
                int order = 0;

                ObjectMapper mapper = new ObjectMapper();
                List<AssessmentRequestDTO.QuestionDTO> questionList = mapper.readValue(
                        dto.getQuestions(),
                        new TypeReference<List<AssessmentRequestDTO.QuestionDTO>>() {
                        });

                for (QuestionDTO qDto : questionList) {
                    AssessmentQuestion question = new AssessmentQuestion();
                    question.setType(AssessmentQuestion.QuestionType.valueOf(qDto.getType().toUpperCase()));
                    question.setText(qDto.getText());
                    question.setMarks(qDto.getMarks());
                    question.setExplanation(qDto.getExplanation());
                    question.setCorrectAnswer(qDto.getCorrectAnswer());
                    question.setSampleAnswer(qDto.getSampleAnswer());
                    question.setDisplayOrder(order++);
                    question.setAssessment(assessment);

                    if (qDto.getOptions() != null && !qDto.getOptions().isEmpty()) {
                        int optOrder = 0;
                        for (OptionDTO oDto : qDto.getOptions()) {
                            QuestionOption option = new QuestionOption();
                            option.setText(oDto.getText());
                            option.setDisplayOrder(optOrder++);
                            option.setQuestion(question);
                            question.getOptions().add(option);
                        }
                    }

                    if (qDto.getMatchingPairs() != null && !qDto.getMatchingPairs().isEmpty()) {
                        int pairOrder = 0;
                        for (MatchingPairDTO mDto : qDto.getMatchingPairs()) {
                            MatchingPair pair = new MatchingPair();
                            pair.setLeftItem(mDto.getLeftItem());
                            pair.setRightItem(mDto.getRightItem());
                            pair.setDisplayOrder(pairOrder++);
                            pair.setQuestion(question);
                            question.getMatchingPairs().add(pair);
                        }
                    }

                    assessmentQuestions.add(question);
                }
                assessment.setQuestions(assessmentQuestions);
            }

            Assessment savedAssessment = assessmentRepository.save(assessment);

            String message = assessment.getType() == Assessment.AssessmentType.TEST && assessment.getQuestions() != null
                    ? "Test assessment saved successfully with " + assessment.getQuestions().size() + " questions"
                    : "Assessment created successfully";

            return new ApiResponse<>(true, message, new AssessmentResponseDTO(savedAssessment));

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to create assessment: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponse<?> updateAssessment(Long id, AssessmentRequestDTO dto) throws IOException {
        try {
            Assessment assessment = assessmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            assessment.setTitle(dto.getTitle());
            assessment.setDescription(dto.getDescription());

            ApiResponse<?> dateValidationResponse = validateAndSetDates(assessment, dto);
            if (dateValidationResponse != null) {
                return dateValidationResponse;
            }

            assessment.setTotalMarks(dto.getTotalMarks());
            assessment.setType(Assessment.AssessmentType.valueOf(dto.getType()));

            if (dto.getDurationMinutes() != null) {
                assessment.setDurationMinutes(dto.getDurationMinutes());
            }

            if (dto.getPassingMarks() != null) {
                assessment.setPassingMarks(dto.getPassingMarks());
            }

            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                if (assessment.getFileUrl() != null) {
                    deleteOldFile(assessment.getFileUrl());
                }
                String savedFileName = saveFile(dto.getFile(), ASSESSMENT_DIR);
                assessment.setFileUrl("/uploads/" + ASSESSMENT_DIR + savedFileName);
                assessment.setFileName(dto.getFile().getOriginalFilename());
                assessment.setFileSize(dto.getFile().getSize());
            }

            if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                List<AssessmentRequestDTO.QuestionDTO> questionList = mapper.readValue(
                        dto.getQuestions(),
                        new TypeReference<List<AssessmentRequestDTO.QuestionDTO>>() {
                        });

                assessment.getQuestions().clear();

                int order = 0;

                for (AssessmentRequestDTO.QuestionDTO qDto : questionList) {
                    AssessmentQuestion question = new AssessmentQuestion();
                    question.setType(AssessmentQuestion.QuestionType.valueOf(qDto.getType()));
                    question.setText(qDto.getText());
                    question.setMarks(qDto.getMarks());
                    question.setExplanation(qDto.getExplanation());
                    question.setCorrectAnswer(qDto.getCorrectAnswer());
                    question.setSampleAnswer(qDto.getSampleAnswer());
                    question.setDisplayOrder(order++);
                    question.setAssessment(assessment);

                    if (question.getOptions() == null) {
                        question.setOptions(new ArrayList<>());
                    }
                    if (question.getMatchingPairs() == null) {
                        question.setMatchingPairs(new ArrayList<>());
                    }

                    if (qDto.getOptions() != null && !qDto.getOptions().isEmpty()) {
                        int optOrder = 0;
                        for (AssessmentRequestDTO.OptionDTO oDto : qDto.getOptions()) {
                            QuestionOption option = new QuestionOption();
                            option.setText(oDto.getText());
                            option.setDisplayOrder(optOrder++);
                            option.setQuestion(question);
                            question.getOptions().add(option);
                        }
                    }

                    if (qDto.getMatchingPairs() != null && !qDto.getMatchingPairs().isEmpty()) {
                        int pairOrder = 0;
                        for (AssessmentRequestDTO.MatchingPairDTO mDto : qDto.getMatchingPairs()) {
                            MatchingPair pair = new MatchingPair();
                            pair.setLeftItem(mDto.getLeftItem());
                            pair.setRightItem(mDto.getRightItem());
                            pair.setDisplayOrder(pairOrder++);
                            pair.setQuestion(question);
                            question.getMatchingPairs().add(pair);
                        }
                    }

                    assessment.getQuestions().add(question);
                }
            } else if (dto.getQuestions() != null && dto.getQuestions().isEmpty()) {
                assessment.getQuestions().clear();
            }

            Assessment saved = assessmentRepository.save(assessment);
            return new ApiResponse<>(true, "Assessment updated successfully " + dto.getStartDate(),
                    new AssessmentResponseDTO(saved));

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "Failed to update assessment: " + e.getMessage(), null);
        }
    }

    private ApiResponse<?> validateAndSetDates(
            Assessment assessment,
            AssessmentRequestDTO dto) {

        LocalDateTime startDate = null;
        LocalDateTime dueDate = null;
        LocalDateTime today = LocalDateTime.now();

        try {

            if (dto.getStartDate() != null && !dto.getStartDate().isBlank()) {

                startDate = LocalDateTime.parse(
                        dto.getStartDate(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            if (dto.getDueDate() != null && !dto.getDueDate().isBlank()) {

                dueDate = LocalDateTime.parse(
                        dto.getDueDate(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

        } catch (DateTimeParseException e) {

            return new ApiResponse<>(
                    false,
                    "Invalid date format. Please use YYYY-MM-DDTHH:mm",
                    null);
        }

        

        if (dueDate != null && dueDate.isBefore(today)) {

            return new ApiResponse<>(
                    false,
                    String.format(
                            "Due date (%s) cannot be in the past",
                            dueDate),
                    null);
        }

        if (startDate != null
                && dueDate != null
                && startDate.isAfter(dueDate)) {

            return new ApiResponse<>(
                    false,
                    String.format(
                            "Start date (%s) cannot be after due date (%s)",
                            startDate,
                            dueDate),
                    null);
        }

        assessment.setStartDate(startDate);
        assessment.setDueDate(dueDate);

        return null;
    }

}