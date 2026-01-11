package vote.Response;


import java.time.LocalDateTime;

public class VoterRegistryResponse {

    private Long id;
    private Long organizationId;
    private String organizationName;
    private String matricNumber;
    private String email;
    private String phone;
    private String fullName;
    private boolean used;
    private LocalDateTime votedAt;
    private int verificationAttempts;
    private LocalDateTime lastVerificationAttempt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private VoterRegistryResponse response = new VoterRegistryResponse();

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

        public Builder matricNumber(String matricNumber) {
            response.matricNumber = matricNumber;
            return this;
        }

        public Builder email(String email) {
            response.email = email;
            return this;
        }

        public Builder phone(String phone) {
            response.phone = phone;
            return this;
        }

        public Builder fullName(String fullName) {
            response.fullName = fullName;
            return this;
        }

        public Builder used(boolean used) {
            response.used = used;
            return this;
        }

        public Builder votedAt(LocalDateTime votedAt) {
            response.votedAt = votedAt;
            return this;
        }

        public Builder verificationAttempts(int verificationAttempts) {
            response.verificationAttempts = verificationAttempts;
            return this;
        }

        public Builder lastVerificationAttempt(LocalDateTime lastVerificationAttempt) {
            response.lastVerificationAttempt = lastVerificationAttempt;
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

        public VoterRegistryResponse build() {
            return response;
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public String getOrganizationName() { return organizationName; }
    public String getMatricNumber() { return matricNumber; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getFullName() { return fullName; }
    public boolean isUsed() { return used; }
    public LocalDateTime getVotedAt() { return votedAt; }
    public int getVerificationAttempts() { return verificationAttempts; }
    public LocalDateTime getLastVerificationAttempt() { return lastVerificationAttempt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}