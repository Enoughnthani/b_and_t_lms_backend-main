package com.app.b_and_t_lms.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "assessments")
public class Assessment {

    public enum AssessmentType {
        LEARNER_WORKBOOK, SUMMATIVE
    }

    public enum AssessmentStatus {
        DRAFT, PUBLISHED, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDate dueDate;

    private Integer totalMarks;

    @Enumerated(EnumType.STRING)
    private AssessmentType type;

    @Enumerated(EnumType.STRING)
    private AssessmentStatus status;

    private String fileUrl;

    private String fileName;

    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "unit_standard_id")
    private UnitStandard unitStandard;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = AssessmentStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}