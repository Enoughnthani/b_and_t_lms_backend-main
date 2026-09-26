package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.UnitStandard.UnitStandardStatus;
import com.app.b_and_t_lms.models.UnitStandard.UnitStandardType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitStandardRequestDTO {
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
    private String qualificationType;
}