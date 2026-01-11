package vote.Response;

import java.time.LocalDateTime;

import vote.Enum.OTPChannel;

public class OTPResponse {

    private String identifier;
    private OTPChannel channel;
    private String purpose;
    private LocalDateTime expiresAt;
    private Long voterRegistryId;
    private String message;
    private boolean otpRequired = true;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OTPResponse response = new OTPResponse();

        public Builder identifier(String identifier) {
            response.identifier = identifier;
            return this;
        }

        public Builder channel(OTPChannel channel) {
            response.channel = channel;
            return this;
        }

        public Builder purpose(String purpose) {
            response.purpose = purpose;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            response.expiresAt = expiresAt;
            return this;
        }

        public Builder voterRegistryId(Long voterRegistryId) {
            response.voterRegistryId = voterRegistryId;
            return this;
        }

        public Builder message(String message) {
            response.message = message;
            return this;
        }

        public Builder otpRequired(boolean otpRequired) {
            response.otpRequired = otpRequired;
            return this;
        }

        public OTPResponse build() {
            return response;
        }
    }

    // Getters
    public String getIdentifier() { return identifier; }
    public OTPChannel getChannel() { return channel; }
    public String getPurpose() { return purpose; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public Long getVoterRegistryId() { return voterRegistryId; }
    public String getMessage() { return message; }
    public boolean isOtpRequired() { return otpRequired; }
}