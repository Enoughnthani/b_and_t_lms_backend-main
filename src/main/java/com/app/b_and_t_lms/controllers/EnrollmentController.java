package com.app.b_and_t_lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.EnrollmentRequestDTO;
import com.app.b_and_t_lms.services.EnrollmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enrollments")
@PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ApiResponse<?> enroll(@Valid @RequestBody EnrollmentRequestDTO dto) {
        return enrollmentService.enroll(dto);
    }

    @DeleteMapping("/remove")
    public ApiResponse<?> remove(@Valid @RequestBody EnrollmentRequestDTO dto) {
        return enrollmentService.remove(dto);
    }

    @PostMapping("/bulk/enroll")
    public ApiResponse<?> bulkEnroll(@Valid @RequestBody EnrollmentRequestDTO dto) {
        return enrollmentService.bulkEnroll(dto);
    }

    @DeleteMapping("/bulk/remove")
    public ApiResponse<?> bulkRemove(@Valid @RequestBody EnrollmentRequestDTO dto) {
        return enrollmentService.bulkRemove(dto);
    }
}