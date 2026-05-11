package com.app.b_and_t_lms.dto;

import lombok.Data;

@Data
public class AssessmentRequestDTO {
    private String title;
    private String description;
    private String dueDate;
    private Integer totalMarks;
    private String type;
    private Long unitStandardId;
}