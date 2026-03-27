package com.app.b_and_t_lms.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BulkOperationResult {

    private int successCount = 0;
    private int errorCount = 0;

    private List<Long> successfulIds = new ArrayList<>();
    private List<Long> failedIds = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public void addSuccess(Long id) {
        successCount++;
        successfulIds.add(id);
    }

    public void addError(Long id, String message) {
        errorCount++;
        failedIds.add(id);
        errors.add("User ID " + id + ": " + message);
    }

    public boolean isSuccess() {
        return errorCount == 0;
    }

    public boolean isPartialSuccess() {
        return successCount > 0 && errorCount > 0;
    }

    public String getSummary() {
        return "Success: " + successCount + ", Errors: " + errorCount;
    }

    // getters
}