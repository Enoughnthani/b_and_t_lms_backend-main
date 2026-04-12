package com.app.b_and_t_lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.AssignRequest;
import com.app.b_and_t_lms.services.ProgramStaffService;

@RestController
@RequestMapping("/api/program-staff")
@PreAuthorize("hasAnyRole('ADMIN','PROGRAM_MANAGER')")
public class ProgramStaffController {

    @Autowired
    private ProgramStaffService programStaffService;

    @PostMapping("/assign")
    public ApiResponse<?> singleAssign(@RequestBody AssignRequest dto) {
        return programStaffService.singleAssign(dto);
    }

    @PostMapping("/bulk-assign")
    public ApiResponse<?> bulkAssign(@RequestBody AssignRequest dto) {
        return programStaffService.bulkAssign(dto);
    }

    @DeleteMapping("/remove")
    public ApiResponse<?> singleRemove(@RequestBody AssignRequest dto) {
        return programStaffService.singleRemove(dto);
    }

    @DeleteMapping("/bulk-remove")
    public ApiResponse<?> bulkRemove(@RequestBody AssignRequest dto) {
        return programStaffService.bulkRemove(dto);
    }
}