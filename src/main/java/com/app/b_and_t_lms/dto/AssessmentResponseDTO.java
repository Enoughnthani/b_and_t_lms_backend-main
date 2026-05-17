package com.app.b_and_t_lms.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.app.b_and_t_lms.models.Assessment;

import lombok.Data;

@Data
public class AssessmentResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime startDate;
    private Integer totalMarks;
    private String type;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Long unitStandardId;
    private String unitStandardTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean hasSubmission;
    private Integer submittedCount;
    private AssessmentSubmissionDTO submission;
    private List<QuestionResponseDTO> questions;

    public AssessmentResponseDTO(Assessment assessment) {
        this.id = assessment.getId();
        this.title = assessment.getTitle();
        this.description = assessment.getDescription();
        this.dueDate = assessment.getDueDate();
        this.startDate = assessment.getStartDate();
        this.totalMarks = assessment.getTotalMarks();
        this.type = assessment.getType() != null ? assessment.getType().name() : null;
        this.fileUrl = assessment.getFileUrl();
        this.fileName = assessment.getFileName();
        this.fileSize = assessment.getFileSize();
        this.unitStandardId = assessment.getUnitStandard() != null ? assessment.getUnitStandard().getUnitStandardId()
                : null;
        this.unitStandardTitle = assessment.getUnitStandard() != null ? assessment.getUnitStandard().getTitle() : null;
        this.createdAt = assessment.getCreatedAt();
        this.updatedAt = assessment.getUpdatedAt();
        this.hasSubmission = assessment.getSubmissions() != null && !assessment.getSubmissions().isEmpty();
        this.submittedCount = hasSubmission ? assessment.getSubmissions().size() : 0;

        if (assessment.getQuestions() != null) {
            questions = assessment.getQuestions().stream().map(QuestionResponseDTO::new).toList();
        }

    }
}