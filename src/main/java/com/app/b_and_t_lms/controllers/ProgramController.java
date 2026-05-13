package com.app.b_and_t_lms.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.ProgramDTO;
import com.app.b_and_t_lms.services.ProgramService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/programs")
@Valid
@PreAuthorize("hasAnyRole('ADMIN', 'PROGRAM_MANAGER')")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @PostMapping
    public ApiResponse<?> addProgram(@RequestBody @Valid ProgramDTO programDTO) {
        return programService.addProgram(programDTO);
    }

    @GetMapping
    public ApiResponse<?> getAllPrograms() {
        return programService.getAllPrograms();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('FACILITATOR')")
    public ApiResponse<?> getProgramById(@PathVariable Long id) {
        return programService.getProgramById(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateProgram(
            @PathVariable Long id,
            @RequestBody @Valid ProgramDTO programDTO) {
        return programService.updateProgram(id, programDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteProgram(@PathVariable Long id) {
        return programService.deleteProgram(id);
    }

    @GetMapping("/{programId}/candidates")
    public ApiResponse<?> getCandidates(@PathVariable Long programId) {
        return programService.getUsersByProgramCategory(programId);
    }

    @GetMapping("/{programId}/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROGRAM_MANAGER', 'FACILITATOR')")
    public ApiResponse<?> getEnrolledLearners(@PathVariable Long programId) {
        return programService.getEnrolledLearners(programId);
    }

    @GetMapping("/{programId}/stats")
    @PreAuthorize("hasAnyRole('FACILITATOR')")
    public ApiResponse<?> facilitatorProgramStats(@PathVariable Long programId) {
        return programService.getFacilitatorProgramStats(programId);
    }

}