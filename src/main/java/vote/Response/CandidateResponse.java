package vote.Response;

import java.time.LocalDateTime;

public class CandidateResponse {

    private Long id;
    private Long electionId;
    private String electionTitle;
    private String name;
    private String position;
    private String bio;
    private String photoUrl;
    private String partyAffiliation;
    private boolean active;
    private boolean writeIn;
    private Integer voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CandidateResponse response = new CandidateResponse();

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

        public Builder name(String name) {
            response.name = name;
            return this;
        }

        public Builder position(String position) {
            response.position = position;
            return this;
        }

        public Builder bio(String bio) {
            response.bio = bio;
            return this;
        }

        public Builder photoUrl(String photoUrl) {
            response.photoUrl = photoUrl;
            return this;
        }

        public Builder partyAffiliation(String partyAffiliation) {
            response.partyAffiliation = partyAffiliation;
            return this;
        }

        public Builder active(boolean active) {
            response.active = active;
            return this;
        }

        public Builder writeIn(boolean writeIn) {
            response.writeIn = writeIn;
            return this;
        }

        public Builder voteCount(Integer voteCount) {
            response.voteCount = voteCount;
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

        public CandidateResponse build() {
            return response;
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getElectionId() { return electionId; }
    public String getElectionTitle() { return electionTitle; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getBio() { return bio; }
    public String getPhotoUrl() { return photoUrl; }
    public String getPartyAffiliation() { return partyAffiliation; }
    public boolean isActive() { return active; }
    public boolean isWriteIn() { return writeIn; }
    public Integer getVoteCount() { return voteCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}