package vote.Service;

import vote.Request.OTPVerificationRequest;
import vote.Request.VoterVerificationRequest;
import vote.Response.OTPResponse;
import vote.Response.VoterVerificationResponse;

public interface VoterVerificationService {

    VoterVerificationResponse getVerificationFields(Long organizationId);

    OTPResponse verifyVoter(VoterVerificationRequest request);

    VoterVerificationResponse confirmOtp(OTPVerificationRequest request);

    OTPResponse resendOtp(VoterVerificationRequest request);

    boolean isVoterVerified(String identifier, Long organizationId);
}