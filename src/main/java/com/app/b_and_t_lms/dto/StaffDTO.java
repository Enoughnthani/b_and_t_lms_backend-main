package com.app.b_and_t_lms.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.app.b_and_t_lms.models.ProgramStaff;
import com.app.b_and_t_lms.models.Status;
import com.app.b_and_t_lms.models.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffDTO {

    private String firstname;
    private String lastname;
    private String email;
    private String gender;
    private String contactNumber;
    private List<String> roles;
    private Long id;
    private Status status;
    private boolean isActive;
    private LocalDateTime asiggnedDate;
    private Map<Long, List<String>> assignedRoles;

    public StaffDTO() {
    }

    public StaffDTO(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.contactNumber = user.getContactNumber();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(r -> r.getName().name()).toList();
        this.status = user.getStatus();
        this.gender = user.getGender();
        this.isActive = user.isAccountNonLocked();
        this.assignedRoles = user.getProgramStaffs().stream()
                .map(AssignedProgramRole::new)
                .collect(Collectors.toMap(
                        AssignedProgramRole::getProgramId,
                        AssignedProgramRole::getRoles));
    }

    public StaffDTO(ProgramStaff programStaff) {
        User user = programStaff.getUser();

        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.contactNumber = user.getContactNumber();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(r -> r.getName().name()).toList();
        this.status = user.getStatus();
        this.gender = user.getGender();
        this.isActive = user.isAccountNonLocked();
        this.asiggnedDate = programStaff.getAssignedDate();
        this.assignedRoles = Map.of(
                programStaff.getProgram().getId(),
                programStaff.getAssignedRoles()
                        .stream()
                        .map(r -> r.getRole().name()) 
                        .toList());
    }

}
