package vote.ServiceImpl;


import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.OTPCode;
import vote.Entity.Organization;
import vote.Enum.OTPChannel;
import vote.Exception.ResourceNotFoundException;
import vote.Repository.OTPCodeRepository;
import vote.Repository.OrganizationRepository;
import vote.Request.OTPVerificationRequest;
import vote.Response.OTPResponse;
import vote.Service.OTPService;
import vote.Util.OTPGenerator;

@Service
@Transactional
public class OTPServiceImpl implements OTPService {

    private static final Logger logger = LoggerFactory.getLogger(OTPServiceImpl.class);

    private final OTPCodeRepository otpCodeRepository;
    private final OrganizationRepository organizationRepository;
    private final OTPGenerator otpGenerator;

    public OTPServiceImpl(OTPCodeRepository otpCodeRepository,
                         OrganizationRepository organizationRepository,
                         OTPGenerator otpGenerator) {
        this.otpCodeRepository = otpCodeRepository;
        this.organizationRepository = organizationRepository;
        this.otpGenerator = otpGenerator;
    }

    @Override
    public OTPResponse generateOTP(String identifier, OTPChannel channel, String purpose, Long organizationId) {
        logger.info("Generating OTP for identifier: {}, channel: {}, purpose: {}",
                   identifier, channel, purpose);

        // Validate organization if provided
        Organization organization = null;
        if (organizationId != null) {
            organization = organizationRepository.findById(organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));
        }

        // Invalidate previous OTPs for same identifier and purpose
        otpCodeRepository.invalidateAllOTPs(identifier, purpose);

        // Generate OTP
        String otpCode = otpGenerator.generateOTP();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5); // 5 minutes expiry

        // Create OTP record
        OTPCode otp = new OTPCode();
        otp.setIdentifier(identifier);
        otp.setCode(otpCode);
        otp.setChannel(channel.name());
        otp.setPurpose(purpose);
        otp.setMaxAttempts(3);
        otp.setExpiresAt(expiresAt);
        otp.setOrganization(organization);

        OTPCode savedOtp = otpCodeRepository.save(otp);

        logger.info("OTP generated for identifier: {}", identifier);

        return OTPResponse.builder()
                .identifier(identifier)
                .channel(channel)
                .purpose(purpose)
                .expiresAt(expiresAt)
                .build();
    }

    @Override
    public boolean verifyOTP(OTPVerificationRequest request) {
        logger.info("Verifying OTP for identifier: {}", request.getIdentifier());

        Optional<OTPCode> otpOptional = otpCodeRepository.findByIdentifierAndCodeAndUsedFalse(
                request.getIdentifier(), request.getOtpCode());

        if (!otpOptional.isPresent()) {
            logger.warn("Invalid OTP for identifier: {}", request.getIdentifier());
            return false;
        }

        OTPCode otp = otpOptional.get();

        // Check if OTP is expired
        if (otp.isExpired()) {
            logger.warn("Expired OTP for identifier: {}", request.getIdentifier());
            return false;
        }

        // Check if max attempts exceeded
        if (otp.getAttempts() >= otp.getMaxAttempts()) {
            logger.warn("Max OTP attempts exceeded for identifier: {}", request.getIdentifier());
            return false;
        }

        // Verify OTP
        boolean isValid = otp.verify(request.getOtpCode());

        if (isValid) {
            otpCodeRepository.save(otp);
            logger.info("OTP verified successfully for identifier: {}", request.getIdentifier());
        } else {
            otpCodeRepository.save(otp); // Save incremented attempts
            logger.warn("Invalid OTP attempt for identifier: {}", request.getIdentifier());
        }

        return isValid;
    }

    @Override
    public OTPResponse resendOTP(String identifier, String purpose) {
        logger.info("Resending OTP for identifier: {}, purpose: {}", identifier, purpose);

        // Find latest valid OTP
        LocalDateTime now = LocalDateTime.now();
        var validOtps = otpCodeRepository.findValidOTPs(identifier, purpose, now);

        if (!validOtps.isEmpty()) {
            // Use existing OTP if still valid
            OTPCode existingOtp = validOtps.get(0);

            return OTPResponse.builder()
                    .identifier(identifier)
                    .channel(OTPChannel.valueOf(existingOtp.getChannel()))
                    .purpose(purpose)
                    .expiresAt(existingOtp.getExpiresAt())
                    .message("Previous OTP is still valid")
                    .build();
        }

        // Generate new OTP
        // Get channel from last OTP or default to EMAIL
        String channel = "EMAIL";
        Optional<OTPCode> lastOtp = otpCodeRepository.findByIdentifierAndPurposeAndUsedFalse(identifier, purpose)
                .stream()
                .findFirst();

        if (lastOtp.isPresent()) {
            channel = lastOtp.get().getChannel();
        }

        return generateOTP(identifier, OTPChannel.valueOf(channel), purpose, null);
    }

    @Override
    public void cleanupExpiredOTPs() {
        logger.info("Cleaning up expired OTPs");

        LocalDateTime expiryDate = LocalDateTime.now().minusHours(24); // Clean up OTPs older than 24 hours
        otpCodeRepository.deleteExpiredOTPs(expiryDate);

        logger.info("Expired OTPs cleanup completed");
    }

    @Override
    public boolean canRetryOTP(String identifier, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        var validOtps = otpCodeRepository.findValidOTPs(identifier, purpose, now);

        if (validOtps.isEmpty()) {
            return false;
        }

        OTPCode otp = validOtps.get(0);
        return otp.canRetry();
    }

    @Override
    public long getRemainingSeconds(String identifier, String purpose) {
        LocalDateTime now = LocalDateTime.now();
        var validOtps = otpCodeRepository.findValidOTPs(identifier, purpose, now);

        if (validOtps.isEmpty()) {
            return 0;
        }

        OTPCode otp = validOtps.get(0);
        return otp.getRemainingSeconds();
    }
}