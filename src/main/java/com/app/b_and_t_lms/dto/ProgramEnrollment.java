package com.app.b_and_t_lms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.app.b_and_t_lms.models.Enrollment;
import com.app.b_and_t_lms.models.Role.RoleName;
import com.app.b_and_t_lms.models.Status;

import lombok.Data;

@Data
public class ProgramEnrollment {

    private String firstname;
    private String lastname;
    private String email;
    private String idNo;
    private LocalDate dob;
    private String gender;
    private String contactNumber;
    private List<RoleName> role;
    private Long id;
    private Status status;
    private boolean isActive;
    private boolean enrolled;
    private LocalDateTime enrollmentDate;
    private List<ReportDTO> reports;

    public ProgramEnrollment() {
    }

    public ProgramEnrollment(Enrollment enrollment) {
        id = enrollment.getUser().getId();
        firstname = enrollment.getUser().getFirstname();
        lastname = enrollment.getUser().getLastname();
        email = enrollment.getUser().getEmail();
        idNo = enrollment.getUser().getIdNumber();
        dob = enrollment.getUser().getDob();
        gender = enrollment.getUser().getGender();
        contactNumber = enrollment.getUser().getContactNumber();
        role = enrollment.getUser().getRoles().stream().map(role -> role.getName()).toList();
        status = enrollment.getUser().getStatus();
        isActive = enrollment.getUser().isAccountNonLocked();
        enrollmentDate = enrollment.getEnrollmentDate();
        this.enrolled = true;

        if (enrollment.getUser().getReports() != null) {
            this.reports = enrollment.getUser()
                    .getReports()
                    .stream()
                    .map(ReportDTO::new)
                    .toList();
        } 
    }

}
