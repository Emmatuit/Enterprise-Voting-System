package vote.ServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import vote.Enum.OTPChannel;
import vote.Service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void sendOTP(String identifier, OTPChannel channel, String otpCode) {
        logger.info("Sending OTP via {} to {}: {}", channel, identifier, otpCode);

        // In a real implementation, integrate with:
        // - Email service (SendGrid, AWS SES, etc.)
        // - SMS service (Twilio, AWS SNS, etc.)

        switch (channel) {
            case EMAIL:
                sendEmailOTP(identifier, otpCode);
                break;
            case SMS:
                sendSMSOTP(identifier, otpCode);
                break;
            case NONE:
                logger.debug("No OTP channel configured");
                break;
        }
    }

    private void sendEmailOTP(String email, String otpCode) {
        // Mock email sending
        logger.info("[MOCK] Email OTP sent to {}: {}", email, otpCode);
        // Implement actual email sending logic here
    }

    private void sendSMSOTP(String phoneNumber, String otpCode) {
        // Mock SMS sending
        logger.info("[MOCK] SMS OTP sent to {}: {}", phoneNumber, otpCode);
        // Implement actual SMS sending logic here
    }

    @Override
    public void sendVoteConfirmation(String identifier, OTPChannel channel, String electionTitle) {
        logger.info("Sending vote confirmation via {} to {} for election: {}",
                   channel, identifier, electionTitle);

        switch (channel) {
            case EMAIL:
                sendEmailConfirmation(identifier, electionTitle);
                break;
            case SMS:
                sendSMSConfirmation(identifier, electionTitle);
                break;
            default:
                logger.debug("No confirmation sent for channel: {}", channel);
        }
    }

    private void sendEmailConfirmation(String email, String electionTitle) {
        logger.info("[MOCK] Vote confirmation email sent to {} for election: {}",
                   email, electionTitle);
    }

    private void sendSMSConfirmation(String phoneNumber, String electionTitle) {
        logger.info("[MOCK] Vote confirmation SMS sent to {} for election: {}",
                   phoneNumber, electionTitle);
    }
}