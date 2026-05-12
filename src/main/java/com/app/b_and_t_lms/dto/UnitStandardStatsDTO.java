package com.app.b_and_t_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitStandardStatsDTO {
    
    private int total;
    private int fundamental;
    private int core;
    private int elective;
    private int totalCredits;
    private int completed;
    private int inProgress;
    private double averageCredits;
    private double averageHours;

}