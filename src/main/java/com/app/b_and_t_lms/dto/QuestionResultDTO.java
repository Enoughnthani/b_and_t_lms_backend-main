package com.app.b_and_t_lms.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class QuestionResultDTO {
    private Long questionId;
    private String questionText;
    private String questionType;
    private Integer maxMarks;
    private Integer marksObtained;
    private String userAnswer;          // for single-answer types
    private List<String> userAnswers;   // for fill-in-blanks
    private Map<String, String> userMatchingAnswers; // for matching
    private String correctAnswer;       // formatted for display
}