package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.ProgramStaff.StaffRole;

import lombok.Data;

@Data
public class AssignRequest {

    private Long programId;
    private Long userId;
    private List<Long> userIds;
    private List<Long> ids; 
    private StaffRole role;

    public AssignRequest() {
    }

    public AssignRequest(Long programId, Long userId, List<Long> userIds, List<Long> ids, StaffRole role) {
        this.programId = programId;
        this.userId = userId;
        this.userIds = userIds;
        this.ids = ids;
        this.role = role;
    }

}