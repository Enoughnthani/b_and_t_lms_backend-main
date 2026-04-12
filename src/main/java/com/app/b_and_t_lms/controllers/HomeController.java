package com.app.b_and_t_lms.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.b_and_t_lms.repositories.UserOtpRepository;


@RestController
public class HomeController {

    private final UserOtpRepository userOtpRepository;

    HomeController(UserOtpRepository userOtpRepository) {
        this.userOtpRepository = userOtpRepository;
    }

    @GetMapping("/a")
    public String getMethodName() {
        return "Hello";
    }
    
    
}
