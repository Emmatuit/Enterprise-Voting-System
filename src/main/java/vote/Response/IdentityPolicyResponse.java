package vote.Response;


import java.time.LocalDateTime;
import java.util.Set;

import vote.Enum.OTPChannel;

public class IdentityPolicyResponse {

    private Long id;
    private Long organizationId;
    private String organizationName;
    private String name;
    private String description;
    private Set<String> identifierFields;
    private OTPChannel otpChannel;
    private boolean locked;
    private boolean active;
    private Integer otpExpiryMinutes;
    private Integer maxOtpAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private IdentityPolicyResponse response = new IdentityPolicyResponse();

        public Builder id(Long id) {
            response.id = id;
            return this;
        }

        public Builder organizationId(Long organizationId) {
            response.organizationId = organizationId;
            return this;
        }

        public Builder organizationName(String organizationName) {
            response.organizationName = organizationName;
            return this;
        }

        public Builder name(String name) {
            response.name = name;
            return this;
        }

        public Builder description(String description) {
            response.description = description;
            return this;
        }

        public Builder identifierFields(Set<String> identifierFields) {
            response.identifierFields = identifierFields;
            return this;
        }

        public Builder otpChannel(OTPChannel otpChannel) {
            response.otpChannel = otpChannel;
            return this;
        }

        public Builder locked(boolean locked) {
            response.locked = locked;
            return this;
        }

        public Builder active(boolean active) {
            response.active = active;
            return this;
        }

        public Builder otpExpiryMinutes(Integer otpExpiryMinutes) {
            response.otpExpiryMinutes = otpExpiryMinutes;
            return this;
        }

        public Builder maxOtpAttempts(Integer maxOtpAttempts) {
            response.maxOtpAttempts = maxOtpAttempts;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            response.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            response.updatedAt = updatedAt;
            return this;
        }

        public IdentityPolicyResponse build() {
            return response;
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public String getOrganizationName() { return organizationName; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Set<String> getIdentifierFields() { return identifierFields; }
    public OTPChannel getOtpChannel() { return otpChannel; }
    public boolean isLocked() { return locked; }
    public boolean isActive() { return active; }
    public Integer getOtpExpiryMinutes() { return otpExpiryMinutes; }
    public Integer getMaxOtpAttempts() { return maxOtpAttempts; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}