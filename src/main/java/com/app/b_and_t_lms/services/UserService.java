package com.app.b_and_t_lms.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.app.b_and_t_lms.models.Status;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.models.Role.RoleName;
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

           
            if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
                boolean exists = userRepository.existsByEmail(dto.getEmail());
                if (exists) {
                    return new ApiResponse<>(false, "Email already exists", new UserData(user));
                }
                user.setEmail(dto.getEmail());
            }

            // ✅ ID NUMBER UNIQUE CHECK
            if (dto.getIdNo() != null && !dto.getIdNo().equals(user.getIdNumber())) {
                boolean exists = userRepository.existsByIdNumber(dto.getIdNo());
                if (exists) {
                    return new ApiResponse<>(false, "ID number already exists", null);
                }
                user.setIdNumber(dto.getIdNo());
            }

            // ✅ BASIC FIELDS (ONLY UPDATE IF PROVIDED)
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

    public ApiResponse<?> updateUser(UserDTO dto) {

        ApiResponse<?> resp = DataValidator.validate(dto);
        if (!resp.isSuccess()) {
            return new ApiResponse<>(false, resp.getMessage(), null);
        }

        if (!RsaIdValidate.isValid(dto.getIdNo())) {
            return new ApiResponse<>(false, "Invalid id number", null);
        }

        // Fetch existing user
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map roles
        List<Role> roles = dto.getRole()
                .stream()
                .map(roleName -> new Role(roleName, user)) // careful: ideally fetch existing Role entities from DB
                .toList();

        String gender = RsaIdInfo.getGender(dto.getIdNo());
        LocalDate dob = RsaIdInfo.getDateOfBirth(dto.getIdNo());

        // Update fields
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setRoles(roles);
        user.setGender(gender);
        user.setDob(dob);
        user.setContactNumber(dto.getContactNumber());
        user.setIdNumber(dto.getIdNo());
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);

        return new ApiResponse<>(true, "User account updated successfully", null);
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
                    "firstname", "lastname", "email", "phone", "idnumber", "roles", "status");

            List<String> missingHeaders = requiredFields.stream()
                    .filter(field -> !headers.contains(field))
                    .toList();

            if (!missingHeaders.isEmpty()) {
                return new ApiResponse<>(false,
                        "Invalid CSV format. Missing headers: " + String.join(", ", missingHeaders),
                        null);
            }

            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                headerIndex.put(headers.get(i), i);
            }

            // ===== ROWS =====
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty())
                    continue;

                try {
                    List<String> data = parseCSVLine(line);

                    String firstName = getValue(data, headerIndex, "firstname");
                    String lastName = getValue(data, headerIndex, "lastname");
                    String email = getValue(data, headerIndex, "email");
                    String phoneNumber = getValue(data, headerIndex, "phone");
                    String idNumber = getValue(data, headerIndex, "idnumber");
                    String dobStr = getValue(data, headerIndex, "dob");
                    String gender = getValue(data, headerIndex, "gender");
                    String roleStr = getValue(data, headerIndex, "roles");

                    // ===== VALIDATION =====
                    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
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
                    user.setStatus(Status.ACTIVE);
                    user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

                    if (!dobStr.isEmpty()) {
                        try {
                            user.setDob(LocalDate.parse(dobStr));
                        } catch (Exception e) {
                            result.getErrors().add("Line " + lineNumber + ": Invalid DOB");
                            result.setErrorCount(result.getErrorCount() + 1);
                            continue;
                        }
                    }

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

                } catch (Exception e) {
                    result.getErrors().add("Line " + lineNumber + ": " + e.getMessage());
                    result.setErrorCount(result.getErrorCount() + 1);
                }
            }
        }

        String message = "Processed: " + (result.getSuccessCount() + result.getErrorCount()) +
                ", " + result.getSummary();

        return new ApiResponse<>(true, message, result);
    }

    private String getValue(List<String> data, Map<String, Integer> headerIndex, String key) {
        Integer index = headerIndex.get(key);
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

            try {
                if (user.getStatus() == request.getStatus()) {
                    result.addError(userId, "User is already: " + request.getStatus());
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
}
