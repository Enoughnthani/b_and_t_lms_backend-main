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
        FUNDAMENTAL, CORE, ELECTIVE
    }

    @Id
    private Long unitStandardId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 1000)
    private String description;

    private Integer credits;

    @Column(name = "nqf_level", length = 20)
    private String nqfLevel;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UnitStandardType type;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToMany(mappedBy = "unitStandard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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

    public int getTotalCredits() {
        return credits != null ? credits : 0;
    }

    public int getTotalContentCount() {
        return contents != null ? contents.size() : 0;
    }
}