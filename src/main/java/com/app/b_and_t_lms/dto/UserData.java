package com.app.b_and_t_lms.dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.app.b_and_t_lms.models.Enrollment;
import com.app.b_and_t_lms.models.Role.RoleName;
import com.app.b_and_t_lms.models.Status;
import com.app.b_and_t_lms.models.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Boolean isEnrolled;
    private LocalDateTime asiggnedDate;
    private LocalDateTime enrolmentDate;
    private List<AssignedProgramDTO> assignedPrograms;
    private UserEnrollment enrolledProgram;
    private Boolean enrolled;

    public UserData() {
    }

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
        isEnrolled = user.getEnrollment() == null ? null : Boolean.TRUE;
        this.enrolledProgram = getEnrollmentProgram(user);
        this.assignedPrograms = getAssignedPrograms(user);
        this.enrolled = isEnrolled(user.getEnrollment());

    }

    private Boolean isEnrolled(Enrollment enrolledProgram) {
        return enrolledProgram == null ? null : Boolean.TRUE;
    }

    private UserEnrollment getEnrollmentProgram(User user) {
        if (user.getEnrollment() == null) {
            return null;
        } else {
            return new UserEnrollment(user.getEnrollment());
        }
    }

    private List<AssignedProgramDTO> getAssignedPrograms(User user) {
        if (user.getProgramStaffs().isEmpty()) {
            return null;
        }

        return user.getProgramStaffs().stream().map(AssignedProgramDTO::new).toList();
    }

}
