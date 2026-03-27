package com.app.b_and_t_lms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.ForgotPasswordRequest;
import com.app.b_and_t_lms.dto.LoginRequest;
import com.app.b_and_t_lms.dto.UserData;
import com.app.b_and_t_lms.security.UserPages;
import com.app.b_and_t_lms.services.AuthService;
import com.app.b_and_t_lms.services.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password,
            @RequestParam(name = "remember-me", required = false) boolean rememberMe, HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            ApiResponse<UserData> userData = authService.login(new LoginRequest(email, password, rememberMe), request,
                    response);
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Failed to login", null));
        }
    }

    @GetMapping("/check_auth")
    public ResponseEntity<?> checkPage(Authentication authentication) {
        try {
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
            return ResponseEntity.ok(new ApiResponse<>(isAuthenticated,
                    isAuthenticated ? "authenticated" : "not authenticated", isAuthenticated));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "not authenticated", null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        try {
            System.out.println(authentication);
            return ResponseEntity.ok(authService.me(authentication));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/page_access")
    public ResponseEntity<?> pageAccess(@RequestBody String page, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }

            boolean allowed = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> UserPages.allowedPages.get(role.replace("ROLE_", "")).contains(page));
            if (allowed) {
                return ResponseEntity.ok(new ApiResponse<>(true, "authorized", null));
            }

            return ResponseEntity.status(403).build();

        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session, HttpServletResponse response) {

        try {
            authService.logout(session, response);
            return ResponseEntity.ok(new ApiResponse<>(true, "Logout successful", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "failed", null));
        }
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request, HttpSession session) {
        try {
            return ResponseEntity.ok(passwordResetService.requestOTP(request, session));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "failed to reset password.", null));
        }
    }

    @PostMapping("/verify_otp")
    public ResponseEntity<?> verifyOTP(@RequestBody ForgotPasswordRequest request, HttpSession session) {
        try {
            return ResponseEntity.ok(passwordResetService.verifyOTP(request, session));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "failed to verify otp", null));
        }
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@RequestBody ForgotPasswordRequest request, HttpSession session) {
        try {
            return ResponseEntity.ok(passwordResetService.resetPassword(request, session));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "failed to reset password.", null));
        }
    }

}
