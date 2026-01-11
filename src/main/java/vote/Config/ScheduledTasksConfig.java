package vote.Config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import vote.Service.ElectionService;
import vote.Service.OTPService;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksConfig.class);

    private final ElectionService electionService;
    private final OTPService otpService;

    public ScheduledTasksConfig(ElectionService electionService, OTPService otpService) {
        this.electionService = electionService;
        this.otpService = otpService;
    }

    // Run every minute to update election statuses
    @Scheduled(fixedDelay = 60000) // 60 seconds
    public void updateElectionStatuses() {
        logger.debug("Running scheduled election status update");
        try {
            electionService.updateElectionStatuses();
        } catch (Exception e) {
            logger.error("Error updating election statuses: {}", e.getMessage(), e);
        }
    }

    // Run every hour to cleanup expired OTPs
    @Scheduled(fixedDelay = 3600000) // 60 minutes
    public void cleanupExpiredOtps() {
        logger.debug("Running scheduled OTP cleanup");
        try {
            otpService.cleanupExpiredOTPs();
        } catch (Exception e) {
            logger.error("Error cleaning up expired OTPs: {}", e.getMessage(), e);
        }
    }

    // Run every day at midnight to cleanup old data
    @Scheduled(cron = "0 0 0 * * *") // Midnight daily
    public void dailyCleanup() {
        logger.info("Running daily cleanup task");
        // Add additional cleanup tasks here
    }
}