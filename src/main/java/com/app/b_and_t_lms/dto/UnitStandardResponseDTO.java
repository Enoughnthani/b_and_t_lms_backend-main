package com.app.b_and_t_lms.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.app.b_and_t_lms.models.UnitStandard;
import com.app.b_and_t_lms.models.UnitStandard.UnitStandardStatus;
import com.app.b_and_t_lms.models.UnitStandard.UnitStandardType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitStandardResponseDTO {

    private Long unitStandardId;
    private String title;
    private String description;
    private String purpose;
    private String learningAssumed;
    private Integer credits;
    private Integer notionalHours;
    private String nqfLevel;
    private UnitStandardType type;
    private UnitStandardStatus status;
    private String moderationBody;
    private String rangeStatement;
    private List<String> specificOutcomes;
    private List<String> assessmentCriteria;
    private List<String> criticalCrossFieldOutcomes;

    private Long programId;
    private String programName;

    private Integer contentCount;
    private List<ContentResponseDTO> contents;

    private Integer assessmentCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UnitStandardResponseDTO(UnitStandard unitStandard) {
        this.unitStandardId = unitStandard.getUnitStandardId();
        this.title = unitStandard.getTitle();
        this.description = unitStandard.getDescription();
        this.purpose = unitStandard.getPurpose();
        this.learningAssumed = unitStandard.getLearningAssumed();
        this.credits = unitStandard.getCredits();
        this.notionalHours = unitStandard.getNotionalHours();
        this.nqfLevel = unitStandard.getNqfLevel();
        this.type = unitStandard.getType();
        this.status = unitStandard.getStatus();
        this.moderationBody = unitStandard.getModerationBody();
        this.rangeStatement = unitStandard.getRangeStatement();
        this.specificOutcomes = unitStandard.getSpecificOutcomes();
        this.assessmentCriteria = unitStandard.getAssessmentCriteria();
        this.criticalCrossFieldOutcomes = unitStandard.getCriticalCrossFieldOutcomes();

        if (unitStandard.getProgram() != null) {
            this.programId = unitStandard.getProgram().getId();
            this.programName = unitStandard.getProgram().getName();
        }

        if (unitStandard.getContents() != null && !unitStandard.getContents().isEmpty()) {
            this.contentCount = unitStandard.getContents().size();
            this.contents = unitStandard.getContents().stream()
                    .map(ContentResponseDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.contentCount = 0;
        }

        if (unitStandard.getAssessments() != null) {
            this.assessmentCount = unitStandard.getAssessments().size();
        } else {
            this.assessmentCount = 0;
        }

        this.createdAt = unitStandard.getCreatedAt();
        this.updatedAt = unitStandard.getUpdatedAt();
    }

    public static List<UnitStandardResponseDTO> fromEntities(List<UnitStandard> entities) {
        return entities.stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }
}