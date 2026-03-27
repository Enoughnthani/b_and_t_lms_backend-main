package com.app.b_and_t_lms.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.ForgotPasswordRequest;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.models.UserOtp;
import com.app.b_and_t_lms.repositories.UserOtpRepository;
import com.app.b_and_t_lms.repositories.UserRepository;
import com.app.b_and_t_lms.util.PasswordValidator;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class PasswordResetService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserOtpRepository userOtpRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public PasswordResetService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String generateOTP() {
        return String.format("%06d", random.nextInt(999999));
    }

    @Transactional
    public ApiResponse<?> requestOTP(ForgotPasswordRequest request, HttpSession session) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return new ApiResponse<>(false, "Email field cannot be empty", null);
        }

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return new ApiResponse<>(true, "If your email is registered, you will receive an OTP.", null);
        }

        if (!user.isAccountNonLocked()) {
            return new ApiResponse<>(false, "Your account is locked. Please contact support.", null);
        }

        session.setAttribute("otp_user", user);
        String otpCode = generateOTP();
        UserOtp userOtp = user.getUserOtp() == null ? new UserOtp() : user.getUserOtp();

        if (userOtp.getLastRequestTime() != null) {
            if (userOtp.getLastRequestTime().isAfter(LocalDateTime.now().minusMinutes(1))) {
                long secondsLeft = 120 - ChronoUnit.SECONDS.between(userOtp.getLastRequestTime(), LocalDateTime.now());
                return new ApiResponse<>(false,
                        "Please wait " + secondsLeft + " seconds before requesting another OTP.",
                        null);
            }

            if (userOtp.getLastRequestTime().isBefore(LocalDateTime.now().minusHours(1))) {
                userOtp.setRequestCount(0);
            }
        }

        if (userOtp.getRequestCount() >= 3) {
            return new ApiResponse<>(false,
                    "Maximum OTP requests reached. Please try again after 1 hour.",
                    null);
        }

        userOtp.setUser(user);
        userOtp.setOtp(otpCode);
        userOtp.setExpiryDate(LocalDateTime.now().plusMinutes(6));
        userOtp.setUsed(false);
        userOtp.setAttemptCount(0);
        userOtp.setLastAttemptTime(null);

        userOtp.setRequestCount(userOtp.getRequestCount() + 1);
        userOtp.setLastRequestTime(LocalDateTime.now());

        if (userOtp.getCreatedAt() == null) {
            userOtp.setCreatedAt(LocalDateTime.now());
        }

        userOtpRepository.save(userOtp);

        String name = user.getFirstname() != null ? user.getFirstname() : "User";
        emailService.sendOtpEmail(request.getEmail(), otpCode, name);

        return new ApiResponse<>(true, "If your email is registered, you will receive an OTP.", null);
    }

    @Transactional
    public ApiResponse<?> verifyOTP(ForgotPasswordRequest request, HttpSession session) {
        User user = (User) session.getAttribute("otp_user");

        if (user == null || user.getUserOtp() == null) {
            return new ApiResponse<>(false, "Invalid or expired OTP", null);
        }

        UserOtp userOtp = user.getUserOtp();

        if (userOtp.getLastAttemptTime() != null) {
            if (userOtp.getLastAttemptTime().isBefore(LocalDateTime.now().minusMinutes(15))) {
                userOtp.setAttemptCount(0);
            }
        }

        if (userOtp.getAttemptCount() >= 5) {
            return new ApiResponse<>(false, "Too many failed attempts. Please request a new OTP.", true);
        }

        userOtp.setLastAttemptTime(LocalDateTime.now());

        if (userOtp.isUsed()) {
            return new ApiResponse<>(false, "OTP already used", null);
        }

        if (userOtp.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new ApiResponse<>(false, "OTP has expired", null);
        }

        if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
            return new ApiResponse<>(false, "Otp field cannot be empty", null);
        }

        if (!request.getOtp().equals(userOtp.getOtp())) {
            userOtp.setAttemptCount(userOtp.getAttemptCount() + 1);
            userOtpRepository.save(userOtp);
            return new ApiResponse<>(false, "Invalid OTP.", null);
        }

        userOtp.setAttemptCount(0);
        userOtp.setUsed(true);
        userOtp.setLastAttemptTime(null);
        userOtpRepository.save(userOtp);

        return new ApiResponse<>(true, "OTP verified.", null);
    }

    @Transactional
    public ApiResponse<?> resetPassword(ForgotPasswordRequest request, HttpSession session) {
        User user = (User) session.getAttribute("otp_user");

        if (user == null) {
            return new ApiResponse<>(false, "User not found", null);
        }

        if (request.getNewPassword() == null || request.getConfirmPassword() == null
                || request.getNewPassword().trim().isEmpty() || request.getConfirmPassword().trim().isEmpty()) {
            return new ApiResponse<>(false, "Password fields cannot be empty", null);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return new ApiResponse<>(false, "Passwords do not match", null);
        }

        if (!PasswordValidator.isValid(request.getNewPassword())) {
            return new ApiResponse<>(false, "The provided password is too weak.", null);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        String name = user.getFirstname() != null ? user.getFirstname() : "User";
        emailService.sendPasswordResetConfirmation(user.getEmail(), name);

        return new ApiResponse<>(true, "Password reset successful", null);
    }

}