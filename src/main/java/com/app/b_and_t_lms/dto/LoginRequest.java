package com.app.b_and_t_lms.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "email address is required.")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
    private boolean rememberMe;

    public LoginRequest(@NotBlank(message = "email address is required.") String email,
            @NotBlank(message = "password is required") String password, Boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRememberMe() {
        return false;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

}
