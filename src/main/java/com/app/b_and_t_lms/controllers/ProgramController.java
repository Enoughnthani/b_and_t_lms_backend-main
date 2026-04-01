package com.app.b_and_t_lms.controllers;

import com.app.b_and_t_lms.services.ProgramService;
import org.springframework.web.bind.annotation.*;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.ProgramDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/programs")
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
}