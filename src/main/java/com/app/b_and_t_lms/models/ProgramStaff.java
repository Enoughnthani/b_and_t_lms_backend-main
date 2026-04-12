package com.app.b_and_t_lms.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class ProgramStaff {

    public enum StaffRole {
        FACILITATOR,
        ASSESSOR,
        MODERATOR,
        MENTOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties("programStaffs")
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "programStaff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramStaffRole> assignedRoles = new ArrayList<>();

    private LocalDateTime assignedDate;

    public ProgramStaff() {
    }

    public ProgramStaff(Long id, Program program, User user, List<ProgramStaffRole> assignedRoles,
            LocalDateTime assignedDate) {
        this.id = id;
        this.program = program;
        this.user = user;
        this.assignedRoles = assignedRoles;
        this.assignedDate = assignedDate;
    }

    
}
