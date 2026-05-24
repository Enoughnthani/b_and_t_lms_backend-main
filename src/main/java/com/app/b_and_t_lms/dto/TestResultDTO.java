package com.app.b_and_t_lms.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TestResultDTO {
    private Long assessmentId;
    private String assessmentTitle;
    private Integer totalMarks;
    private Integer obtainedMarks;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private List<QuestionResultDTO> questionResults;
}