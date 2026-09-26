package com.app.b_and_t_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitStandardStatsDTO {
    private Integer total;
    private Integer knowledge;
    private Integer practical;
    private Integer workExperience;
    private Integer active;
    private Integer phasedOut;
    private Integer totalCredits;
    private Integer notionalHours;
}