package vote.Response;

import java.time.LocalDateTime;

public class VoteResponse {

    private Long id;
    private Long electionId;
    private String electionTitle;
    private Long candidateId;
    private String candidateName;
    private Long voterRegistryId;
    private String voterIdentifier;
    private LocalDateTime castAt;
    private String ipAddress;
    private boolean anonymous;
    private String writeInCandidateName;
    private String verificationMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private VoteResponse response = new VoteResponse();

        public Builder id(Long id) {
            response.id = id;
            return this;
        }

        public Builder electionId(Long electionId) {
            response.electionId = electionId;
            return this;
        }

        public Builder electionTitle(String electionTitle) {
            response.electionTitle = electionTitle;
            return this;
        }

        public Builder candidateId(Long candidateId) {
            response.candidateId = candidateId;
            return this;
        }

        public Builder candidateName(String candidateName) {
            response.candidateName = candidateName;
            return this;
        }

        public Builder voterRegistryId(Long voterRegistryId) {
            response.voterRegistryId = voterRegistryId;
            return this;
        }

        public Builder voterIdentifier(String voterIdentifier) {
            response.voterIdentifier = voterIdentifier;
            return this;
        }

        public Builder castAt(LocalDateTime castAt) {
            response.castAt = castAt;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            response.ipAddress = ipAddress;
            return this;
        }

        public Builder anonymous(boolean anonymous) {
            response.anonymous = anonymous;
            return this;
        }

        public Builder writeInCandidateName(String writeInCandidateName) {
            response.writeInCandidateName = writeInCandidateName;
            return this;
        }

        public Builder verificationMethod(String verificationMethod) {
            response.verificationMethod = verificationMethod;
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

        public VoteResponse build() {
            return response;
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getElectionId() { return electionId; }
    public String getElectionTitle() { return electionTitle; }
    public Long getCandidateId() { return candidateId; }
    public String getCandidateName() { return candidateName; }
    public Long getVoterRegistryId() { return voterRegistryId; }
    public String getVoterIdentifier() { return voterIdentifier; }
    public LocalDateTime getCastAt() { return castAt; }
    public String getIpAddress() { return ipAddress; }
    public boolean isAnonymous() { return anonymous; }
    public String getWriteInCandidateName() { return writeInCandidateName; }
    public String getVerificationMethod() { return verificationMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}