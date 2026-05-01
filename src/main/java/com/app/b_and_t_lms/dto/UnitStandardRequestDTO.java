package com.app.b_and_t_lms.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.app.b_and_t_lms.models.UnitStandard.UnitStandardType;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitStandardRequestDTO {
    private Long unitStandardId;
    private String title;
    private String description;
    private Integer credits;
    private String nqfLevel;
    private UnitStandardType type;
    private Integer notionalHours;
    private String learningOutcomes;
    private String assessmentCriteria;
    private String purposeStatement;
    private Long programId;
}