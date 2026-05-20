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
import com.app.b_and_t_lms.dto.TestSubmissionDTO;
import com.app.b_and_t_lms.models.Assessment;
import com.app.b_and_t_lms.models.AssessmentBlank;
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
            List<AssessmentSubmissionDTO> submissions = submissionRepository.findByAssessmentId(assessmentId).stream()
                    .map(AssessmentSubmissionDTO::new).toList();
            return new ApiResponse<>(true, "Submissions retrieved successfully", submissions);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve submissions: ", null);
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
            return new ApiResponse<>(false, "Failed to get submission: ", null);
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

            
            if (assessment.getType() != Assessment.AssessmentType.TEST) {
                return new ApiResponse<>(false, "This endpoint is only for test submissions", null);
            }

          
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

      
            Optional<AssessmentSubmission> existingSubmission = submissionRepository.findByAssessmentAndUser(assessment,
                    user);

            if (existingSubmission.isPresent()
                    && existingSubmission.get().getStatus() == AssessmentSubmission.SubmissionStatus.SUBMITTED) {
                return new ApiResponse<>(false, "You have already submitted this test", null);
            }

         
            int obtainedMarks = calculateMarks(assessment, submission.getAnswers());

          
            AssessmentSubmission assessmentSubmission = existingSubmission.orElse(new AssessmentSubmission());
            assessmentSubmission.setAssessment(assessment);
            assessmentSubmission.setUser(user);
            assessmentSubmission.setObtainedMarks(obtainedMarks);
            assessmentSubmission.setStatus(AssessmentSubmission.SubmissionStatus.SUBMITTED);
            assessmentSubmission.setSubmittedAt(LocalDateTime.now());

            ObjectMapper mapper = new ObjectMapper();
            String answersJson = mapper.writeValueAsString(submission.getAnswers());
            assessmentSubmission.setFeedback(answersJson); 

            submissionRepository.save(assessmentSubmission);

            return new ApiResponse<>(true,
                    String.format("Test submitted successfully! Score: %d/%d", obtainedMarks,
                            assessment.getTotalMarks()),
                    Map.of("obtainedMarks", obtainedMarks, "totalMarks", assessment.getTotalMarks()));

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "Failed to submit test: " + e.getMessage(), null);
        }
    }

    private int calculateMarks(Assessment assessment, List<TestSubmissionDTO.AnswerDTO> answers) {
        int totalObtained = 0;

        
        Map<Long, TestSubmissionDTO.AnswerDTO> answerMap = answers.stream()
                .collect(Collectors.toMap(
                        TestSubmissionDTO.AnswerDTO::getQuestionId,
                        a -> a,
                        (existing, replacement) -> existing));

        for (AssessmentQuestion question : assessment.getQuestions()) {
            TestSubmissionDTO.AnswerDTO userAnswer = answerMap.get(question.getId());
            if (userAnswer != null) {
                totalObtained += evaluateQuestion(question, userAnswer);
            }
        }

        return totalObtained;
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

        List<String> userBlanks = userAnswer.getAnswerArray();

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

}