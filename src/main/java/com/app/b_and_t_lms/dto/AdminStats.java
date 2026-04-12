package com.app.b_and_t_lms.dto;

import lombok.Data;

@Data
public class AdminStats {

    private Long totalUsers;
    private Long totalPrograms;
    private Long activeLearnershipsCount;
    private Long activeInternshipsCount;
    private Long activeShourCourseCount;

    public AdminStats() {
    }

    public AdminStats(Long totalUsers, Long totalPrograms, Long activeLearnershipsCount, Long activeInternshipsCount,
            Long activeShourCourseCount) {
        this.totalUsers = totalUsers;
        this.totalPrograms = totalPrograms;
        this.activeLearnershipsCount = activeLearnershipsCount;
        this.activeInternshipsCount = activeInternshipsCount;
        this.activeShourCourseCount = activeShourCourseCount;
    }

}
