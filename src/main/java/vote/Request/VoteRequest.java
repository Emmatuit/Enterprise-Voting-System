package vote.Request;


import jakarta.validation.constraints.NotNull;

public class VoteRequest {

    @NotNull(message = "Election ID is required")
    private Long electionId;

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotNull(message = "Voter registry ID is required")
    private Long voterRegistryId;

    private boolean anonymous = false;

    private String writeInCandidateName;

    // Getters and Setters
    public Long getElectionId() {
        return electionId;
    }

    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getVoterRegistryId() {
        return voterRegistryId;
    }

    public void setVoterRegistryId(Long voterRegistryId) {
        this.voterRegistryId = voterRegistryId;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getWriteInCandidateName() {
        return writeInCandidateName;
    }

    public void setWriteInCandidateName(String writeInCandidateName) {
        this.writeInCandidateName = writeInCandidateName;
    }
}