package com.app.b_and_t_lms.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TestSubmissionDTO {
    private Long assessmentId;
    private List<AnswerDTO> answers;
    
    @Data
    public static class AnswerDTO {
        private Long questionId;
        private String answer;              
        private List<String> answers;   
        private Map<String, String> matchingAnswers; 
    }
}