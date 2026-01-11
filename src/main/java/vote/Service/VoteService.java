package vote.Service;



import java.util.List;

import vote.Request.VoteRequest;
import vote.Response.VoteResponse;

public interface VoteService {

    VoteResponse castVote(VoteRequest request, String ipAddress, String userAgent);

    List<VoteResponse> getVotesByElection(Long electionId);

    List<VoteResponse> getVotesByVoter(Long voterRegistryId);

    boolean hasVoted(Long electionId, Long voterRegistryId);

    long getElectionVoteCount(Long electionId);

    long getCandidateVoteCount(Long candidateId);

    VoteResponse getVoteById(Long voteId);
}