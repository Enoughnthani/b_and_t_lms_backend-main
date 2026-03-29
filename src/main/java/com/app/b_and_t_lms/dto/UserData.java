package com.app.b_and_t_lms.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.app.b_and_t_lms.models.Role.RoleName;
import com.app.b_and_t_lms.models.Status;
import com.app.b_and_t_lms.models.User;

import lombok.Data;

@Data
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

}
