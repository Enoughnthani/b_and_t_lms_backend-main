package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.Role;

import lombok.Data;

@Data
public class LoginResponse {

    private String email;
    private List<Role> role;

    public LoginResponse(String email, List<Role> role) {
        this.email = email;
        this.role = role;
    }

}
