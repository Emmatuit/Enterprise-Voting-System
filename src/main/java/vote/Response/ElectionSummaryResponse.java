package vote.Response;


public class ElectionSummaryResponse {

    private Long electionId;
    private String electionTitle;
    private String organizationName;
    private long totalCandidates;
    private long totalVotes;
    private long totalVoters;
    private long votedCount;
    private double voterTurnoutPercentage;
    private String leadingCandidate;
    private long leadingCandidateVotes;
    private boolean resultsPublished;

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ElectionSummaryResponse response = new ElectionSummaryResponse();

        public Builder electionId(Long electionId) {
            response.electionId = electionId;
            return this;
        }

        public Builder electionTitle(String electionTitle) {
            response.electionTitle = electionTitle;
            return this;
        }

        public Builder organizationName(String organizationName) {
            response.organizationName = organizationName;
            return this;
        }

        public Builder totalCandidates(long totalCandidates) {
            response.totalCandidates = totalCandidates;
            return this;
        }

        public Builder totalVotes(long totalVotes) {
            response.totalVotes = totalVotes;
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

        public Builder voterTurnoutPercentage(double voterTurnoutPercentage) {
            response.voterTurnoutPercentage = voterTurnoutPercentage;
            return this;
        }

        public Builder leadingCandidate(String leadingCandidate) {
            response.leadingCandidate = leadingCandidate;
            return this;
        }

        public Builder leadingCandidateVotes(long leadingCandidateVotes) {
            response.leadingCandidateVotes = leadingCandidateVotes;
            return this;
        }

        public Builder resultsPublished(boolean resultsPublished) {
            response.resultsPublished = resultsPublished;
            return this;
        }

        public ElectionSummaryResponse build() {
            return response;
        }
    }

    // Getters
    public Long getElectionId() { return electionId; }
    public String getElectionTitle() { return electionTitle; }
    public String getOrganizationName() { return organizationName; }
    public long getTotalCandidates() { return totalCandidates; }
    public long getTotalVotes() { return totalVotes; }
    public long getTotalVoters() { return totalVoters; }
    public long getVotedCount() { return votedCount; }
    public double getVoterTurnoutPercentage() { return voterTurnoutPercentage; }
    public String getLeadingCandidate() { return leadingCandidate; }
    public long getLeadingCandidateVotes() { return leadingCandidateVotes; }
    public boolean isResultsPublished() { return resultsPublished; }
}