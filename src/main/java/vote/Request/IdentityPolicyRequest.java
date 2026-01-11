package vote.Request;


import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import vote.Enum.OTPChannel;

public class IdentityPolicyRequest {

    @NotNull(message = "Organization ID is required")
    private Long organizationId;

    @NotBlank(message = "Policy name is required")
    @Size(min = 3, max = 100, message = "Policy name must be between 3 and 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Identifier fields are required")
    @Size(min = 1, message = "At least one identifier field is required")
    private Set<String> identifierFields;

    @NotNull(message = "OTP channel is required")
    private OTPChannel otpChannel;

    private Integer otpExpiryMinutes = 5;

    private Integer maxOtpAttempts = 3;

    // Getters and Setters
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getIdentifierFields() {
        return identifierFields;
    }

    public void setIdentifierFields(Set<String> identifierFields) {
        this.identifierFields = identifierFields;
    }

    public OTPChannel getOtpChannel() {
        return otpChannel;
    }

    public void setOtpChannel(OTPChannel otpChannel) {
        this.otpChannel = otpChannel;
    }

    public Integer getOtpExpiryMinutes() {
        return otpExpiryMinutes;
    }

    public void setOtpExpiryMinutes(Integer otpExpiryMinutes) {
        this.otpExpiryMinutes = otpExpiryMinutes;
    }

    public Integer getMaxOtpAttempts() {
        return maxOtpAttempts;
    }

    public void setMaxOtpAttempts(Integer maxOtpAttempts) {
        this.maxOtpAttempts = maxOtpAttempts;
    }
}