package com.app.b_and_t_lms.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.EnrollmentRequestDTO;
import com.app.b_and_t_lms.dto.ProgramDTO;
import com.app.b_and_t_lms.models.Enrollment;
import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.repositories.EnrollmentRepository;
import com.app.b_and_t_lms.repositories.ProgramRepository;
import com.app.b_and_t_lms.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EnrollmentService {

    private final ProgramRepository programRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public EnrollmentService(ProgramRepository programRepository, EnrollmentRepository enrollmentRepository,
            UserRepository userRepository, ProgramService programService) {
        this.programRepository = programRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;

    }

    public ApiResponse<?> enroll(EnrollmentRequestDTO dto) {
        try {
            Program program = programRepository.findById(dto.getProgramId()).orElse(null);
            User user = userRepository.findById(dto.getUserId()).orElse(null);

            if (program == null) {
                return new ApiResponse<>(false, "Program not found", null);
            }

            if (user == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setProgram(program);
            enrollment.setUser(user);
            enrollment.setEnrollmentDate(LocalDateTime.now());

            program.getEnrollments().add(enrollment);
            programRepository.save(program);

            return new ApiResponse<>(true, user.getFirstname() + " Enrolled Successfully.", new ProgramDTO(program));

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("UKsd7o8vg10i3wr76pt6ivxasop")) {
                return new ApiResponse<>(false, "Learner is already enrolled in this program", null);
            }
            return new ApiResponse<>(false, "Enrollment failed due to data integrity issue", null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Enrollment failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<?> remove(EnrollmentRequestDTO dto) {

        try {
            Program program = programRepository.findById(dto.getProgramId()).orElse(null);

            User user = userRepository.findById(dto.getUserId()).orElse(null);

            if (user == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            if (program == null) {
                return new ApiResponse<>(false,
                        "No program found with id: " + dto.getProgramId(), null);
            }

            if (dto.getUserId() == null) {
                return new ApiResponse<>(false,
                        "User ID is required for removal", null);
            }

            enrollmentRepository.deleteByUserId(dto.getUserId());

            return new ApiResponse<>(true, user.getFirstname() + " removed.", new ProgramDTO(program));

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to remove user from program " + e.getMessage(), null);
        }
    }

    public ApiResponse<?> bulkEnroll(EnrollmentRequestDTO dto) {

        try {
            if (dto.getProgramId() == null || dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
                return new ApiResponse<>(false, "ProgramId and UserIds are required", null);
            }

            Program program = programRepository.findById(dto.getProgramId()).orElse(null);

            if (program == null) {
                return new ApiResponse<>(false,
                        "No program found with id: " + dto.getProgramId(), null);
            }

            List<Enrollment> enrollmentsToSave = new ArrayList<>();

            for (Long userId : dto.getUserIds()) {

                boolean exists = enrollmentRepository
                        .existsByProgramIdAndUserId(dto.getProgramId(), userId);

                if (!exists) {
                    User user = userRepository.findById(userId).orElse(null);

                    if (user != null) {
                        Enrollment enrollment = new Enrollment();
                        enrollment.setProgram(program);
                        enrollment.setUser(user);
                        enrollment.setEnrollmentDate(LocalDateTime.now());

                        enrollmentsToSave.add(enrollment);
                    }
                }
            }

            enrollmentRepository.saveAll(enrollmentsToSave);

            return new ApiResponse<>(true,
                    enrollmentsToSave.size() + " users enrolled successfully", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Bulk enroll failed", null);
        }
    }

    public ApiResponse<?> bulkRemove(EnrollmentRequestDTO dto) {
        try {

            Program program = programRepository.findById(dto.getProgramId()).orElse(null);

            if (dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
                return new ApiResponse<>(false, "No user IDs provided", null);
            }

            if (program == null) {
                return new ApiResponse<>(false, "Program not found", null);
            }

            int deletedCount = enrollmentRepository.deleteByProgramIdAndUserIds(
                    dto.getProgramId(),
                    dto.getUserIds());

            return new ApiResponse<>(true, "Removed " + deletedCount + " enrollment(s)", new ProgramDTO(program));

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed: ", null);
        }
    }

    public ApiResponse<?> countByProgramId(Long programId) {
        try {
            return new ApiResponse<>(true, "Total enrolled learners", enrollmentRepository.countByProgramId(programId));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Request Failed", null);
        }
    }
}
