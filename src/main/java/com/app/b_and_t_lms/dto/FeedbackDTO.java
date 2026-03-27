package com.app.b_and_t_lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FeedbackDTO {

    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Surname is required")
    private String surname;
    @NotBlank(message = "Contact number is required")
    @Size(max = 10, message = "Invalid contact number")
    private String contact_no;
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;
    @NotNull(message = "Message is required")
    private String message;
    private String recaptchaToken;

    public FeedbackDTO(@NotNull(message = "Name is required") String name,
            @NotNull(message = "Surname is required") String surname,
            @NotBlank(message = "Contact number is required") @Size(max = 10, message = "Invalid contact number") String contact_no,
            @NotNull(message = "Email is required") @Email(message = "Invalid email") String email,
            @NotNull(message = "Message is required") String message, String recaptchaToken) {
        this.name = name;
        this.surname = surname;
        this.contact_no = contact_no;
        this.email = email;
        this.message = message;
        this.recaptchaToken = recaptchaToken;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getContact_no() {
        return contact_no;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecaptchaToken() {
        return recaptchaToken;
    }

    public void setRecaptchaToken(String recaptchaToken) {
        this.recaptchaToken = recaptchaToken;
    }

}
