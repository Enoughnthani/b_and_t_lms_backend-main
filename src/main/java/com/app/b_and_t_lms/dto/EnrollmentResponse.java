package com.app.b_and_t_lms.dto;

import java.time.LocalDate;
import java.util.List;

import com.app.b_and_t_lms.models.Enrollment;
import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.models.Program.ProgramCategory;
import com.app.b_and_t_lms.models.Program.ProgramStatus;

import lombok.Data;

@Data
public class EnrollmentResponse {

    private Long id;
    private String name;
    private String type;
    private String description;
    private String location;
    private ProgramCategory category;
    private ProgramStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<UnitStandardResponseDTO> unitStandards;
    private String imageBase64;

    public EnrollmentResponse() {
    }

    public EnrollmentResponse(Enrollment enrollment) {

        Program program = enrollment.getProgram();

        this.id = program.getId();
        this.name = program.getName();
        this.type = program.getType();
        this.description = program.getDescription();
        this.location = program.getLocation();
        this.category = program.getCategory();
        this.status = program.getStatus();
        this.startDate = program.getStartDate();
        this.endDate = program.getEndDate();
        this.imageBase64 = program.getImageUrl();
        this.unitStandards = program.getUnitStandards().stream().map(UnitStandardResponseDTO::new).toList();
    }

}
