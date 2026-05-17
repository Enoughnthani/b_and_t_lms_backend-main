package com.app.b_and_t_lms.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "assessments")
public class Assessment {

    public enum AssessmentType {
        LEARNER_WORKBOOK, SUMMATIVE, TEST
    }

    public enum AssessmentStatus {
        CREATED, WRITTEN, MARKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDateTime dueDate;

    private LocalDateTime startDate;

    private Integer totalMarks;
    
    private Integer passingMarks;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private AssessmentType type;

    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AssessmentStatus status;

    private String fileUrl;

    private String fileName;

    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "unit_standard_id")
    private UnitStandard unitStandard;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessment", orphanRemoval = true)
    private List<AssessmentSubmission> submissions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessment", orphanRemoval = true)
    private List<AssessmentQuestion> questions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
       
        if (status == null) {
            status = AssessmentStatus.CREATED;
        }
        
        if (submissions == null) {
            submissions = new ArrayList<>();
        }
        if (questions == null) {
            questions = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addSubmission(AssessmentSubmission submission) {
        submissions.add(submission);
        submission.setAssessment(this);
    }
    
    public void removeSubmission(AssessmentSubmission submission) {
        submissions.remove(submission);
        submission.setAssessment(null);
    }
    
    public void addQuestion(AssessmentQuestion question) {
        questions.add(question);
        question.setAssessment(this);
    }
    
    public void removeQuestion(AssessmentQuestion question) {
        questions.remove(question);
        question.setAssessment(null);
    }
}