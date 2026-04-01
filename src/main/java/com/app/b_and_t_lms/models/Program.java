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

@Entity
public class Program {

    public enum ProgramStatus {
        NOTSTARTED, INPROGRESS, COMPLETED
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

    public Program(Long id, String name, List<Enrollment> enrollments, List<ProgramStaff> programStaffs, String type,
            String location, String description, ProgramCategory category, long capacity, ProgramStatus status,
            LocalDate startDate, LocalDate endDate, String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.enrollments = enrollments;
        this.programStaffs = programStaffs;
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

    public Program(List<Program> programs) {
        //TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public List<ProgramStaff> getProgramStaffs() {
        return programStaffs;
    }

    public void setProgramStaffs(List<ProgramStaff> programStaffs) {
        this.programStaffs = programStaffs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProgramCategory getCategory() {
        return category;
    }

    public void setCategory(ProgramCategory category) {
        this.category = category;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public ProgramStatus getStatus() {
        return status;
    }

    public void setStatus(ProgramStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
