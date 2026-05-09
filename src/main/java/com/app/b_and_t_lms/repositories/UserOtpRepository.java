package com.app.b_and_t_lms.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.app.b_and_t_lms.models.UserOtp;

public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {

    // Find valid OTP for user
    Optional<UserOtp> findByUserIdAndOtpAndUsedFalseAndExpiryDateAfter(Long userId, String otp, LocalDateTime now);

    // Delete expired OTPs (for cleanup job)
    @Modifying
    @Transactional
    @Query("DELETE FROM UserOtp u WHERE u.expiryDate < :now")
    void deleteAllExpired(@Param("now") LocalDateTime now);

    Optional<UserOtp> findByUserId(Long id);
}