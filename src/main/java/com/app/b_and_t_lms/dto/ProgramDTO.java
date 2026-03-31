package com.app.b_and_t_lms.dto;

import java.time.LocalDate;

import com.app.b_and_t_lms.models.Status;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgramDTO {

    private Long id;

    @NotNull(message = "Program name is required")
    private String name;

    @NotNull(message = "Program type is required")
    private String programType;

    @NotNull(message = "Program capacity is required")
    private long capacity;

     @NotNull(message = "Program description is required")
    private String description;

    @NotNull(message = "Program status is required")
    private Status status;

    @NotNull(message = "Program start date is required")
    private LocalDate startDate;

    @NotNull(message = "Program end date is required")
    private LocalDate endDate;

    public ProgramDTO(Long id, @NotNull(message = "Program name is required") String name,
            @NotNull(message = "Program type is required") String programType,
            @NotNull(message = "Program capacity is required") long capacity,
            @NotNull(message = "Program status is required") Status status,
            @NotNull(message = "Program start date is required") LocalDate startDate,
            @NotNull(message = "Program end date is required") LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.programType = programType;
        this.capacity = capacity;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    

}
