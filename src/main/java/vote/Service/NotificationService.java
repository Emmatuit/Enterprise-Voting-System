package vote.Service;

import vote.Enum.OTPChannel;

public interface NotificationService {

    void sendOTP(String identifier, OTPChannel channel, String otpCode);

    void sendVoteConfirmation(String identifier, OTPChannel channel, String electionTitle);
}