package com.app.b_and_t_lms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.models.Program.ProgramCategory;
import com.app.b_and_t_lms.models.Program.ProgramStatus;
import com.app.b_and_t_lms.models.ProgramStaff;

import lombok.Data;

@Data
public class AssignedProgramDTO {

    private Long id;
    private String name;
    private String type;
    private Long capacity;
    private String description;
    private String location;
    private ProgramCategory category;
    private ProgramStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer enrolledCount;
    private String imageUrl;
    private List<String> assignedRoles;
    private LocalDateTime assignedDate;
    private List<ProgramEnrollment> enrollmentData;

    public AssignedProgramDTO() {
    }

    public AssignedProgramDTO(ProgramStaff programStaff) {
        Program program = programStaff.getProgram();

        this.id = program.getId();
        this.name = program.getName();
        this.type = program.getType();
        this.capacity = program.getCapacity();
        this.description = program.getDescription();
        this.location = program.getLocation();
        this.category = program.getCategory();
        this.status = program.getStatus();
        this.startDate = program.getStartDate();
        this.endDate = program.getEndDate();
        this.enrolledCount = program.getEnrollments().size();
        this.imageUrl = program.getImageUrl();

        this.enrollmentData = program.getEnrollments().stream()
                .map(ProgramEnrollment::new)
                .toList();

        this.assignedRoles = programStaff.getAssignedRoles().stream().map(r -> r.getRole().name()).toList();
        this.assignedDate = programStaff.getAssignedDate();
    }
}