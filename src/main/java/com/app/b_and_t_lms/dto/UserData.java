package com.app.b_and_t_lms.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.app.b_and_t_lms.models.Role.RoleName;
import com.app.b_and_t_lms.models.Status;
import com.app.b_and_t_lms.models.User;

public class UserData {

    private String firstname;
    private String lastname;
    private String email;
    private Timestamp createdAt;
    private String idNo;
    private LocalDate dob;
    private String gender;
    private String contactNumber;
    private LocalDateTime lastLogin;
    private LocalDateTime prevLogin;
    private List<RoleName> role;
    private Long id;
    private Status status;
    private boolean isActive;

    public UserData(User user) {
        id = user.getId();
        firstname = user.getFirstname();
        lastname = user.getLastname();
        email = user.getEmail();
        createdAt = user.getCreatedAt();
        idNo = user.getIdNumber();
        dob = user.getDob();
        gender = user.getGender();
        contactNumber = user.getContactNumber();
        lastLogin = user.getLastLogin();
        prevLogin = user.getPrevLogin();
        role = user.getRoles().stream().map(role -> role.getName()).toList();
        status = user.getStatus();
        isActive = user.isAccountNonLocked();
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getIdNo() {
        return idNo;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public List<RoleName> getRole() {
        return role;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getPrevLogin() {
        return prevLogin;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isActive() {
        return isActive;
    }

}
