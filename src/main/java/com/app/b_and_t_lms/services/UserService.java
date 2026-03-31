package com.app.b_and_t_lms.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.BulkOperationResult;
import com.app.b_and_t_lms.dto.BulkRoleRequest;
import com.app.b_and_t_lms.dto.BulkStatusRequest;
import com.app.b_and_t_lms.dto.RoleAssignRequest;
import com.app.b_and_t_lms.dto.UserDTO;
import com.app.b_and_t_lms.dto.UserData;
import com.app.b_and_t_lms.models.Role;
import com.app.b_and_t_lms.models.Role.RoleName;
import com.app.b_and_t_lms.models.Status;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.repositories.UserRepository;
import com.app.b_and_t_lms.util.DataValidator;
import com.app.b_and_t_lms.util.RsaIdInfo;
import com.app.b_and_t_lms.util.RsaIdValidate;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse<?> createUser(UserDTO dto) {

        ApiResponse<?> resp = DataValidator.validate(dto);

        if (!resp.isSuccess()) {
            return new ApiResponse<>(false, resp.getMessage(), null);
        }

        if (!RsaIdValidate.isValid(dto.getIdNo())) {
            return new ApiResponse<>(false, "Invalid id number", null);
        }

        String gender = RsaIdInfo.getGender(dto.getIdNo());
        LocalDate dob = RsaIdInfo.getDateOfBirth(dto.getIdNo());

        User user = new User();

        List<Role> roles = dto.getRole().stream().map(role -> new Role(role, user)).toList();

        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(roles);
        user.setCreatedAt(Timestamp.from(Instant.now()));
        user.setGender(gender);
        user.setDob(dob);
        user.setContactNumber(dto.getContactNumber());
        user.setIdNumber(dto.getIdNo());
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);
        return new ApiResponse<>(true, "User account created successfully", null);
    }

    @Transactional
    public ApiResponse<UserData> updateUser(Long id, UserDTO dto) {

        try {
            User user = userRepository.findById(id).orElse(null);

            if (user == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            if (user.isSuperUser()) {
                return new ApiResponse<>(false, "Cannot change admin roles", null);
            }

            if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
                boolean exists = userRepository.existsByEmail(dto.getEmail());
                if (exists) {
                    return new ApiResponse<>(false, "Email already exists", new UserData(user));
                }
                user.setEmail(dto.getEmail());
            }

            if (dto.getIdNo() != null && !dto.getIdNo().equals(user.getIdNumber())) {
                boolean exists = userRepository.existsByIdNumber(dto.getIdNo());
                if (exists) {
                    return new ApiResponse<>(false, "ID number already exists", null);
                }
                user.setIdNumber(dto.getIdNo());
            }

            if (dto.getFirstname() != null) {
                user.setFirstname(dto.getFirstname());
            }

            if (dto.getLastname() != null) {
                user.setLastname(dto.getLastname());
            }

            if (dto.getContactNumber() != null) {
                user.setContactNumber(dto.getContactNumber());
            }

            if (dto.getStatus() != null) {
                user.setStatus(dto.getStatus());
            }

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            if (dto.getRole() != null && !dto.getRole().isEmpty()) {

                List<Role> roles = new ArrayList<>();

                for (RoleName roleName : dto.getRole()) {
                    try {

                        roles.add(new Role(roleName, user));
                    } catch (Exception e) {
                        return new ApiResponse<>(false, "Invalid role: " + roleName, null);
                    }
                }

                user.getRoles().clear();
                user.getRoles().addAll(roles);
            }

            User updatedUser = userRepository.save(user);
            return new ApiResponse<>(true, "User updated successfully", new UserData(updatedUser));

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to update user", null);
        }
    }

    public ApiResponse<?> deleteUser(Long id, Authentication authentication) {
        try {

            User deleteUser = userRepository.findById(id).orElse(null);

            if (deleteUser == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            if (deleteUser.isSuperUser()) {
                return new ApiResponse<>(false, "Cannot delete system super admin account", null);
            }

            userRepository.deleteById(id);
            return new ApiResponse<>(true, "User deleted successfully", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete user: " + e.getMessage(), null);
        }
    }

    public List<UserData> getAllUsers() {
        return userRepository.findAll().stream().map(UserData::new).toList();
    }

    public ApiResponse<?> deteleteUser(Long id) {

        if (id == null) {
            return new ApiResponse<>(false, "User id is required.", null);
        }

        ApiResponse<?> apiResponse = new ApiResponse<>();

        try {
            User user = userRepository.findById(id).orElse(null);

            if (user == null) {
                return new ApiResponse<>(false, "No user found", null);
            }

            userRepository.delete(user);
            apiResponse = new ApiResponse<>(true, "User has been deleted successfully", null);
        } catch (Exception e) {
            apiResponse = new ApiResponse<>(false, "An error occurred while deleting the user.", null);
        }

        return apiResponse;
    }

    public ApiResponse<BulkOperationResult> bulkCreateUsers(MultipartFile file) throws IOException {

        BulkOperationResult result = new BulkOperationResult();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            int lineNumber = 0;

            // ===== HEADER =====
            String headerLine = reader.readLine();
            lineNumber++;

            if (headerLine == null) {
                return new ApiResponse<>(false, "CSV file is empty", null);
            }

            headerLine = headerLine.replace("\uFEFF", "");

            List<String> headers = parseCSVLine(headerLine)
                    .stream()
                    .map(h -> h.trim().toLowerCase())
                    .collect(Collectors.toList());

            List<String> requiredFields = List.of(
                    "firstname", "lastname", "email", "contactNumber", "idNumber", "roles", "status");

            List<String> missingHeaders = requiredFields.stream()
                    .filter(field -> !headers.contains(field.toLowerCase()))
                    .toList();

            if (!missingHeaders.isEmpty()) {
                return new ApiResponse<>(false,
                        "Invalid CSV format. Missing headers: " + String.join(", ", missingHeaders),
                        null);
            }

            // ===== ROWS =====
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty())
                    continue;

                try {
                    List<String> data = parseCSVLine(line);

                    String firstName = getValue(data, 0);
                    String lastName = getValue(data, 1);
                    String email = getValue(data, 2);
                    String phoneNumber = getValue(data, 3);
                    String idNumber = getValue(data, 4);
                    String roleStr = getValue(data, 5);

                    String gender = RsaIdInfo.getGender(idNumber);
                    LocalDate dobStr = RsaIdInfo.getDateOfBirth(idNumber);

                    data.forEach(System.out::println);

                    System.out.println("Id number ======================= " + idNumber);

                    // ===== VALIDATION =====
                    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()
                            || idNumber.isBlank()) {
                        result.getErrors().add("Line " + lineNumber + ": firstname, lastname, email required");
                        result.setErrorCount(result.getErrorCount() + 1);
                        continue;
                    }

                    if (userRepository.existsByEmail(email)) {
                        result.getErrors().add("Line " + lineNumber + ": Email exists -> " + email);
                        result.setErrorCount(result.getErrorCount() + 1);
                        continue;
                    }

                    if (!idNumber.isEmpty() && userRepository.existsByIdNumber(idNumber)) {
                        result.getErrors().add("Line " + lineNumber + ": ID exists -> " + idNumber);
                        result.setErrorCount(result.getErrorCount() + 1);
                        continue;
                    }

                    // ===== USER =====
                    User user = new User();
                    user.setFirstname(firstName);
                    user.setLastname(lastName);
                    user.setEmail(email);
                    user.setContactNumber(phoneNumber);
                    user.setIdNumber(idNumber);
                    user.setGender(gender);
                    user.setDob(dobStr);
                    user.setStatus(Status.ACTIVE);
                    user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

                    user.setPassword(passwordEncoder.encode("#123"));

                    // Roles
                    if (!roleStr.isEmpty()) {
                        List<Role> roles = new ArrayList<>();
                        for (String r : roleStr.split(";|,")) {
                            try {
                                Role.RoleName roleName = Role.RoleName.valueOf(r.trim().toUpperCase());
                                roles.add(new Role(roleName, user));
                            } catch (Exception e) {
                                result.getErrors().add("Line " + lineNumber + ": Invalid role -> " + r.trim());
                            }
                        }
                        user.setRoles(roles);
                    }

                    User savedUser = userRepository.save(user);
                    result.addSuccess(savedUser.getId());

                } catch (DataIntegrityViolationException e) {

                    String message = resolveDatabaseError(e);

                    result.getErrors().add("Line " + lineNumber + ": " + message);
                    result.setErrorCount(result.getErrorCount() + 1);

                } catch (Exception e) {

                    result.getErrors().add("Line " + lineNumber + ": Unexpected error");
                    result.setErrorCount(result.getErrorCount() + 1);
                }
            }
        }

        String message = "Processed: " + (result.getSuccessCount() + result.getErrorCount()) +
                ", " + result.getSummary();

        return new ApiResponse<>(true, message, result);
    }

    private String getValue(List<String> data, Integer index) {
        return (index != null && index < data.size()) ? data.get(index).trim() : "";
    }

    private List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        result.add(field.toString());

        return result;
    }

    @Transactional
    public ApiResponse<BulkOperationResult> bulkAssignRole(BulkRoleRequest request) {

        BulkOperationResult result = new BulkOperationResult();
        List<User> users = userRepository.findAllById(request.getUserIds());

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        for (Long userId : request.getUserIds()) {

            User user = userMap.get(userId);

            if (user == null) {
                result.addError(userId, "User not found");
                continue;
            }

            try {
                boolean hasRole = user.getRoles().stream()
                        .anyMatch(r -> r.getName() == request.getRole());

                if (hasRole) {
                    result.addError(userId, "User already has this role");
                    continue;
                }

                Role role = new Role();
                role.setName(request.getRole());
                role.setUser(user);

                user.getRoles().add(role);
                userRepository.saveAndFlush(user);

                result.addSuccess(userId);

            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                result.addError(userId, "User already has this role");

            } catch (Exception e) {
                result.addError(userId, "Failed to assign role");
            }
        }

        int total = result.getSuccessCount() + result.getErrorCount();
        String message = "Processed: " + total + ", " + result.getSummary();
        return new ApiResponse<>(true, message, result);
    }

    @Transactional
    public BulkOperationResult bulkDeleteUsers(List<Long> userIds) {
        BulkOperationResult result = new BulkOperationResult();

        if (userIds == null || userIds.isEmpty()) {
            result.addError(null, "User IDs list cannot be empty");
            return result;
        }

        // Batch fetch users to check existence
        List<User> users = userRepository.findAllById(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        for (Long userId : userIds) {
            try {
                User user = userMap.get(userId);

                if (user == null) {
                    result.addError(userId, "User not found");
                    continue;
                }

                if (user.isSuperUser()) {
                    result.addError(userId, "Cannot delete system super admin");
                    continue;
                }

                userRepository.deleteById(userId);
                result.addSuccess(userId);

            } catch (DataIntegrityViolationException e) {
                result.addError(userId, "Cannot delete user due to existing references (e.g., loans, transactions)");
            } catch (Exception e) {
                result.addError(userId, "Failed to delete user: " + e.getMessage());
            }
        }

        return result;
    }

    @Transactional
    public ApiResponse<?> activateUser(long userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ApiResponse<>(false, "No user found with id " + userId, null);
        }

        if (user.getStatus().equals(Status.ACTIVE)) {
            return new ApiResponse<>(true, "User is already active", null);
        }

        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        return new ApiResponse<>(true, "User activated", null);
    }

    @Transactional
    public ApiResponse<?> deactivateUser(long userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ApiResponse<>(false, "No user found with id " + userId, null);
        }

        if (user.isSuperUser()) {
            return new ApiResponse<>(false, "Cannot deactivate system super admin", null);
        }

        if (user.getStatus().equals(Status.INACTIVE)) {
            return new ApiResponse<>(true, "User is already inactive", null);
        }

        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
        return new ApiResponse<>(true, "User deactivated", null);
    }

    @Transactional
    public ApiResponse<?> assignRoles(RoleAssignRequest roleAssignRequest) {
        User user = userRepository.findById(roleAssignRequest.getUserId()).orElse(null);

        if (user == null) {
            return new ApiResponse<>(false, "No user found with id " + roleAssignRequest.getUserId(), null);
        }

        if (user.isSuperUser()) {
            return new ApiResponse<>(false, "Cannot modify roles for system super admin", null);
        }

        if (roleAssignRequest.getRoles() == null || roleAssignRequest.getRoles().isEmpty()) {
            return new ApiResponse<>(false, "No roles provided", null);
        }

        List<Role> roles = roleAssignRequest.getRoles().stream()
                .map(roleName -> new Role(roleName, user))
                .toList();

        user.getRoles().clear();
        user.getRoles().addAll(roles);
        userRepository.save(user);
        return new ApiResponse<>(true, "Changes saved", null);
    }

    @Transactional
    public ApiResponse<BulkOperationResult> bulkUpdateStatus(BulkStatusRequest request) {

        BulkOperationResult result = new BulkOperationResult();

        List<User> users = userRepository.findAllById(request.getUserIds());

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        for (Long userId : request.getUserIds()) {

            User user = userMap.get(userId);

            if (user == null) {
                result.addError(userId, "User not found");
                continue;
            }

            if (user.isSuperUser() && request.getStatus().equals(Status.INACTIVE)) {
                result.addError(userId, "Can not deactivate super user.");
                continue;
            }

            try {
                if (user.getStatus() == request.getStatus()) {
                    result.addSuccess(userId);
                    continue;
                }

                user.setStatus(request.getStatus());
                userRepository.save(user);
                result.addSuccess(userId);

            } catch (Exception e) {
                result.addError(userId, "Failed to update status");
            }
        }
        int total = result.getSuccessCount() + result.getErrorCount();
        String message = "Processed: " + total + ", " + result.getSummary();

        return new ApiResponse<>(true, message, result);
    }

    private String resolveDatabaseError(DataIntegrityViolationException e) {
        String msg = e.getMostSpecificCause() != null
                ? e.getMostSpecificCause().getMessage().toLowerCase()
                : "";

        if (msg.contains("uk_user_id_number")) {
            return "Duplicate Entry ID number";
        }

        if (msg.contains("uk_user_email")) {
            return "Duplicate Entry for Email";
        }

        if (msg.contains("uk_user_contact_number")) {
            return "Duplicate Entry  Contact number.";
        }

        if (msg.contains("duplicate entry")) {
            return "Duplicate value already exists";
        }

        return "Database error";
    }
}
