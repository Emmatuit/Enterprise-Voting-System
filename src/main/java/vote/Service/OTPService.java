package vote.Service;

import vote.Enum.OTPChannel;
import vote.Request.OTPVerificationRequest;
import vote.Response.OTPResponse;

public interface OTPService {

    OTPResponse generateOTP(String identifier, OTPChannel channel, String purpose, Long organizationId);

    boolean verifyOTP(OTPVerificationRequest request);

    OTPResponse resendOTP(String identifier, String purpose);

    void cleanupExpiredOTPs();

    boolean canRetryOTP(String identifier, String purpose);

    long getRemainingSeconds(String identifier, String purpose);
}