package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.Role.RoleName;
import com.app.b_and_t_lms.models.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
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

    private String password;

    @NotNull(message = "Role is required")
    private List<RoleName> role;

    @NotNull(message = "ID number is required")
    private String idNo;

    private Status status;

    public UserDTO() {
    }

    public UserDTO(Long id, @NotBlank(message = "First name is required") String firstname,
            @NotBlank(message = "Last name is required") String lastname,
            @NotBlank(message = "Contact number is required") String contactNumber,
            @Email(message = "Invalid email") @NotBlank(message = "Email address is required") String email,
            String password, @NotNull(message = "Role is required") List<RoleName> role,
            @NotNull(message = "ID number is required") String idNo, Status status) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contactNumber = contactNumber;
        this.email = email;
        this.password = password;
        this.role = role;
        this.idNo = idNo;
        this.status = status;
    }

}
