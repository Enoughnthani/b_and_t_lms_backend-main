package com.app.b_and_t_lms.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.app.b_and_t_lms.dto.AssessmentRequestDTO;
import com.app.b_and_t_lms.dto.AssessmentRequestDTO.MatchingPairDTO;
import com.app.b_and_t_lms.dto.AssessmentRequestDTO.OptionDTO;
import com.app.b_and_t_lms.dto.AssessmentRequestDTO.QuestionDTO;
import com.app.b_and_t_lms.dto.AssessmentResponseDTO;
import com.app.b_and_t_lms.dto.AssessmentSubmissionDTO;
import com.app.b_and_t_lms.dto.LearnerAssessmentResponseDTO;
import com.app.b_and_t_lms.dto.QuestionResultDTO;
import com.app.b_and_t_lms.dto.TestResultDTO;
import com.app.b_and_t_lms.dto.TestSubmissionDTO;
import com.app.b_and_t_lms.models.Assessment;
import com.app.b_and_t_lms.models.AssessmentBlank;
import com.app.b_and_t_lms.models.AssessmentQuestion;
import com.app.b_and_t_lms.models.AssessmentSubmission;
import com.app.b_and_t_lms.models.AssessmentSubmissionAnswer;
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
            return new ApiResponse<>(false, "Failed to retrieve assessments: ", null);
        }
    }

    public ApiResponse<?> getAssessmentsByUnitStandardForLearner(
            Long unitStandardId,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            List<Assessment> assessments = assessmentRepository
                    .findByUnitStandardUnitStandardId(unitStandardId);

            List<LearnerAssessmentResponseDTO> dtos = assessments.stream()
                    .map(assessment -> {
                        LearnerAssessmentResponseDTO dto = new LearnerAssessmentResponseDTO(assessment);

                        AssessmentSubmission submission = assessment.getSubmissions().stream()
                                .filter(s -> s.getUser().equals(user))
                                .findFirst()
                                .orElse(null);

                        if (submission != null) {
                            dto.setSubmission(new AssessmentSubmissionDTO(submission));
                        }

                        return dto;
                    })
                    .toList();

            return new ApiResponse<>(true, "Assessments retrieved successfully", dtos);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve assessments: ", null);
        }
    }

    public ApiResponse<?> getAssessmentByIdForLearner(Long id) {
        try {
            Assessment assessment = assessmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            return new ApiResponse<>(true, "Assessment retrieved successfully",
                    new LearnerAssessmentResponseDTO(assessment));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve assessment: ", null);
        }
    }

    public ApiResponse<?> getAssessmentById(Long id) {
        try {
            Assessment assessment = assessmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));
            return new ApiResponse<>(true, "Assessment retrieved successfully", new AssessmentResponseDTO(assessment));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve assessment: ", null);
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
            return new ApiResponse<>(false, "Failed to delete assessment: ", null);
        }
    }

    public ApiResponse<?> getSubmissions(Long assessmentId) {
        try {
            Assessment assessment = assessmentRepository.findById(assessmentId)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            List<AssessmentSubmission> submissions = submissionRepository.findByAssessmentId(assessmentId);

            // For TEST assessments, return detailed results with per-question answers
            if (assessment.getType() == Assessment.AssessmentType.TEST) {
                List<Map<String, Object>> detailedSubmissions = new ArrayList<>();

                for (AssessmentSubmission submission : submissions) {
                    Map<String, Object> detailedSubmission = new HashMap<>();
                    detailedSubmission.put("id", submission.getId());
                    detailedSubmission.put("userId", submission.getUser().getId());
                    detailedSubmission.put("userName",
                            submission.getUser().getFirstname() + " " + submission.getUser().getLastname());
                    detailedSubmission.put("userEmail", submission.getUser().getEmail());
                    detailedSubmission.put("obtainedMarks", submission.getObtainedMarks());
                    detailedSubmission.put("totalMarks", assessment.getTotalMarks());
                    detailedSubmission.put("percentageScore",
                            Math.round((submission.getObtainedMarks() * 100.0 / assessment.getTotalMarks()) * 100.0)
                                    / 100.0);
                    detailedSubmission.put("status", submission.getStatus().name());
                    detailedSubmission.put("submittedAt", submission.getSubmittedAt());
                    detailedSubmission.put("gradedAt", submission.getGradedAt());

                    // Check if passed
                    boolean passed = assessment.getPassingMarks() != null &&
                            submission.getObtainedMarks() >= assessment.getPassingMarks();
                    detailedSubmission.put("passed", passed);

                    // Calculate statistics
                    int fullyCorrect = 0;
                    int partiallyCorrect = 0;
                    int incorrect = 0;

                    // Get per-question answers
                    List<Map<String, Object>> questionAnswers = new ArrayList<>();
                    for (AssessmentQuestion question : assessment.getQuestions()) {
                        AssessmentSubmissionAnswer submittedAnswer = submission.getAssessmentSubmissionAnswer().stream()
                                .filter(a -> a.getQuestionId().equals(question.getId()))
                                .findFirst()
                                .orElse(null);

                        Map<String, Object> qa = new HashMap<>();
                        qa.put("questionId", question.getId());
                        qa.put("questionText", question.getText());
                        qa.put("questionType", question.getType().name());
                        qa.put("maxMarks", question.getMarks());
                        qa.put("marksObtained", submittedAnswer != null ? submittedAnswer.getMarksObtained() : 0);

                        // Track statistics
                        if (submittedAnswer != null) {
                            if (submittedAnswer.getMarksObtained() == question.getMarks()) {
                                fullyCorrect++;
                            } else if (submittedAnswer.getMarksObtained() > 0) {
                                partiallyCorrect++;
                            } else {
                                incorrect++;
                            }
                        } else {
                            incorrect++;
                        }

                        // Get user's answer based on question type
                        if (submittedAnswer != null) {
                            switch (question.getType()) {
                                case TRUE_OR_FALSE:
                                case MULTIPLE_CHOICE:
                                    qa.put("userAnswer", submittedAnswer.getAnswer());
                                    break;
                                case LONG_QUESTION:
                                    qa.put("userAnswer", submittedAnswer.getUserAnswerText());
                                    break;
                                case FILL_IN_BLANKS:
                                    qa.put("userAnswers", submittedAnswer.getAnswers());
                                    break;
                                case MATCHING:
                                    qa.put("userMatchingAnswers", submittedAnswer.getMatchingAnswers());
                                    break;
                            }
                        }

                        // Add correct answer
                        qa.put("correctAnswer", getCorrectAnswerSnapshot(question));

                        questionAnswers.add(qa);
                    }

                    detailedSubmission.put("fullyCorrectCount", fullyCorrect);
                    detailedSubmission.put("partiallyCorrectCount", partiallyCorrect);
                    detailedSubmission.put("incorrectCount", incorrect);
                    detailedSubmission.put("questionAnswers", questionAnswers);

                    detailedSubmissions.add(detailedSubmission);
                }

                return new ApiResponse<>(true, "Test submissions retrieved successfully", detailedSubmissions);
            } else {
                List<AssessmentSubmissionDTO> submissionDTOs = submissions.stream()
                        .map(AssessmentSubmissionDTO::new)
                        .toList();
                return new ApiResponse<>(true, "Submissions retrieved successfully", submissionDTOs);
            }

        } catch (Exception e) {
            e.printStackTrace();
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

            Optional<AssessmentSubmission> submissionOpt = submissionRepository.findByAssessmentAndUser(assessment,
                    user);

            if (!submissionOpt.isPresent()) {
                return new ApiResponse<>(true, "No submission found", null);
            }

            AssessmentSubmission submission = submissionOpt.get();

            // For TEST assessments, return detailed results with per-question answers and
            // marks
            if (assessment.getType() == Assessment.AssessmentType.TEST) {
                Map<String, Object> detailedResult = new HashMap<>();
                detailedResult.put("id", submission.getId());
                detailedResult.put("assessmentId", assessment.getId());
                detailedResult.put("assessmentTitle", assessment.getTitle());
                detailedResult.put("totalMarks", assessment.getTotalMarks());
                detailedResult.put("obtainedMarks", submission.getObtainedMarks());
                detailedResult.put("status", submission.getStatus().name());
                detailedResult.put("submittedAt", submission.getSubmittedAt());
                detailedResult.put("gradedAt", submission.getGradedAt());

                // Calculate percentage
                double percentage = (submission.getObtainedMarks() * 100.0) / assessment.getTotalMarks();
                detailedResult.put("percentageScore", Math.round(percentage * 100.0) / 100.0);

                // Check if passed
                boolean passed = assessment.getPassingMarks() != null &&
                        submission.getObtainedMarks() >= assessment.getPassingMarks();
                detailedResult.put("passed", passed);

                // Build detailed question results
                List<Map<String, Object>> questionResults = new ArrayList<>();
                int fullyCorrect = 0;
                int partiallyCorrect = 0;
                int incorrect = 0;

                for (AssessmentQuestion question : assessment.getQuestions()) {
                    // Find the corresponding submission answer
                    AssessmentSubmissionAnswer submittedAnswer = submission.getAssessmentSubmissionAnswer().stream()
                            .filter(a -> a.getQuestionId().equals(question.getId()))
                            .findFirst()
                            .orElse(null);

                    Map<String, Object> qr = new HashMap<>();
                    qr.put("questionId", question.getId());
                    qr.put("questionText", question.getText());
                    qr.put("questionType", question.getType().name());
                    qr.put("maxMarks", question.getMarks());
                    qr.put("marksObtained", submittedAnswer != null ? submittedAnswer.getMarksObtained() : 0);

                    // Track statistics
                    if (submittedAnswer != null) {
                        if (submittedAnswer.getMarksObtained() == question.getMarks()) {
                            fullyCorrect++;
                        } else if (submittedAnswer.getMarksObtained() > 0) {
                            partiallyCorrect++;
                        } else {
                            incorrect++;
                        }
                    } else {
                        incorrect++;
                    }

                    // Set user's answers based on question type
                    if (submittedAnswer != null) {
                        switch (question.getType()) {
                            case TRUE_OR_FALSE:
                            case MULTIPLE_CHOICE:
                                qr.put("userAnswer", submittedAnswer.getAnswer());
                                break;
                            case LONG_QUESTION:
                                qr.put("userAnswer", submittedAnswer.getUserAnswerText());
                                break;
                            case FILL_IN_BLANKS:
                                qr.put("userAnswers", submittedAnswer.getAnswers());
                                break;
                            case MATCHING:
                                qr.put("userMatchingAnswers", submittedAnswer.getMatchingAnswers());
                                break;
                        }
                    }

                    // Set correct answers
                    qr.put("correctAnswer", getCorrectAnswerSnapshot(question));

                    // For multiple choice, also provide available options
                    if (question.getType() == AssessmentQuestion.QuestionType.MULTIPLE_CHOICE) {
                        List<String> options = question.getOptions().stream()
                                .sorted(Comparator.comparing(QuestionOption::getDisplayOrder))
                                .map(QuestionOption::getText)
                                .collect(Collectors.toList());
                        qr.put("availableOptions", options);
                    }

                    // For fill in blanks, show blank positions
                    if (question.getType() == AssessmentQuestion.QuestionType.FILL_IN_BLANKS) {
                        List<String> blankPositions = question.getBlanks().stream()
                                .sorted(Comparator.comparing(AssessmentBlank::getPosition))
                                .map(blank -> "Blank " + (blank.getPosition() + 1))
                                .collect(Collectors.toList());
                        qr.put("blankPositions", blankPositions);
                    }

                    // For matching, show the correct pairs
                    if (question.getType() == AssessmentQuestion.QuestionType.MATCHING) {
                        List<Map<String, String>> correctPairs = question.getMatchingPairs().stream()
                                .sorted(Comparator.comparing(MatchingPair::getDisplayOrder))
                                .map(pair -> {
                                    Map<String, String> pairMap = new HashMap<>();
                                    pairMap.put("left", pair.getLeftItem());
                                    pairMap.put("right", pair.getRightItem());
                                    return pairMap;
                                })
                                .collect(Collectors.toList());
                        qr.put("correctPairs", correctPairs);
                    }

                    questionResults.add(qr);
                }

                detailedResult.put("questionResults", questionResults);
                detailedResult.put("fullyCorrectCount", fullyCorrect);
                detailedResult.put("partiallyCorrectCount", partiallyCorrect);
                detailedResult.put("incorrectCount", incorrect);

                return new ApiResponse<>(true, "Test submission details retrieved successfully", detailedResult);
            }
            // For other assessment types (LEARNER_WORKBOOK, SUMMATIVE), return regular
            // submission DTO
            else {
                return new ApiResponse<>(true, "Submission found", new AssessmentSubmissionDTO(submission));
            }

        } catch (Exception e) {
            e.printStackTrace();
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

                    if (qDto.getBlanks() != null && !qDto.getBlanks().isEmpty()) {
                        int position = 0;
                        for (String blank : qDto.getBlanks()) {
                            AssessmentBlank AssessmentBlank = new AssessmentBlank();
                            AssessmentBlank.setBlank(blank);
                            AssessmentBlank.setPosition(position++);
                            AssessmentBlank.setQuestion(question);
                            question.getBlanks().add(AssessmentBlank);
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
            return new ApiResponse<>(false, "Failed to create assessment: ", null);
        }
    }

    @Transactional
    public ApiResponse<?> updateAssessment(Long id, AssessmentRequestDTO dto) throws IOException {
        try {
            Assessment assessment = assessmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            // Update basic info
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

                    question.getOptions().clear();
                    question.getBlanks().clear();
                    question.getMatchingPairs().clear();

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

                    if (qDto.getBlanks() != null && !qDto.getBlanks().isEmpty()) {
                        int position = 0;
                        for (String blank : qDto.getBlanks()) {
                            AssessmentBlank assessmentBlank = new AssessmentBlank();
                            assessmentBlank.setBlank(blank);
                            assessmentBlank.setPosition(position++);
                            assessmentBlank.setQuestion(question);
                            question.getBlanks().add(assessmentBlank);
                        }
                    } else if (qDto.getType().equalsIgnoreCase("FILL_IN_BLANKS") && qDto.getText() != null) {

                        if (qDto.getBlanks() != null && !qDto.getBlanks().isEmpty()) {
                            int position = 0;
                            for (String blank : qDto.getBlanks()) {
                                AssessmentBlank assessmentBlank = new AssessmentBlank();
                                assessmentBlank.setBlank(blank);
                                assessmentBlank.setPosition(position++);
                                assessmentBlank.setQuestion(question);
                                question.getBlanks().add(assessmentBlank);
                            }
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

            if (assessment.getQuestions() != null && !assessment.getQuestions().isEmpty()) {
                Integer calculatedTotal = assessment.getQuestions().stream()
                        .mapToInt(AssessmentQuestion::getMarks)
                        .sum();
                if (!calculatedTotal.equals(assessment.getTotalMarks())) {
                    assessment.setTotalMarks(calculatedTotal);
                }
            }

            Assessment saved = assessmentRepository.save(assessment);
            return new ApiResponse<>(true, "Assessment updated successfully",
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

    @Transactional
    public ApiResponse<?> submitTest(TestSubmissionDTO submission, Authentication authentication) {
        try {
            Assessment assessment = assessmentRepository.findById(submission.getAssessmentId())
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<AssessmentSubmission> existingSubmission = submissionRepository.findByAssessmentAndUser(assessment,
                    user);

            if (existingSubmission.isPresent() &&
                    existingSubmission.get().getStatus() == AssessmentSubmission.SubmissionStatus.SUBMITTED) {
                return new ApiResponse<>(false, "You have already submitted this test", null);
            }

            Map<Long, TestSubmissionDTO.AnswerDTO> answerMap = submission.getAnswers().stream()
                    .collect(Collectors.toMap(
                            TestSubmissionDTO.AnswerDTO::getQuestionId,
                            a -> a,
                            (existing, replacement) -> existing));

            List<AssessmentSubmissionAnswer> submissionAnswers = new ArrayList<>();
            int totalObtained = 0;

            for (AssessmentQuestion question : assessment.getQuestions()) {
                TestSubmissionDTO.AnswerDTO userAnswer = answerMap.get(question.getId());

                AssessmentSubmissionAnswer answerEntity = new AssessmentSubmissionAnswer();
                answerEntity.setQuestionId(question.getId());

                if (userAnswer != null) {
                    switch (question.getType()) {
                        case TRUE_OR_FALSE:
                        case MULTIPLE_CHOICE:
                            answerEntity.setAnswer(userAnswer.getAnswer());
                            break;
                        case LONG_QUESTION:
                            answerEntity.setUserAnswerText(userAnswer.getAnswer());
                            break;
                        case FILL_IN_BLANKS:
                            answerEntity.setAnswers(userAnswer.getAnswers());
                            break;
                        case MATCHING:
                            answerEntity.setMatchingAnswers(userAnswer.getMatchingAnswers());
                            break;
                    }
                }

                answerEntity.setCorrectAnswerSnapshot(getCorrectAnswerSnapshot(question));

                int marksObtained = (userAnswer != null) ? evaluateQuestion(question, userAnswer) : 0;
                answerEntity.setMarksObtained(marksObtained);
                totalObtained += marksObtained;

                answerEntity.setSubmission(null);
                submissionAnswers.add(answerEntity);
            }

            AssessmentSubmission assessmentSubmission = existingSubmission.orElse(new AssessmentSubmission());
            assessmentSubmission.setAssessment(assessment);
            assessmentSubmission.setUser(user);
            assessmentSubmission.setObtainedMarks(totalObtained);
            assessmentSubmission.setStatus(AssessmentSubmission.SubmissionStatus.SUBMITTED);
            assessmentSubmission.setSubmittedAt(LocalDateTime.now());
            assessmentSubmission.setGradedAt(LocalDateTime.now());

            if (assessmentSubmission.getAssessmentSubmissionAnswer() != null) {
                assessmentSubmission.getAssessmentSubmissionAnswer().clear();
            }

            for (AssessmentSubmissionAnswer ans : submissionAnswers) {
                ans.setSubmission(assessmentSubmission);
            }
            assessmentSubmission.setAssessmentSubmissionAnswer(submissionAnswers);

            submissionRepository.save(assessmentSubmission);

            Map<String, Object> result = new HashMap<>();
            result.put("obtainedMarks", totalObtained);
            result.put("totalMarks", assessment.getTotalMarks());
            result.put("submissionId", assessmentSubmission.getId());

            List<Map<String, Object>> questionResults = new ArrayList<>();
            for (int i = 0; i < assessment.getQuestions().size(); i++) {
                AssessmentQuestion q = assessment.getQuestions().get(i);
                AssessmentSubmissionAnswer a = submissionAnswers.get(i);

                Map<String, Object> qResult = new HashMap<>();
                qResult.put("questionId", q.getId());
                qResult.put("questionText", q.getText());
                qResult.put("maxMarks", q.getMarks());
                qResult.put("marksObtained", a.getMarksObtained());
                qResult.put("userAnswer", getUserAnswerString(a, q));
                qResult.put("correctAnswer", a.getCorrectAnswerSnapshot());
                questionResults.add(qResult);
            }
            result.put("questionResults", questionResults);

            return new ApiResponse<>(true, "Test submitted successfully", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "Failed to submit test: " + e.getMessage(), null);
        }
    }

    private String getCorrectAnswerSnapshot(AssessmentQuestion question) {
        switch (question.getType()) {
            case TRUE_OR_FALSE:
            case MULTIPLE_CHOICE:
                return question.getCorrectAnswer();
            case FILL_IN_BLANKS:
                return question.getBlanks().stream()
                        .sorted(Comparator.comparing(AssessmentBlank::getPosition))
                        .map(AssessmentBlank::getBlank)
                        .collect(Collectors.joining(" | "));
            case MATCHING:
                return question.getMatchingPairs().stream()
                        .sorted(Comparator.comparing(MatchingPair::getDisplayOrder))
                        .map(p -> p.getLeftItem() + " → " + p.getRightItem())
                        .collect(Collectors.joining("; "));
            case LONG_QUESTION:
                return "Manual grading required";
            default:
                return "";
        }
    }

    private String getUserAnswerString(AssessmentSubmissionAnswer answer, AssessmentQuestion question) {
        switch (question.getType()) {
            case TRUE_OR_FALSE:
            case MULTIPLE_CHOICE:
                return answer.getAnswer();
            case LONG_QUESTION:
                return answer.getUserAnswerText();
            case FILL_IN_BLANKS:
                return answer.getAnswers() != null ? String.join(" | ", answer.getAnswers()) : "";
            case MATCHING:
                return answer.getMatchingAnswers() != null ? answer.getMatchingAnswers().entrySet().stream()
                        .map(e -> e.getKey() + " → " + e.getValue())
                        .collect(Collectors.joining("; ")) : "";
            default:
                return "";
        }
    }

    public ApiResponse<?> getTestResult(Long assessmentId, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Assessment assessment = assessmentRepository.findById(assessmentId)
                    .orElseThrow(() -> new RuntimeException("Assessment not found"));

            AssessmentSubmission submission = submissionRepository.findByAssessmentAndUser(assessment, user)
                    .orElseThrow(() -> new RuntimeException("No submission found for this test"));

            TestResultDTO result = new TestResultDTO();
            result.setAssessmentId(assessment.getId());
            result.setAssessmentTitle(assessment.getTitle());
            result.setTotalMarks(assessment.getTotalMarks());
            result.setObtainedMarks(submission.getObtainedMarks());
            result.setSubmittedAt(submission.getSubmittedAt());
            result.setGradedAt(submission.getGradedAt());

            List<QuestionResultDTO> questionResults = new ArrayList<>();
            for (AssessmentQuestion question : assessment.getQuestions()) {

                AssessmentSubmissionAnswer storedAnswer = submission.getAssessmentSubmissionAnswer().stream()
                        .filter(a -> a.getQuestionId().equals(question.getId()))
                        .findFirst()
                        .orElse(null);

                QuestionResultDTO qr = new QuestionResultDTO();
                qr.setQuestionId(question.getId());
                qr.setQuestionText(question.getText());
                qr.setMaxMarks(question.getMarks());
                qr.setMarksObtained(storedAnswer != null ? storedAnswer.getMarksObtained() : 0);
                qr.setQuestionType(question.getType().name());

                // Set user answer based on question type
                if (storedAnswer != null) {
                    switch (question.getType()) {
                        case TRUE_OR_FALSE:
                        case MULTIPLE_CHOICE:
                        case LONG_QUESTION:
                            qr.setUserAnswer(storedAnswer.getAnswer());
                            break;
                        case FILL_IN_BLANKS:
                            qr.setUserAnswers(storedAnswer.getAnswers());
                            break;
                        case MATCHING:
                            qr.setUserMatchingAnswers(storedAnswer.getMatchingAnswers());
                            break;
                    }
                }

                // Set correct answer (for auto-graded types)
                if (question.getType() != AssessmentQuestion.QuestionType.LONG_QUESTION) {
                    qr.setCorrectAnswer(formatCorrectAnswer(question));
                } else {
                    qr.setCorrectAnswer("To be graded by assessor");
                }

                questionResults.add(qr);
            }

            result.setQuestionResults(questionResults);
            return new ApiResponse<>(true, "Test result retrieved successfully", result);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "Failed to retrieve test result: " + e.getMessage(), null);
        }
    }

    private String formatCorrectAnswer(AssessmentQuestion question) {
        switch (question.getType()) {
            case TRUE_OR_FALSE:
            case MULTIPLE_CHOICE:
                return question.getCorrectAnswer();
            case FILL_IN_BLANKS:
                return question.getBlanks().stream()
                        .sorted(Comparator.comparing(AssessmentBlank::getPosition))
                        .map(AssessmentBlank::getBlank)
                        .collect(Collectors.joining(", "));
            case MATCHING:
                return question.getMatchingPairs().stream()
                        .sorted(Comparator.comparing(MatchingPair::getDisplayOrder))
                        .map(p -> p.getLeftItem() + " → " + p.getRightItem())
                        .collect(Collectors.joining("; "));
            default:
                return "";
        }
    }

    private int evaluateQuestion(AssessmentQuestion question, TestSubmissionDTO.AnswerDTO userAnswer) {
        switch (question.getType()) {
            case TRUE_OR_FALSE:
                if (userAnswer.getAnswer() != null &&
                        userAnswer.getAnswer().equalsIgnoreCase(question.getCorrectAnswer())) {
                    return question.getMarks();
                }
                return 0;

            case MULTIPLE_CHOICE:
                if (userAnswer.getAnswer() != null &&
                        userAnswer.getAnswer().equalsIgnoreCase(question.getCorrectAnswer())) {
                    return question.getMarks();
                }
                return 0;

            case FILL_IN_BLANKS:
                return evaluateFillInBlanks(question, userAnswer);

            case LONG_QUESTION:
                return 0;

            case MATCHING:
                return evaluateMatching(question, userAnswer);

            default:
                return 0;
        }
    }

    private int evaluateFillInBlanks(AssessmentQuestion question, TestSubmissionDTO.AnswerDTO userAnswer) {
        List<String> correctBlanks = question.getBlanks().stream()
                .sorted(Comparator.comparing(AssessmentBlank::getPosition))
                .map(AssessmentBlank::getBlank)
                .collect(Collectors.toList());

        List<String> userBlanks = userAnswer.getAnswers();

        if (userBlanks == null || userBlanks.isEmpty()) {
            return 0;
        }

        int marksPerBlank = question.getMarks() / correctBlanks.size();
        int obtained = 0;

        for (int i = 0; i < correctBlanks.size() && i < userBlanks.size(); i++) {
            if (correctBlanks.get(i).equalsIgnoreCase(userBlanks.get(i).trim())) {
                obtained += marksPerBlank;
            }
        }

        return obtained;
    }

    private int evaluateMatching(AssessmentQuestion question, TestSubmissionDTO.AnswerDTO userAnswer) {
        Map<String, String> userMatches = userAnswer.getMatchingAnswers();
        if (userMatches == null || userMatches.isEmpty()) {
            return 0;
        }

        List<MatchingPair> correctPairs = question.getMatchingPairs().stream()
                .sorted(Comparator.comparing(MatchingPair::getDisplayOrder))
                .collect(Collectors.toList());

        int marksPerPair = question.getMarks() / correctPairs.size();
        int correctMatches = 0;

        for (MatchingPair pair : correctPairs) {
            String userRightItem = userMatches.get(pair.getLeftItem());
            if (userRightItem != null && userRightItem.equals(pair.getRightItem())) {
                correctMatches++;
            }
        }

        return correctMatches * marksPerPair;
    }

    @Transactional
    public ApiResponse<?> gradeSubmission(Long submissionId, Map<String, Object> gradeData,
            Authentication authentication) {
        try {
            AssessmentSubmission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission not found"));

            Integer obtainedMarks = null;
            if (gradeData.get("obtainedMarks") instanceof Integer) {
                obtainedMarks = (Integer) gradeData.get("obtainedMarks");
            } else if (gradeData.get("obtainedMarks") instanceof Number) {
                obtainedMarks = ((Number) gradeData.get("obtainedMarks")).intValue();
            }

          
            Object questionMarksObj = gradeData.get("questionMarks");
            Map<Long, Integer> questionMarks = new HashMap<>();

            if (questionMarksObj instanceof Map) {
                Map<?, ?> rawMap = (Map<?, ?>) questionMarksObj;
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    try {
                        Long questionId = Long.valueOf(entry.getKey().toString());
                        Integer marks = null;
                        if (entry.getValue() instanceof Integer) {
                            marks = (Integer) entry.getValue();
                        } else if (entry.getValue() instanceof Number) {
                            marks = ((Number) entry.getValue()).intValue();
                        }
                        if (marks != null) {
                            questionMarks.put(questionId, marks);
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }

            if (!questionMarks.isEmpty() && submission.getAssessmentSubmissionAnswer() != null) {
                for (AssessmentSubmissionAnswer answer : submission.getAssessmentSubmissionAnswer()) {
                    Integer newMarks = questionMarks.get(answer.getQuestionId());
                    if (newMarks != null) {
                        answer.setMarksObtained(newMarks);
                    }
                }
            }

            if (obtainedMarks != null) {
                submission.setObtainedMarks(obtainedMarks);
            }
            submission.setStatus(AssessmentSubmission.SubmissionStatus.GRADED);
            submission.setGradedAt(LocalDateTime.now());

            submissionRepository.save(submission);

            return new ApiResponse<>(true, "Submission graded successfully", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "Failed to grade submission: " + e.getMessage(), null);
        }
    }

}