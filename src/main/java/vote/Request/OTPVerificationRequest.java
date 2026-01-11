package vote.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OTPVerificationRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "OTP code is required")
    private String otpCode;

    @NotNull(message = "Voter registry ID is required")
    private Long voterRegistryId;

    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public Long getVoterRegistryId() {
        return voterRegistryId;
    }

    public void setVoterRegistryId(Long voterRegistryId) {
        this.voterRegistryId = voterRegistryId;
    }
}