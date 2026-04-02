package com.app.b_and_t_lms.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/enrollments")
@Valid
@PreAuthorize("hasRole(RoleName.ADMIN) or hasRole(RoleName.PROGRAM_MANAGER) ")
public class EnrollmentController {


    @PostMapping
    public String enroll(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    
}
