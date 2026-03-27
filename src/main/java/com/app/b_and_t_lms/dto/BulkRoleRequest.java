package com.app.b_and_t_lms.dto;

import java.util.List;

import lombok.Data;

@Data
public class BulkRoleRequest {
    private List<Long> userIds;
    private String role;
}