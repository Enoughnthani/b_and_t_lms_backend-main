package com.app.b_and_t_lms.dto;

import com.app.b_and_t_lms.models.Program;

import lombok.Data;

@Data
public class FacilitatorProgramStats {

    private Integer totalLearners;
    private Integer totalUnitStandards;
    private Integer totalAssignments;

    public FacilitatorProgramStats(Program program) {

        totalLearners = program.getEnrollments().size();
        totalUnitStandards = program.getUnitStandards().size();
        

    }

}
