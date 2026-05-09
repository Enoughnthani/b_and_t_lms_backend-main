package com.app.b_and_t_lms.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.AssignRequest;
import com.app.b_and_t_lms.dto.ProgramDTO;
import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.models.ProgramStaff;
import com.app.b_and_t_lms.models.ProgramStaff.StaffRole;
import com.app.b_and_t_lms.models.ProgramStaffRole;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.repositories.ProgramRepository;
import com.app.b_and_t_lms.repositories.ProgramStaffRepository;
import com.app.b_and_t_lms.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProgramStaffService {

    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final ProgramStaffRepository programStaffRepository;

    public ProgramStaffService(
            ProgramRepository programRepository,
            UserRepository userRepository,
            ProgramStaffRepository programStaffRepository) {

        this.programRepository = programRepository;
        this.userRepository = userRepository;
        this.programStaffRepository = programStaffRepository;
    }

    public ApiResponse<?> singleAssign(AssignRequest dto) {
        try {

            Program program = programRepository.findById(dto.getProgramId()).orElse(null);
            User user = userRepository.findById(dto.getUserId()).orElse(null);

            if (program == null) {
                return new ApiResponse<>(false, "Program not found", null);
            }

            if (user == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            ProgramStaff ps = programStaffRepository
                    .findByProgramIdAndUserId(dto.getProgramId(), dto.getUserId())
                    .orElse(null);

            if (ps == null) {
                ps = new ProgramStaff();
                ps.setProgram(program);
                ps.setUser(user);
                ps.setAssignedDate(LocalDateTime.now());
                ps.setAssignedRoles(new ArrayList<>());
            }

            boolean roleExists = ps.getAssignedRoles().stream()
                    .anyMatch(r -> r.getRole().equals(dto.getRole()));

            if (!roleExists) {
                ProgramStaffRole newRole = new ProgramStaffRole();
                newRole.setRole(dto.getRole());
                newRole.setProgramStaff(ps);
                ps.getAssignedRoles().add(newRole);
            } else {
                return new ApiResponse<>(false, user.getFirstname() + " is already assigned to this role.", null);
            }

            programStaffRepository.save(ps);

            return new ApiResponse<>(true, user.getFirstname() + " assigned", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Assignment failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<?> bulkAssign(AssignRequest dto) {
        try {

            if (dto.getProgramId() == null || dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
                return new ApiResponse<>(false, "ProgramId and UserIds are required", null);
            }

            Program program = programRepository.findById(dto.getProgramId()).orElse(null);

            if (program == null) {
                return new ApiResponse<>(false, "Program not found", null);
            }

            int count = 0;

            for (Long userId : dto.getUserIds()) {

                User user = userRepository.findById(userId).orElse(null);
                if (user == null)
                    continue;

                ProgramStaff ps = programStaffRepository.findByProgramIdAndUserId(dto.getProgramId(), userId)
                        .orElse(null);

                if (ps == null) {
                    ps = new ProgramStaff();
                    ps.setProgram(program);
                    ps.setUser(user);
                    ps.setAssignedDate(LocalDateTime.now());
                    ps.setAssignedRoles(new ArrayList<>());
                }

                boolean roleExists = ps.getAssignedRoles().stream()
                        .anyMatch(r -> r.getRole().equals(dto.getRole()));

                if (!roleExists) {
                    ProgramStaffRole newRole = new ProgramStaffRole();
                    newRole.setRole(dto.getRole());
                    newRole.setProgramStaff(ps);
                    ps.getAssignedRoles().add(newRole);
                }

                programStaffRepository.save(ps);
                count++;
            }

            return new ApiResponse<>(true, count + " staff processed successfully ", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Bulk assign failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<?> singleRemove(AssignRequest dto) {
        try {

            ProgramStaff programStaff = programStaffRepository
                    .findByProgramIdAndUserId(dto.getProgramId(), dto.getUserId()).orElse(null);

            Program program = programRepository.findById(dto.getProgramId()).orElse(null);

            User user = userRepository.findById(dto.getUserId()).orElse(null);

            if (program == null) {
                return new ApiResponse<>(false, "Program not found.", null);
            }

            if (user == null) {
                return new ApiResponse<>(false, "An error occured! User is not found.", null);
            }

            if (programStaff == null) {
                return new ApiResponse<>(false, "Failed to unassign " + user.getFirstname(), null);
            }

            List<ProgramStaffRole> roles = programStaff.getAssignedRoles();
            StaffRole role = dto.getRole();

            if (roles.size() == 1 || role == null) {
                programStaffRepository.delete(programStaff);
                programStaffRepository.flush();
                return new ApiResponse<>( true,user.getFirstname() + " unassigned.",new ProgramDTO(program));
            }

            programStaff.getAssignedRoles().removeIf(r -> r.getRole().equals(role));
            programStaffRepository.flush();

            return new ApiResponse<>(    true,role + " role unassigned.",new ProgramDTO(program));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Remove failed: " + e.getMessage(), null);
        }
    }

    public ApiResponse<?> bulkRemove(AssignRequest dto) {
        try {

            if (dto.getProgramId() == null || dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
                return new ApiResponse<>(false, "ProgramId and UserIds are required", null);
            }

            Program program = programRepository.findById(dto.getProgramId()).orElse(null);

            if (program == null) {
                return new ApiResponse<>(false, "Program not found.", null);
            }

            int count = 0;

            for (Long userId : dto.getUserIds()) {

                if (userId == null)
                    continue;

                ProgramStaff programStaff = programStaffRepository
                        .findByProgramIdAndUserId(dto.getProgramId(), userId)
                        .orElse(null);

                if (programStaff == null)
                    continue;

                List<ProgramStaffRole> roles = programStaff.getAssignedRoles();

                if (roles.size() == 1 || dto.getRole() == null) {
                    programStaffRepository.delete(programStaff);
                } else {
                    StaffRole role = dto.getRole();
                    programStaff.getAssignedRoles()
                            .removeIf(r -> r.getRole() == role);
                }

                count++;
            }

            programStaffRepository.flush();

            return new ApiResponse<>(true, count + " removed successfully", new ProgramDTO(program));

        } catch (Exception e) {
            return new ApiResponse<>(false, "Bulk remove failed: " + e.getMessage(), null);
        }
    }
}