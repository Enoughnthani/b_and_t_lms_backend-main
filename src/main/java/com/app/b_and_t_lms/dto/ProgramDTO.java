package com.app.b_and_t_lms.dto;

import java.time.LocalDate;

import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.models.Program.ProgramCategory;
import com.app.b_and_t_lms.models.Program.ProgramStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgramDTO {

    private Long id;

    @NotNull(message = "Program name is required")
    private String name;

    @NotNull(message = "Program type is required")
    private String type;

    @NotNull(message = "Program capacity is required")
    private long capacity;

    @NotNull(message = "Program description is required")
    private String description;

    @NotNull(message = "Program location is required")
    private String location;

    @NotNull(message = "Program location is required")
    private ProgramCategory category;

    @NotNull(message = "Program status is required")
    private ProgramStatus status;

    @NotNull(message = "Program start date is required")
    private LocalDate startDate;

    @NotNull(message = "Program end date is required")
    private LocalDate endDate;

    private long enrolledCount;

    private String imageBase64;

    public ProgramDTO() {
    }

    public ProgramDTO(Long id, @NotNull(message = "Program name is required") String name,
            @NotNull(message = "Program type is required") String type,
            @NotNull(message = "Program capacity is required") long capacity,
            @NotNull(message = "Program description is required") String description,
            @NotNull(message = "Program location is required") String location,
            @NotNull(message = "Program location is required") ProgramCategory category,
            @NotNull(message = "Program status is required") ProgramStatus status,
            @NotNull(message = "Program start date is required") LocalDate startDate,
            @NotNull(message = "Program end date is required") LocalDate endDate, long enrolledCount,
            String imageBase64) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.description = description;
        this.location = location;
        this.category = category;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enrolledCount = enrolledCount;
        this.imageBase64 = imageBase64;
    }

    public ProgramDTO(Program program) {
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
        this.imageBase64 = program.getImageUrl();
    }

}
