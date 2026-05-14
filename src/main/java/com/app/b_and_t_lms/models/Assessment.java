package com.app.b_and_t_lms.models;

import java.time.LocalDate;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "assessments")
public class Assessment {

    public enum AssessmentType {
        LEARNER_WORKBOOK, SUMMATIVE
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

    private String fileUrl;

    private String fileName;

    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "unit_standard_id")
    private UnitStandard unitStandard;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "assessment", orphanRemoval = true)
    AssessmentSubmission assessmentSubmission;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}