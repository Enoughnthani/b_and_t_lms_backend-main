package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.Role.RoleName;

import lombok.Data;

@Data
public class RoleAssignRequest {

    List<RoleName> roles;
    Long userId;

    public RoleAssignRequest() {
    }

    public RoleAssignRequest(List<RoleName> roles, Long userId) {
        this.roles = roles;
        this.userId = userId;
    }

   
}
