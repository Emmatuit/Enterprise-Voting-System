package vote.Service;



import java.util.List;

import vote.Request.CandidateRequest;
import vote.Response.CandidateResponse;

public interface CandidateService {

    CandidateResponse addCandidate(Long electionId, CandidateRequest request);

    List<CandidateResponse> getCandidatesByElection(Long electionId, boolean activeOnly);

    List<CandidateResponse> getActiveCandidatesByElection(Long electionId);

    CandidateResponse getCandidateById(Long candidateId, Long electionId);

    CandidateResponse updateCandidate(Long candidateId, Long electionId, CandidateRequest request);

    void deleteCandidate(Long candidateId, Long electionId);

    CandidateResponse activateCandidate(Long candidateId, Long electionId);

    CandidateResponse deactivateCandidate(Long candidateId, Long electionId);

    long getCandidateVoteCount(Long candidateId);
}