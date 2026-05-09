package com.app.b_and_t_lms.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class UserOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 6)
    private String otp;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int attemptCount = 0;

    @Column(name = "last_attempt_time")
    private LocalDateTime lastAttemptTime;

    private int requestCount = 0;
    private LocalDateTime lastRequestTime;

    public UserOtp() {
    }

    public UserOtp(Long id, User user, String otp, LocalDateTime expiryDate, boolean used, LocalDateTime createdAt,
            int attemptCount, LocalDateTime lastAttemptTime, int requestCount, LocalDateTime lastRequestTime) {
        this.id = id;
        this.user = user;
        this.otp = otp;
        this.expiryDate = expiryDate;
        this.used = used;
        this.createdAt = createdAt;
        this.attemptCount = attemptCount;
        this.lastAttemptTime = lastAttemptTime;
        this.requestCount = requestCount;
        this.lastRequestTime = lastRequestTime;
    }

    
}
