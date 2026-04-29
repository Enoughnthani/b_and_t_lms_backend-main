package com.app.b_and_t_lms.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Program {

    public enum ProgramStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }

    public enum ProgramCategory {
        LEARNERSHIP, INTERNSHIP, SHORT_COURSE
    }
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;



    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramStaff> programStaffs = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discussion> discussions = new ArrayList<>();

    private String type;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private ProgramCategory category;

    private long capacity;

    private ProgramStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private String imageUrl;

    private LocalDateTime createdAt;

    public Program() {
    }

    public Program(Long id, String name, List<Enrollment> enrollments, List<ProgramStaff> programStaffs,
            List<Discussion> discussions, String type, String location, String description, ProgramCategory category,
            long capacity, ProgramStatus status, LocalDate startDate, LocalDate endDate, String imageUrl,
            LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.enrollments = enrollments;
        this.programStaffs = programStaffs;
        this.discussions = discussions;
        this.type = type;
        this.location = location;
        this.description = description;
        this.category = category;
        this.capacity = capacity;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }
    
}
