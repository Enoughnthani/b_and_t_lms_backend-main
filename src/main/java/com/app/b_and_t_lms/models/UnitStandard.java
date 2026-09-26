package com.app.b_and_t_lms.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitStandard {

    public enum UnitStandardType {
        KNOWLEDGE,
        PRACTICAL,
        WORK_EXPERIENCE
    }

    public enum UnitStandardStatus {
        ACTIVE,
        PHASED_OUT,
        PENDING
    }

    @Id
    private Long unitStandardId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "learning_assumed", columnDefinition = "TEXT")
    private String learningAssumed;

    private Integer credits;

    @Column(name = "notional_hours")
    private Integer notionalHours;

    @Column(name = "nqf_level", length = 20)
    private String nqfLevel;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UnitStandardType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UnitStandardStatus status = UnitStandardStatus.ACTIVE;

    @Column(name = "moderation_body", length = 255)
    private String moderationBody;

    @Column(name = "range_statement", columnDefinition = "TEXT")
    private String rangeStatement;

    @ElementCollection
    @CollectionTable(
        name = "unit_standard_specific_outcomes",
        joinColumns = @JoinColumn(name = "unit_standard_id")
    )
    @Column(name = "outcome", columnDefinition = "TEXT")
    private List<String> specificOutcomes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "unit_standard_assessment_criteria",
        joinColumns = @JoinColumn(name = "unit_standard_id")
    )
    @Column(name = "criterion", columnDefinition = "TEXT")
    private List<String> assessmentCriteria = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
        name = "unit_standard_cross_field_outcomes",
        joinColumns = @JoinColumn(name = "unit_standard_id")
    )
    @Column(name = "outcome", columnDefinition = "TEXT")
    private List<String> criticalCrossFieldOutcomes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToMany(mappedBy = "unitStandard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unitStandard", orphanRemoval = true)
    List<Assessment> assessments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = UnitStandardStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public int getTotalCredits() {
        return credits != null ? credits : 0;
    }

    public int getTotalContentCount() {
        return contents != null ? contents.size() : 0;
    }
}