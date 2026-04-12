package com.app.b_and_t_lms.models;



import com.app.b_and_t_lms.models.ProgramStaff.StaffRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ProgramStaffRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private ProgramStaff programStaff;

    @Enumerated(EnumType.STRING)
    private StaffRole role;

    public ProgramStaffRole(StaffRole role, ProgramStaff programStaff) {
        this.programStaff = programStaff;
        this.role = role;
    }

    public ProgramStaffRole() {
    }

    
}