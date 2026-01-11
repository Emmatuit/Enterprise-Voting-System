package vote.Response;


import java.time.LocalDateTime;

import vote.Enum.ElectionStatus;

public class ElectionResponse {

    private Long id;
    private Long organizationId;
    private String organizationName;
    private String title;
    private String description;
    private ElectionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean resultsPublished;
    private Integer voterTurnout;
    private Integer totalVoters;
    private Integer maxVotesPerVoter;
    private boolean allowWriteIn;
    private boolean requirePhotoId;
    private boolean isOngoing;
    private long remainingDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ElectionResponse response = new ElectionResponse();

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

        public Builder title(String title) {
            response.title = title;
            return this;
        }

        public Builder description(String description) {
            response.description = description;
            return this;
        }

        public Builder status(ElectionStatus status) {
            response.status = status;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            response.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            response.endTime = endTime;
            return this;
        }

        public Builder resultsPublished(boolean resultsPublished) {
            response.resultsPublished = resultsPublished;
            return this;
        }

        public Builder voterTurnout(Integer voterTurnout) {
            response.voterTurnout = voterTurnout;
            return this;
        }

        public Builder totalVoters(Integer totalVoters) {
            response.totalVoters = totalVoters;
            return this;
        }

        public Builder maxVotesPerVoter(Integer maxVotesPerVoter) {
            response.maxVotesPerVoter = maxVotesPerVoter;
            return this;
        }

        public Builder allowWriteIn(boolean allowWriteIn) {
            response.allowWriteIn = allowWriteIn;
            return this;
        }

        public Builder requirePhotoId(boolean requirePhotoId) {
            response.requirePhotoId = requirePhotoId;
            return this;
        }

        public Builder isOngoing(boolean isOngoing) {
            response.isOngoing = isOngoing;
            return this;
        }

        public Builder remainingDays(long remainingDays) {
            response.remainingDays = remainingDays;
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

        public ElectionResponse build() {
            return response;
        }
    }

    // Getters
    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public String getOrganizationName() { return organizationName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ElectionStatus getStatus() { return status; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isResultsPublished() { return resultsPublished; }
    public Integer getVoterTurnout() { return voterTurnout; }
    public Integer getTotalVoters() { return totalVoters; }
    public Integer getMaxVotesPerVoter() { return maxVotesPerVoter; }
    public boolean isAllowWriteIn() { return allowWriteIn; }
    public boolean isRequirePhotoId() { return requirePhotoId; }
    public boolean isOngoing() { return isOngoing; }
    public long getRemainingDays() { return remainingDays; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
