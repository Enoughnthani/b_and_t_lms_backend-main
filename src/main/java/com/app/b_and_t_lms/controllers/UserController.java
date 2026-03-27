package com.app.b_and_t_lms.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.app.b_and_t_lms.dto.UserDTO;
import com.app.b_and_t_lms.dto.UserData;
import com.app.b_and_t_lms.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
        ApiResponse<?> response;
        try {
            response = userService.createUser(userDTO);
        } catch (DataIntegrityViolationException e) {
            response = new ApiResponse<>(false, "An account with this email or ID already exists.", null);
        } catch (Exception e) {
            response = new ApiResponse<>(false, "Failed to create user account. " + e.getMessage(), null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserData>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<?>> bulkCreateUsers(@RequestParam("file") MultipartFile file) {

        try {
            // ===== VALIDATION =====
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

            // ===== SERVICE CALL =====
            ApiResponse<Map<String, Object>> serviceResponse = userService.bulkCreateUsers(file);

            // 🔥 Just return service response directly (no re-wrapping)
            return ResponseEntity.ok(serviceResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Failed to process bulk upload: " + e.getMessage(),
                            null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserData> getUserById(@PathVariable Long id) {
        UserData user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        ApiResponse<?> response;
        try {
            response = userService.updateUser(id, userDTO);
        } catch (Exception e) {
            response = new ApiResponse<>(false, "Failed to update user.", null);
        }
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(userService.deleteUser(id, authentication));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-role")
    public ResponseEntity<ApiResponse<BulkOperationResult>> bulkAssignRole(
            @RequestBody BulkRoleRequest request) {

        BulkOperationResult result = userService.bulkAssignRole(
                request.getUserIds(),
                request.getRole());

        boolean success = result.isSuccess();
        boolean partial = result.isPartialSuccess();

        String message;
        if (success) {
            message = "Role assigned to " + result.getSuccessCount() + " users";
        } else if (partial) {
            message = "Role assignment partially completed. " + result.getSummary();
        } else {
            message = "Failed to assign roles. " + result.getSummary();
        }

        ApiResponse<BulkOperationResult> response = new ApiResponse<>(
                success,
                message,
                result);


        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<BulkOperationResult>> bulkDeleteUsers(@RequestBody BulkDeleteRequest request) {

        // Validate request
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
}