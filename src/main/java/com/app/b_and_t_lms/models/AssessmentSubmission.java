package com.app.b_and_t_lms.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

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

    @OneToOne
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