package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.Status;

import lombok.Data;

@Data
public class BulkStatusRequest {
    
    private List<Long> userIds;
    private Status status;
    
    public BulkStatusRequest() {
    }

    public BulkStatusRequest(List<Long> userIds, Status status) {
        this.userIds = userIds;
        this.status = status;
    }
    
    
}
