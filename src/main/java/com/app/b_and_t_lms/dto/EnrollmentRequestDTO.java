package com.app.b_and_t_lms.dto;

import java.util.List;

import lombok.Data;

@Data
public class EnrollmentRequestDTO {

    private Long userId;
    private Long programId;
    private List<Long> userIds;

    public EnrollmentRequestDTO() {
    }

    public EnrollmentRequestDTO(Long userId, Long programId, List<Long> userIds) {
        this.userId = userId;
        this.programId = programId;
        this.userIds = userIds;
    }

}
