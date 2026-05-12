package com.app.b_and_t_lms.dto;

import com.app.b_and_t_lms.models.UnitStandard;
import com.app.b_and_t_lms.models.UnitStandard.UnitStandardType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitStandardResponseDTO {
    private Long unitStandardId;
    private String title;
    private String description;
    private Integer credits;
    private String nqfLevel;
    private UnitStandardType type;
    private Long programId;
    private String programName;
    private Integer contentCount;
    private Integer totalCredits;
    private List<ContentResponseDTO> contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer assessmentCount;

    public UnitStandardResponseDTO(UnitStandard unitStandard) {
        this.unitStandardId = unitStandard.getUnitStandardId();
        this.title = unitStandard.getTitle();
        this.description = unitStandard.getDescription();
        this.credits = unitStandard.getCredits();
        this.nqfLevel = unitStandard.getNqfLevel();
        this.type = unitStandard.getType();

        if (unitStandard.getProgram() != null) {
            this.programId = unitStandard.getProgram().getId();
            this.programName = unitStandard.getProgram().getName();
        }

        this.contentCount = unitStandard.getContents() != null ? unitStandard.getContents().size() : 0;

        if (unitStandard.getContents() != null && !unitStandard.getContents().isEmpty()) {
            this.contents = unitStandard.getContents().stream()
                    .map(ContentResponseDTO::new)
                    .collect(Collectors.toList());
        }

        this.createdAt = unitStandard.getCreatedAt();
        this.updatedAt = unitStandard.getUpdatedAt();
        this.assessmentCount = unitStandard.getAssessments().size();
    }

    public static List<UnitStandardResponseDTO> fromEntities(List<UnitStandard> entities) {
        return entities.stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }
}