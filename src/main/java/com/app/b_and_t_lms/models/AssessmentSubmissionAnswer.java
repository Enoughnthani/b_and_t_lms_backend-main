package com.app.b_and_t_lms.models;

import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class AssessmentSubmissionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AssessmentSubmission submission;

    private Long questionId;
    
    private String answer;             
    private String userAnswerText;     
    
    @ElementCollection
    @CollectionTable(name = "submission_fill_answers")
    private List<String> answers;     
    
    @ElementCollection
    @CollectionTable(name = "submission_matching_answers")
    private Map<String, String> matchingAnswers; 
    
    private Integer marksObtained;     
    
    @Column(length = 2000)
    private String correctAnswerSnapshot; 
}