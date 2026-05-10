package com.app.b_and_t_lms.dto;

import java.time.LocalDateTime;


import com.app.b_and_t_lms.models.Enrollment;
import com.app.b_and_t_lms.models.Status;
import com.app.b_and_t_lms.models.User;
import lombok.Data;

@Data
public class LearnerEnrollmentData {

    private String firstname;
    private String lastname;
    private String email;
    private String gender;
    private Status status;
    private String contactNumber;
    private LocalDateTime enrollmentDate;


    public LearnerEnrollmentData(Enrollment enrollment) {
        User user = enrollment.getUser();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.status = user.getStatus();
        this.contactNumber = user.getContactNumber();
        this.enrollmentDate = enrollment.getEnrollmentDate();
    }


}
