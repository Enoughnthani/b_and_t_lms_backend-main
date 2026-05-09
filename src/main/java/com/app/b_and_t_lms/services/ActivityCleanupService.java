package com.app.b_and_t_lms.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.b_and_t_lms.repositories.ActivityRepository;

@Service
@EnableScheduling
public class ActivityCleanupService {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupOldActivities() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
        int deletedCount = activityRepository.deleteByCreatedAtBefore(cutoffDate);
        System.out.println("Deleted " + deletedCount + " activities older than 90 days");
    }
}