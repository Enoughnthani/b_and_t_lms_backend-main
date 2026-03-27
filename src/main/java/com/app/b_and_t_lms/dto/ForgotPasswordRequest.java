package com.app.b_and_t_lms.dto;

public class ForgotPasswordRequest {

    private String email;
    private String otp;
    private String new_password;
    private String confirm_password;

    public ForgotPasswordRequest(String email, String otp, String new_password, String confirm_password) {
        this.email = email;
        this.otp = otp;
        this.new_password = new_password;
        this.confirm_password = confirm_password;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public String getNewPassword() {
        return new_password;
    }

    public String getConfirmPassword() {
        return confirm_password;
    }

}
