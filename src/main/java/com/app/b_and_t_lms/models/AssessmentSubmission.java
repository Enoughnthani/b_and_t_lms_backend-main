package com.app.b_and_t_lms.models;

import java.time.LocalDateTime;
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
@Table(name = "assessment_submissions")
public class AssessmentSubmission {

    public enum SubmissionStatus {
        SUBMITTED,
        GRADED,
        RE_SUBMITTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment; 

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  

    private String fileUrl;

    private String fileName;

    private Long fileSize;

    private Integer obtainedMarks;

    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SubmissionStatus status;

    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "submission",orphanRemoval = true)
    private List<AssessmentSubmissionAnswer> assessmentSubmissionAnswer;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        status = SubmissionStatus.SUBMITTED;
    }

    @PreUpdate
    protected void onUpdate() {
        submittedAt = LocalDateTime.now();
        status = SubmissionStatus.RE_SUBMITTED;
    }

}