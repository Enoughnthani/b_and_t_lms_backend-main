package com.app.b_and_t_lms.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Report {

    public enum ReportStatus {
        DRAFT,
        SUBMITTED,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String reportMonth;

    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @ManyToOne
    private User submittedBy;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportFile> files;
}