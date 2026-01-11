package vote.Response;


import java.time.LocalDateTime;
import java.util.List;

import vote.Enum.OTPChannel;

public class VoterVerificationResponse {

    private Long organizationId;
    private String organizationName;
    private List<String> requiredFields;
    private boolean otpRequired;
    private OTPChannel otpChannel;
    private boolean policyLocked;
    private Long voterRegistryId;
    private boolean verified;
    private String verificationMethod;
    private LocalDateTime verificationTime;
    private String message;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private VoterVerificationResponse response = new VoterVerificationResponse();

        public Builder organizationId(Long organizationId) {
            response.organizationId = organizationId;
            return this;
        }

        public Builder organizationName(String organizationName) {
            response.organizationName = organizationName;
            return this;
        }

        public Builder requiredFields(List<String> requiredFields) {
            response.requiredFields = requiredFields;
            return this;
        }

        public Builder otpRequired(boolean otpRequired) {
            response.otpRequired = otpRequired;
            return this;
        }

        public Builder otpChannel(OTPChannel otpChannel) {
            response.otpChannel = otpChannel;
            return this;
        }

        public Builder policyLocked(boolean policyLocked) {
            response.policyLocked = policyLocked;
            return this;
        }

        public Builder voterRegistryId(Long voterRegistryId) {
            response.voterRegistryId = voterRegistryId;
            return this;
        }

        public Builder verified(boolean verified) {
            response.verified = verified;
            return this;
        }

        public Builder verificationMethod(String verificationMethod) {
            response.verificationMethod = verificationMethod;
            return this;
        }

        public Builder verificationTime(LocalDateTime verificationTime) {
            response.verificationTime = verificationTime;
            return this;
        }

        public Builder message(String message) {
            response.message = message;
            return this;
        }

        public VoterVerificationResponse build() {
            return response;
        }
    }

    // Getters
    public Long getOrganizationId() { return organizationId; }
    public String getOrganizationName() { return organizationName; }
    public List<String> getRequiredFields() { return requiredFields; }
    public boolean isOtpRequired() { return otpRequired; }
    public OTPChannel getOtpChannel() { return otpChannel; }
    public boolean isPolicyLocked() { return policyLocked; }
    public Long getVoterRegistryId() { return voterRegistryId; }
    public boolean isVerified() { return verified; }
    public String getVerificationMethod() { return verificationMethod; }
    public LocalDateTime getVerificationTime() { return verificationTime; }
    public String getMessage() { return message; }
}
