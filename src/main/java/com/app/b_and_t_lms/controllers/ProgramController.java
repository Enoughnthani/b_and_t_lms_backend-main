package com.app.b_and_t_lms.controllers;

import com.app.b_and_t_lms.services.ProgramService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.ProgramDTO;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/programs")
@Valid
public class ProgramController {
    


    private final ProgramService programService;

    ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @PostMapping
    public ApiResponse<?> addprogram(@RequestBody  ProgramDTO programDTO) {
       return programService.addProgram(programDTO);
    }
    
}
