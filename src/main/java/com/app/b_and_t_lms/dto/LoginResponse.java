package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.Role;

public class LoginResponse {

    private String email;
    private List<Role> role;

    public LoginResponse(String email, List<Role> role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }

}
