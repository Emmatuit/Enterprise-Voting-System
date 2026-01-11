package vote.Response;

public class VoterRegistrySummaryResponse {

    private Long organizationId;
    private String organizationName;
    private String organizationCode;
    private long totalVoters;
    private long votedCount;
    private long remainingVoters;
    private double voterTurnoutPercentage;
    private int lockedVoters; // Voters with too many verification attempts
    private int activeElections;
    private int completedElections;
    private long totalElections;
    private String lastUpdated;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private VoterRegistrySummaryResponse response = new VoterRegistrySummaryResponse();

        public Builder organizationId(Long organizationId) {
            response.organizationId = organizationId;
            return this;
        }

        public Builder organizationName(String organizationName) {
            response.organizationName = organizationName;
            return this;
        }

        public Builder organizationCode(String organizationCode) {
            response.organizationCode = organizationCode;
            return this;
        }

        public Builder totalVoters(long totalVoters) {
            response.totalVoters = totalVoters;
            return this;
        }

        public Builder votedCount(long votedCount) {
            response.votedCount = votedCount;
            return this;
        }

        public Builder remainingVoters(long remainingVoters) {
            response.remainingVoters = remainingVoters;
            return this;
        }

        public Builder voterTurnoutPercentage(double voterTurnoutPercentage) {
            response.voterTurnoutPercentage = voterTurnoutPercentage;
            return this;
        }

        public Builder lockedVoters(int lockedVoters) {
            response.lockedVoters = lockedVoters;
            return this;
        }

        public Builder activeElections(int activeElections) {
            response.activeElections = activeElections;
            return this;
        }

        public Builder completedElections(int completedElections) {
            response.completedElections = completedElections;
            return this;
        }

        public Builder totalElections(long totalElections) {
            response.totalElections = totalElections;
            return this;
        }

        public Builder lastUpdated(String lastUpdated) {
            response.lastUpdated = lastUpdated;
            return this;
        }

        public VoterRegistrySummaryResponse build() {
            return response;
        }
    }

    // Getters
    public Long getOrganizationId() {
        return organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public long getTotalVoters() {
        return totalVoters;
    }

    public long getVotedCount() {
        return votedCount;
    }

    public long getRemainingVoters() {
        return remainingVoters;
    }

    public double getVoterTurnoutPercentage() {
        return voterTurnoutPercentage;
    }

    public int getLockedVoters() {
        return lockedVoters;
    }

    public int getActiveElections() {
        return activeElections;
    }

    public int getCompletedElections() {
        return completedElections;
    }

    public long getTotalElections() {
        return totalElections;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    // Setters
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public void setTotalVoters(long totalVoters) {
        this.totalVoters = totalVoters;
    }

    public void setVotedCount(long votedCount) {
        this.votedCount = votedCount;
    }

    public void setRemainingVoters(long remainingVoters) {
        this.remainingVoters = remainingVoters;
    }

    public void setVoterTurnoutPercentage(double voterTurnoutPercentage) {
        this.voterTurnoutPercentage = voterTurnoutPercentage;
    }

    public void setLockedVoters(int lockedVoters) {
        this.lockedVoters = lockedVoters;
    }

    public void setActiveElections(int activeElections) {
        this.activeElections = activeElections;
    }

    public void setCompletedElections(int completedElections) {
        this.completedElections = completedElections;
    }

    public void setTotalElections(long totalElections) {
        this.totalElections = totalElections;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Additional convenience methods
    public boolean hasActiveElections() {
        return activeElections > 0;
    }

    public double getRemainingPercentage() {
        if (totalVoters == 0) {
			return 0.0;
		}
        return ((double) remainingVoters / totalVoters) * 100;
    }

    public String getStatus() {
        if (voterTurnoutPercentage >= 70) {
            return "HIGH_TURNOUT";
        } else if (voterTurnoutPercentage >= 40) {
            return "MODERATE_TURNOUT";
        } else if (voterTurnoutPercentage > 0) {
            return "LOW_TURNOUT";
        } else {
            return "NO_VOTES";
        }
    }

    @Override
    public String toString() {
        return "VoterRegistrySummaryResponse{" +
                "organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", totalVoters=" + totalVoters +
                ", votedCount=" + votedCount +
                ", voterTurnoutPercentage=" + voterTurnoutPercentage +
                '}';
    }
}