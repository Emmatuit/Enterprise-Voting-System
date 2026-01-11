package vote.ServiceImpl;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.IdentityPolicy;
import vote.Entity.OTPCode;
import vote.Entity.Organization;
import vote.Entity.VoterRegistry;
import vote.Enum.OTPChannel;
import vote.Exception.BusinessRuleException;
import vote.Exception.ResourceNotFoundException;
import vote.Repository.IdentityPolicyRepository;
import vote.Repository.OTPCodeRepository;
import vote.Repository.OrganizationRepository;
import vote.Repository.VoterRegistryRepository;
import vote.Request.OTPVerificationRequest;
import vote.Request.VoterVerificationRequest;
import vote.Response.OTPResponse;
import vote.Response.VoterVerificationResponse;
import vote.Service.OTPService;
import vote.Service.VoterVerificationService;

@Service
@Transactional
public class VoterVerificationServiceImpl implements VoterVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VoterVerificationServiceImpl.class);

    private final VoterRegistryRepository voterRegistryRepository;
    private final IdentityPolicyRepository identityPolicyRepository;
    private final OrganizationRepository organizationRepository;
    private final OTPService otpService;
    private final OTPCodeRepository otpCodeRepository;

    public VoterVerificationServiceImpl(VoterRegistryRepository voterRegistryRepository,
                                      IdentityPolicyRepository identityPolicyRepository,
                                      OrganizationRepository organizationRepository,
                                      OTPService otpService,
                                      OTPCodeRepository otpCodeRepository) {
        this.voterRegistryRepository = voterRegistryRepository;
        this.identityPolicyRepository = identityPolicyRepository;
        this.organizationRepository = organizationRepository;
        this.otpService = otpService;
        this.otpCodeRepository = otpCodeRepository;
    }

    @Override
    public VoterVerificationResponse getVerificationFields(Long organizationId) {
        logger.debug("Getting verification fields for organization: {}", organizationId);

        // Validate organization
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        // Get active identity policy
        IdentityPolicy policy = identityPolicyRepository.findByOrganizationIdAndActiveTrue(organizationId)
                .orElseThrow(() -> new BusinessRuleException("No active identity policy found for organization"));

        // Build response
        return VoterVerificationResponse.builder()
                .organizationId(organizationId)
                .organizationName(organization.getName())
                .requiredFields(new ArrayList<>(policy.getIdentifierFields()))
                .otpRequired(policy.requiresOtp())
                .otpChannel(policy.getOtpChannel())
                .policyLocked(policy.isLocked())
                .build();
    }

    @Override
    public OTPResponse verifyVoter(VoterVerificationRequest request) {
        logger.info("Verifying voter for organization: {}", request.getOrganizationId());

        // Validate organization
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.getOrganizationId()));

        // Get active identity policy
        IdentityPolicy policy = identityPolicyRepository.findByOrganizationIdAndActiveTrue(request.getOrganizationId())
                .orElseThrow(() -> new BusinessRuleException("No active identity policy found for organization"));

        // Validate required fields based on policy
        Map<String, String> providedFields = extractProvidedFields(request);
        validateRequiredFields(policy, providedFields);

        // Find voter in registry
        Optional<VoterRegistry> voterOptional = findVoterInRegistry(request, policy);

        if (!voterOptional.isPresent()) {
            logger.warn("Voter not found in registry for organization: {}", request.getOrganizationId());
            throw new BusinessRuleException("Voter not found in registry or already voted");
        }

        VoterRegistry voter = voterOptional.get();

        // Check if voter has already voted
        if (voter.isUsed()) {
            throw new BusinessRuleException("Voter has already cast a vote");
        }

        // Check verification attempts
        if (voter.getVerificationAttempts() >= 5) {
            throw new BusinessRuleException("Maximum verification attempts exceeded. Please contact administrator.");
        }

        // Increment verification attempts
        voter.incrementVerificationAttempts();
        voterRegistryRepository.save(voter);

        // If OTP is required, generate and send it
        if (policy.requiresOtp()) {
            String identifier = getOtpIdentifier(request, policy);
            OTPChannel channel = policy.getOtpChannel();

            OTPResponse otpResponse = otpService.generateOTP(
                    identifier,
                    channel,
                    "VOTER_VERIFICATION",
                    organization.getId());

            // Set voter registry ID in response for later reference
            return OTPResponse.builder()
                    .identifier(identifier)
                    .channel(channel)
                    .purpose("VOTER_VERIFICATION")
                    .expiresAt(otpResponse.getExpiresAt())
                    .voterRegistryId(voter.getId())
                    .message("OTP sent successfully")
                    .build();
        }

        // If no OTP required, verification is complete
        return OTPResponse.builder()
                .voterRegistryId(voter.getId())
                .message("Verification successful. No OTP required.")
                .otpRequired(false)
                .build();
    }

    @Override
    public VoterVerificationResponse confirmOtp(OTPVerificationRequest request) {
        logger.info("Confirming OTP for identifier: {}", request.getIdentifier());

        // Verify OTP
        boolean isValid = otpService.verifyOTP(request);

        if (!isValid) {
            throw new BusinessRuleException("Invalid or expired OTP");
        }

        // Get voter registry entry
        VoterRegistry voter = voterRegistryRepository.findById(request.getVoterRegistryId())
                .orElseThrow(() -> new ResourceNotFoundException("VoterRegistry", "id", request.getVoterRegistryId()));

        // Reset verification attempts on successful OTP
        voter.resetVerificationAttempts();
        voterRegistryRepository.save(voter);

        // Get organization and policy for response
        Organization organization = voter.getOrganization();
        IdentityPolicy policy = identityPolicyRepository.findByOrganizationIdAndActiveTrue(organization.getId())
                .orElseThrow(() -> new BusinessRuleException("No active identity policy found"));

        return VoterVerificationResponse.builder()
                .organizationId(organization.getId())
                .organizationName(organization.getName())
                .voterRegistryId(voter.getId())
                .verified(true)
                .verificationMethod("OTP")
                .verificationTime(LocalDateTime.now())
                .otpChannel(policy.getOtpChannel())
                .message("OTP verified successfully")
                .build();
    }

    @Override
    public OTPResponse resendOtp(VoterVerificationRequest request) {
        logger.info("Resending OTP for organization: {}", request.getOrganizationId());

        // Get active identity policy
        IdentityPolicy policy = identityPolicyRepository.findByOrganizationIdAndActiveTrue(request.getOrganizationId())
                .orElseThrow(() -> new BusinessRuleException("No active identity policy found for organization"));

        if (!policy.requiresOtp()) {
            throw new BusinessRuleException("OTP is not required for this organization");
        }

        // Find voter to get identifier
        Optional<VoterRegistry> voterOptional = findVoterInRegistry(request, policy);

        if (!voterOptional.isPresent()) {
            throw new BusinessRuleException("Voter not found in registry");
        }

        VoterRegistry voter = voterOptional.get();
        String identifier = getOtpIdentifier(request, policy);

        // Resend OTP
        OTPResponse otpResponse = otpService.resendOTP(identifier, "VOTER_VERIFICATION");

        return OTPResponse.builder()
                .identifier(identifier)
                .channel(otpResponse.getChannel())
                .purpose("VOTER_VERIFICATION")
                .expiresAt(otpResponse.getExpiresAt())
                .voterRegistryId(voter.getId())
                .message("OTP resent successfully")
                .build();
    }

    @Override
    public boolean isVoterVerified(String identifier, Long organizationId) {
        logger.debug("Checking verification status for identifier: {}", identifier);

        // Check if there's a valid, used OTP for this identifier
        LocalDateTime now = LocalDateTime.now();
        List<OTPCode> validOtps = otpCodeRepository.findValidOTPs(identifier, "VOTER_VERIFICATION", now);

        return validOtps.stream()
                .anyMatch(otp -> otp.isUsed() &&
                        (otp.getOrganization() == null ||
                         otp.getOrganization().getId().equals(organizationId)));
    }

    // Private helper methods
    private Map<String, String> extractProvidedFields(VoterVerificationRequest request) {
        Map<String, String> fields = new HashMap<>();

        if (request.getMatricNumber() != null && !request.getMatricNumber().trim().isEmpty()) {
            fields.put("matric_number", request.getMatricNumber().trim());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            fields.put("email", request.getEmail().trim().toLowerCase());
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            fields.put("phone", request.getPhone().trim());
        }

        return fields;
    }

    private void validateRequiredFields(IdentityPolicy policy, Map<String, String> providedFields) {
        Set<String> requiredFields = policy.getIdentifierFields();

        for (String requiredField : requiredFields) {
            if (!providedFields.containsKey(requiredField)) {
                throw new BusinessRuleException("Missing required field: " + requiredField);
            }
        }

        // Validate that only required fields are provided (no extra fields)
        for (String providedField : providedFields.keySet()) {
            if (!requiredFields.contains(providedField)) {
                throw new BusinessRuleException("Extra field provided: " + providedField);
            }
        }
    }

    private Optional<VoterRegistry> findVoterInRegistry(VoterVerificationRequest request, IdentityPolicy policy) {
        Set<String> requiredFields = policy.getIdentifierFields();

        // Build query based on policy requirements
        String matricNumber = requiredFields.contains("matric_number") ? request.getMatricNumber() : null;
        String email = requiredFields.contains("email") ? request.getEmail() : null;
        String phone = requiredFields.contains("phone") ? request.getPhone() : null;

        return voterRegistryRepository.findEligibleVoter(
                request.getOrganizationId(),
                matricNumber,
                email,
                phone
        );
    }

    private String getOtpIdentifier(VoterVerificationRequest request, IdentityPolicy policy) {
        if (policy.requiresEmail()) {
            return request.getEmail();
        } else if (policy.requiresPhone()) {
            return request.getPhone();
        } else {
            throw new BusinessRuleException("OTP channel requires email or phone, but neither is configured");
        }
    }
}
