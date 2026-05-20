package com.app.b_and_t_lms.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AssessmentRequestDTO {
    private String title;
    private String description;
    private String dueDate;
    private String startDate;
    private Integer totalMarks;
    private String type;
    private Long unitStandardId;
    private Integer durationMinutes;
    private Integer passingMarks;
    private MultipartFile file;
    private String questions;
    
    @Data
    public static class QuestionDTO {
        private String type;
        private String text;
        private Integer marks;
        private String explanation;
        private String correctAnswer;
        private String sampleAnswer;
        private List<OptionDTO> options;
        private List<MatchingPairDTO> matchingPairs;
        private List<String> blanks;
        private Integer displayOrder;
    }
    
    @Data
    public static class OptionDTO {
        private String text;
        private Integer displayOrder;
    }

    
    @Data
    public static class MatchingPairDTO {
        private String leftItem;
        private String rightItem;
        private Integer displayOrder;
    }
}