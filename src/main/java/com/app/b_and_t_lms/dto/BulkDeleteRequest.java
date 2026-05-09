package com.app.b_and_t_lms.dto;

import java.util.List;

import lombok.Data;

@Data
public class BulkDeleteRequest {

    private List<Long> userIds;

    public BulkDeleteRequest() {
    }

    public BulkDeleteRequest(List<Long> userIds) {
        this.userIds = userIds;
    }
}