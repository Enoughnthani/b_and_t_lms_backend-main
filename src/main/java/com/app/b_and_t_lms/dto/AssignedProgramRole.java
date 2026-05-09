package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.ProgramStaff;

import lombok.Data;

@Data
public class AssignedProgramRole {

    private Long programId;
    private List<String> roles;

    public AssignedProgramRole() {
    }

    public AssignedProgramRole(ProgramStaff ps) {
        programId = ps.getProgram().getId();
        roles = ps.getAssignedRoles().stream().map(r -> r.getRole().name()).toList();
    }

}
