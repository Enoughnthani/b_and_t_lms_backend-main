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
import com.app.b_and_t_lms.dto.UserDTO;
import com.app.b_and_t_lms.dto.UserData;
import com.app.b_and_t_lms.models.Role;
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

    public ApiResponse<Map<String, Object>> bulkCreateUsers(MultipartFile file) throws IOException {

        Map<String, Object> payload = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<UserData> createdUsers = new ArrayList<>();
        int successCount = 0;

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
                    "firstname",
                    "lastname",
                    "email",
                    "phone",
                    "idnumber",
                    "roles",
                    "status");

            List<String> missingHeaders = requiredFields.stream()
                    .filter(field -> !headers.contains(field))
                    .toList();

            if (!missingHeaders.isEmpty()) {
                String message = "Invalid CSV format. Missing headers: " + String.join(", ", missingHeaders);
                return new ApiResponse<>(false, message, null);
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
                    String phoneNumber = getValue(data, headerIndex, "phonenumber");
                    String idNumber = getValue(data, headerIndex, "idnumber");
                    String dobStr = getValue(data, headerIndex, "dob");
                    String gender = getValue(data, headerIndex, "gender");
                    String roleStr = getValue(data, headerIndex, "roles");

                    // ===== VALIDATION =====
                    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                        errors.add("Line " + lineNumber + ": firstname, lastname, email required");
                        continue;
                    }

                    if (userRepository.existsByEmail(email)) {
                        errors.add("Line " + lineNumber + ": Email exists -> " + email);
                        continue;
                    }

                    if (!idNumber.isEmpty() && userRepository.existsByIdNumber(idNumber)) {
                        errors.add("Line " + lineNumber + ": ID exists -> " + idNumber);
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

                    // DOB
                    if (!dobStr.isEmpty()) {
                        try {
                            user.setDob(LocalDate.parse(dobStr));
                        } catch (Exception e) {
                            errors.add("Line " + lineNumber + ": Invalid DOB (YYYY-MM-DD)");
                            continue;
                        }
                    }

                    // Password
                    String tempPassword = "#123";
                    user.setPassword(passwordEncoder.encode(tempPassword));

                    // Roles (optional)
                    if (!roleStr.isEmpty()) {
                        List<Role> roles = new ArrayList<>();
                        for (String r : roleStr.split(";|,")) {
                            String roleNameStr = r.trim().toUpperCase(); // normalize for enum
                            if (roleNameStr.isEmpty())
                                continue;

                            try {
                                Role.RoleName roleName = Role.RoleName.valueOf(roleNameStr);
                                Role role = new Role(roleName, user); // set name and user
                                roles.add(role);
                            } catch (IllegalArgumentException e) {
                                errors.add("Line " + lineNumber + ": Invalid role -> " + r.trim());
                            }
                        }

                        if (!roles.isEmpty()) {
                            user.setRoles(roles);
                        }
                    }

                    User savedUser = userRepository.save(user);
                    createdUsers.add(new UserData(savedUser));
                    successCount++;

                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        // ===== RESPONSE =====
        payload.put("successCount", successCount);
        payload.put("errorCount", errors.size());
        payload.put("errors", errors);
        payload.put("createdUsers", createdUsers);

        String message = "Processed: " + (successCount + errors.size()) +
                ", Success: " + successCount +
                ", Errors: " + errors.size();

        return new ApiResponse<>(true, message, payload);
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
    public BulkOperationResult bulkAssignRole(List<Long> userIds, String roleName) {

        BulkOperationResult result = new BulkOperationResult();

        // ✅ Validate role once
        Role.RoleName roleEnum;
        try {
            roleEnum = Role.RoleName.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            userIds.forEach(id -> result.addError(id, "Invalid role name"));
            return result;
        }

        // ✅ Batch fetch users
        List<User> users = userRepository.findAllById(userIds);

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        for (Long userId : userIds) {

            User user = userMap.get(userId);

            if (user == null) {
                result.addError(userId, "User not found");
                continue;
            }

            try {
                boolean hasRole = user.getRoles().stream()
                        .anyMatch(r -> r.getName() == roleEnum);

                if (hasRole) {
                    result.addError(userId, "User already has this role");
                    continue;
                }

                Role role = new Role();
                role.setName(roleEnum);
                role.setUser(user);

                user.getRoles().add(role);

                // ✅ SAVE + FLUSH immediately
                userRepository.saveAndFlush(user);

                result.addSuccess(userId);

            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                // ✅ Handle DB constraint safely
                result.addError(userId, "User already has this role");

            } catch (Exception e) {
                result.addError(userId, "Failed to assign role");
            }
        }

        return result;
    }

    public UserData getUserById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
    }

    public ApiResponse<?> updateUser(Long id, UserDTO userDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    public ApiResponse<?> deleteUser(Long id, Authentication authentication) {
        try {
            User currentAdmin = (User) authentication.getPrincipal();

            User deleteUser = userRepository.findById(id).orElse(null);

            if (currentAdmin == null || deleteUser == null) {
                return new ApiResponse<>(false, "User not found", null);
            }

            if (currentAdmin.getId().equals(deleteUser.getId())) {
                return new ApiResponse<>(false, "You cannot delete your own account", null);
            }

            if (deleteUser.isSuperUser()) {
                return new ApiResponse<>(false, "Cannot delete system super admin", null);
            }

            userRepository.deleteById(id);
            return new ApiResponse<>(true, "User deleted successfully", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete user: " + e.getMessage(), null);
        }
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

             

                // Optional: Check if user has active loans
                // if (hasActiveLoans(userId)) {
                // result.addError(userId, "User has active loans and cannot be deleted");
                // continue;
                // }

                // Perform deletion
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
}
