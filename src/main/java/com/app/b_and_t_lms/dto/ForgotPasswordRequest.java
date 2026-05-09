package com.app.b_and_t_lms.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {

    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;

    public ForgotPasswordRequest(String email, String otp, String newPassword, String confirmPassword) {
        this.email = email;
        this.otp = otp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

}
