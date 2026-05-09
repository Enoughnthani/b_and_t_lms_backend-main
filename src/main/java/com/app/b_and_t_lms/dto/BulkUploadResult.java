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


    public void incrementSuccess() {
        this.successCount++;
    }

    
    public void addError(String error) {
        this.errors.add(error);
        this.errorCount = this.errors.size();
    }

   
    public void addCreatedUser(UserData user) {
        this.createdUsers.add(user);
    }


    public boolean hasErrors() {
        return !errors.isEmpty();
    }

   
    public String createSummary() {
        return String.format("Processed: %d total, Success: %d, Errors: %d",
                successCount + errorCount, successCount, errorCount);
    }

  
}