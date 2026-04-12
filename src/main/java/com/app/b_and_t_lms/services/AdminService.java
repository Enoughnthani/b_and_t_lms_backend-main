package com.app.b_and_t_lms.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.b_and_t_lms.dto.AdminStats;
import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.models.Activity;
import com.app.b_and_t_lms.models.Program.ProgramCategory;
import com.app.b_and_t_lms.models.Program.ProgramStatus;
import com.app.b_and_t_lms.repositories.ActivityRepository;
import com.app.b_and_t_lms.repositories.ProgramRepository;
import com.app.b_and_t_lms.repositories.UserRepository;

@Service
public class AdminService {

    private final ActivityRepository activityRepository;
    private final ProgramRepository programRepository;
    private final UserRepository userRepository;

    AdminService(UserRepository userRepository, ProgramRepository programRepository,
            ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.programRepository = programRepository;
        this.activityRepository = activityRepository;
    }

    public ApiResponse<?> getStats() {

        try {
            Long totalusers = userRepository.count();
            Long totalPrograms = programRepository.count();

            Long totalActiveLearnerships = programRepository.countByCategoryAndStatus(ProgramCategory.LEARNERSHIP,
                    ProgramStatus.IN_PROGRESS);
            Long totalActiveInternships = programRepository.countByCategoryAndStatus(ProgramCategory.INTERNSHIP,
                    ProgramStatus.IN_PROGRESS);
            Long totalActiveShortCourses = programRepository.countByCategoryAndStatus(ProgramCategory.SHORT_COURSE,
                    ProgramStatus.IN_PROGRESS);

            return new ApiResponse<>(true, "stats", new AdminStats(totalusers, totalPrograms, totalActiveLearnerships,
                    totalActiveInternships, totalActiveShortCourses));
        } catch (Exception e) {
            return new ApiResponse<>(false, "An error occured while fetching stats", null);
        }

    }

    public ApiResponse<?> getActivities() {

        try {
            List<Activity> activities = activityRepository.findAllByOrderByCreatedAtDesc();
            return new ApiResponse<>(true, "activities", activities);
        } catch (Exception e) {
            return new ApiResponse<>(false, "An error occured while fetching activities", null);
        }
    }

}
