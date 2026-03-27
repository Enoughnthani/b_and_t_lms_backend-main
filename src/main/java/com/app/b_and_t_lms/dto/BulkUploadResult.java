package com.app.b_and_t_lms.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BulkUploadResult {

    private int successCount = 0;
    private int errorCount = 0;
    private List<String> errors = new ArrayList<>();
    private List<UserData> createdUsers = new ArrayList<>();
    private String summary;

    // Helper method to increment success count
    public void incrementSuccess() {
        this.successCount++;
    }

    // Helper method to add an error
    public void addError(String error) {
        this.errors.add(error);
        this.errorCount = this.errors.size();
    }

    // Helper method to add a created user
    public void addCreatedUser(UserData user) {
        this.createdUsers.add(user);
    }

    // Helper method to check if upload had any errors
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    // Helper method to get summary message
    public String getSummary() {
        return String.format("Processed: %d total, Success: %d, Errors: %d",
                successCount + errorCount, successCount, errorCount);
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}