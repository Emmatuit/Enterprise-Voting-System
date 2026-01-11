package vote.Controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vote.Request.OTPVerificationRequest;
import vote.Request.VoterVerificationRequest;
import vote.Response.ApiResponse;
import vote.Response.OTPResponse;
import vote.Response.VoterVerificationResponse;
import vote.Service.VoterVerificationService;


@RestController
@RequestMapping("/api/voters")
@Tag(name = "Voter Verification", description = "Voter verification and OTP APIs")
public class VoterVerificationController {

    private static final Logger logger = LoggerFactory.getLogger(VoterVerificationController.class);

    private final VoterVerificationService voterVerificationService;

    public VoterVerificationController(VoterVerificationService voterVerificationService) {
        this.voterVerificationService = voterVerificationService;
    }

    @GetMapping("/organizations/{organizationId}/verification-fields")
    @Operation(summary = "Fetch required verification fields",
               description = "Gets required verification fields for organization (public)")
    public ResponseEntity<ApiResponse<VoterVerificationResponse>> getVerificationFields(
            @PathVariable Long organizationId) {

        logger.debug("Getting verification fields for organization: {}", organizationId);
        VoterVerificationResponse response = voterVerificationService.getVerificationFields(organizationId);

        return ResponseEntity.ok(ApiResponse.success(response, "Verification fields retrieved"));
    }

    @PostMapping("/verify")
    @Operation(summary = "Validate voter against registry",
               description = "Validates voter against registry and sends OTP if required (public)")
    public ResponseEntity<ApiResponse<OTPResponse>> verifyVoter(
            @Valid @RequestBody VoterVerificationRequest request) {

        logger.info("Verifying voter for organization: {}", request.getOrganizationId());
        OTPResponse response = voterVerificationService.verifyVoter(request);

        return ResponseEntity.ok(ApiResponse.success(response, "Voter verification completed"));
    }

    @PostMapping("/confirm-otp")
    @Operation(summary = "Confirm OTP",
               description = "Confirms OTP for voter verification (public)")
    public ResponseEntity<ApiResponse<VoterVerificationResponse>> confirmOtp(
            @Valid @RequestBody OTPVerificationRequest request) {

        logger.info("Confirming OTP for identifier: {}", request.getIdentifier());
        VoterVerificationResponse response = voterVerificationService.confirmOtp(request);

        return ResponseEntity.ok(ApiResponse.success(response, "OTP confirmed successfully"));
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Resend OTP",
               description = "Resends OTP for voter verification (public)")
    public ResponseEntity<ApiResponse<OTPResponse>> resendOtp(
            @Valid @RequestBody VoterVerificationRequest request) {

        logger.info("Resending OTP for organization: {}", request.getOrganizationId());
        OTPResponse response = voterVerificationService.resendOtp(request);

        return ResponseEntity.ok(ApiResponse.success(response, "OTP resent successfully"));
    }

    @GetMapping("/verification-status/{identifier}")
    @Operation(summary = "Check verification status",
               description = "Checks verification status for a voter (public)")
    public ResponseEntity<ApiResponse<Boolean>> checkVerificationStatus(
            @PathVariable String identifier,
            @RequestParam Long organizationId) {

        logger.debug("Checking verification status for identifier: {}", identifier);
        boolean isVerified = voterVerificationService.isVoterVerified(identifier, organizationId);

        return ResponseEntity.ok(ApiResponse.success(isVerified, "Verification status checked"));
    }
}