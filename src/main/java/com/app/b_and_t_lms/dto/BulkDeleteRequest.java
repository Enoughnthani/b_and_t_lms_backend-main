package com.app.b_and_t_lms.dto;

import java.util.List;

public class BulkDeleteRequest {

    private List<Long> userIds;

    public BulkDeleteRequest() {
    }

    public BulkDeleteRequest(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}