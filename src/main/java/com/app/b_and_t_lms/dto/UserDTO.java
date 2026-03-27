package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.Role.RoleName;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDTO {

    private Long id;

    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email address is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private List<RoleName> role;

    @NotNull(message = "ID number is required")
    private String idNo;

    public UserDTO() {
    }

    public UserDTO(Long id, @NotBlank(message = "First name is required") String firstname,
            @NotBlank(message = "Last name is required") String lastname,
            @NotBlank(message = "Contact number is required") String contactNumber,
            @Email(message = "Invalid email") @NotBlank(message = "Email address is required") String email,
            @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password,
            @NotNull(message = "Role is required") List<RoleName> role,
            @NotNull(message = "ID number is required") String idNo) {
        this.id = id == null ? -1 : id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contactNumber = contactNumber;
        this.email = email;
        this.password = password;
        this.role = role;
        this.idNo = idNo;
        System.out.println("Using Con NSsksjsjsn +++ +++======================= ");
    }

    public Long getId() {
        return id;
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

    public String getPassword() {
        return password;
    }

    public List<RoleName> getRole() {
        return role;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getIdNo() {
        return idNo;
    }

}
