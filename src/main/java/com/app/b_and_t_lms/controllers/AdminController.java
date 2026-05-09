package com.app.b_and_t_lms.controllers;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.services.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {



    private final AdminService adminService;

    AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("stats")
    public ApiResponse<?> getStats() {
       return adminService.getStats();
    }
    
    @GetMapping("activities")
    public ApiResponse<?> getActivities() {
       return adminService.getActivities();
    }

}
