package com.app.b_and_t_lms.dto;

import com.app.b_and_t_lms.models.AssessmentSubmission;
import com.app.b_and_t_lms.models.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssessmentSubmissionDTO {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Integer obtainedMarks;
    private String feedback;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;

    public AssessmentSubmissionDTO(AssessmentSubmission submission) {
        if (submission == null) return;
        
        this.id = submission.getId();
        this.fileUrl = submission.getFileUrl();
        this.fileName = submission.getFileName();
        this.fileSize = submission.getFileSize();
        this.obtainedMarks = submission.getObtainedMarks();
        this.feedback = submission.getFeedback();
        this.submittedAt = submission.getSubmittedAt();
        this.gradedAt = submission.getGradedAt();
        
        if (submission.getStatus() != null) {
            this.status = submission.getStatus().name();
        }
        
        User user = submission.getUser();
        if (user != null) {
            this.firstname = user.getFirstname();
            this.lastname = user.getLastname();
            this.email = user.getEmail();
        }
    }
}