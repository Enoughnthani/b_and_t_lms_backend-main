package com.app.b_and_t_lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.BulkDeleteRequest;
import com.app.b_and_t_lms.dto.BulkOperationResult;
import com.app.b_and_t_lms.dto.BulkRoleRequest;
import com.app.b_and_t_lms.dto.BulkStatusRequest;
import com.app.b_and_t_lms.dto.RoleAssignRequest;
import com.app.b_and_t_lms.dto.UserDTO;
import com.app.b_and_t_lms.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ApiResponse<?> createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @GetMapping("/learners")
    @PreAuthorize("hasRole('PROGRAM_MANAGER')")
    public ApiResponse<?> getLeaners() {
        return userService.getAllLeaners();
    }

    @GetMapping("/interns")
    @PreAuthorize("hasRole('PROGRAM_MANAGER')")
    public ApiResponse<?> getInterns() {
        return userService.getInterns();
    }

    @GetMapping("/mentors")
    @PreAuthorize("hasRole('PROGRAM_MANAGER')")
    public ApiResponse<?> getMentors() {
        return userService.getMentors();
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('PROGRAM_MANAGER')")
    public ApiResponse<?> getStaff() {
        return userService.getStaff();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(userService.deleteUser(id, authentication));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<?>> bulkCreateUsers(@RequestParam("file") MultipartFile file) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "File is empty", null));
            }

            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.equals("text/csv") &&
                            !contentType.equals("application/vnd.ms-excel"))) {

                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Only CSV files are allowed", null));
            }

            ApiResponse<?> serviceResponse = userService.bulkCreateUsers(file);
            return ResponseEntity.ok(serviceResponse);

        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Failed to process bulk upload: ", null));
        }
    }

    @PostMapping("/bulk-role")
    public ResponseEntity<ApiResponse<BulkOperationResult>> bulkAssignRole(
            @RequestBody BulkRoleRequest request) {

        try {
            ApiResponse<BulkOperationResult> response = userService.bulkAssignRole(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, "Failed to assign roles.", null));
        }
    }

    @PostMapping("bulk-status")
    public ApiResponse<?> bulkUpdateStatus(@RequestBody BulkStatusRequest request) {
        try {
            return userService.bulkUpdateStatus(request);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to update user status.", null);
        }
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<BulkOperationResult>> bulkDeleteUsers(@RequestBody BulkDeleteRequest request) {

        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            ApiResponse<BulkOperationResult> response = new ApiResponse<>(
                    false,
                    "User IDs list cannot be empty",
                    null);
            return ResponseEntity.badRequest().body(response);
        }

        BulkOperationResult result = userService.bulkDeleteUsers(request.getUserIds());

        boolean success = result.isSuccess();
        boolean partial = result.isPartialSuccess();

        String message;
        if (success) {
            message = "Successfully deleted " + result.getSuccessCount() + " users";
        } else if (partial) {
            message = "Partial deletion completed. " + result.getSummary();
        } else {
            message = "Failed to delete users. " + result.getSummary();
        }

        ApiResponse<BulkOperationResult> response = new ApiResponse<>(
                success,
                message,
                result);

        HttpStatus status;
        if (success) {
            status = HttpStatus.OK;
        } else if (partial) {
            status = HttpStatus.MULTI_STATUS;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("{userId}/activate")
    public ApiResponse<?> activate(@PathVariable long userId) {
        try {
            return userService.activateUser(userId);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to activate user", null);
        }
    }

    @PostMapping("{userId}/deactivate")
    public ApiResponse<?> deactivate(@PathVariable long userId) {
        try {
            return userService.deactivateUser(userId);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to deactivate user " + e.getMessage(), null);
        }
    }

    @PutMapping("roles")
    public ApiResponse<?> assignRoles(@RequestBody RoleAssignRequest roleAssignRequest) {

        try {
            return userService.assignRoles(roleAssignRequest);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to assign roles " + e, null);
        }

    }

}