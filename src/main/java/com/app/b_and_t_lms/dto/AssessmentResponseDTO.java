package com.app.b_and_t_lms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.app.b_and_t_lms.models.Assessment;

import lombok.Data;

@Data
public class AssessmentResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer totalMarks;
    private String type;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Long unitStandardId;
    private String unitStandardTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AssessmentSubmissionDTO submission;
    private Boolean hasSubmission;

    public AssessmentResponseDTO(Assessment assessment) {
        this.id = assessment.getId();
        this.title = assessment.getTitle();
        this.description = assessment.getDescription();
        this.dueDate = assessment.getDueDate();
        this.totalMarks = assessment.getTotalMarks();
        this.type = assessment.getType() != null ? assessment.getType().name() : null;
        this.fileUrl = assessment.getFileUrl();
        this.fileName = assessment.getFileName();
        this.fileSize = assessment.getFileSize();
        this.unitStandardId = assessment.getUnitStandard() != null ? assessment.getUnitStandard().getUnitStandardId() : null;
        this.unitStandardTitle = assessment.getUnitStandard() != null ? assessment.getUnitStandard().getTitle() : null;
        this.createdAt = assessment.getCreatedAt();
        this.updatedAt = assessment.getUpdatedAt();
   
        if (assessment.getAssessmentSubmission() != null) {
            this.submission = new AssessmentSubmissionDTO(assessment.getAssessmentSubmission());
            this.hasSubmission = true;
        } else {
            this.submission = null;
            this.hasSubmission = false;
        }
    }
}