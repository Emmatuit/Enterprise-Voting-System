package vote.ServiceImpl;


import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.Candidate;
import vote.Entity.Election;
import vote.Entity.IdentityPolicy;
import vote.Entity.Vote;
import vote.Entity.VoterRegistry;
import vote.Exception.BusinessRuleException;
import vote.Exception.ResourceNotFoundException;
import vote.Repository.CandidateRepository;
import vote.Repository.ElectionRepository;
import vote.Repository.IdentityPolicyRepository;
import vote.Repository.VoteRepository;
import vote.Repository.VoterRegistryRepository;
import vote.Request.VoteRequest;
import vote.Response.VoteResponse;
import vote.Service.VoteService;

@Service
@Transactional
public class VoteServiceImpl implements VoteService {

    private static final Logger logger = LoggerFactory.getLogger(VoteServiceImpl.class);

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoterRegistryRepository voterRegistryRepository;
    private final IdentityPolicyRepository identityPolicyRepository;

    public VoteServiceImpl(VoteRepository voteRepository,
                         ElectionRepository electionRepository,
                         CandidateRepository candidateRepository,
                         VoterRegistryRepository voterRegistryRepository,
                         IdentityPolicyRepository identityPolicyRepository) {
        this.voteRepository = voteRepository;
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
        this.voterRegistryRepository = voterRegistryRepository;
        this.identityPolicyRepository = identityPolicyRepository;
    }

    @Override
    public VoteResponse castVote(VoteRequest request, String ipAddress, String userAgent) {
        logger.info("Processing vote for election ID: {}", request.getElectionId());

        // Validate election
        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", request.getElectionId()));

        // Check if election is active
        if (!election.isActive() || !election.isOngoing()) {
            throw new BusinessRuleException("Election is not active or has ended");
        }

        // Validate candidate
        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", request.getCandidateId()));

        // Verify candidate belongs to election
        if (!candidate.getElection().getId().equals(election.getId())) {
            throw new BusinessRuleException("Candidate does not belong to this election");
        }

        // Verify candidate is active
        if (!candidate.isActive()) {
            throw new BusinessRuleException("Candidate is not active");
        }

        // Validate voter registry entry
        VoterRegistry voter = voterRegistryRepository.findById(request.getVoterRegistryId())
                .orElseThrow(() -> new ResourceNotFoundException("VoterRegistry", "id", request.getVoterRegistryId()));

        // Verify voter belongs to election organization
        if (!voter.getOrganization().getId().equals(election.getOrganization().getId())) {
            throw new BusinessRuleException("Voter does not belong to this election's organization");
        }

        // Check if voter has already voted
        if (voteRepository.existsByElectionIdAndVoterRegistryId(election.getId(), voter.getId())) {
            throw new BusinessRuleException("Voter has already cast a vote in this election");
        }

        // Check if voter is marked as used
        if (voter.isUsed()) {
            throw new BusinessRuleException("Voter has already voted in another election");
        }

        // Check voter verification attempts
        if (voter.getVerificationAttempts() >= 5) {
            throw new BusinessRuleException("Voter has exceeded maximum verification attempts");
        }

        // Check identity policy requirements
        IdentityPolicy policy = identityPolicyRepository.findByOrganizationIdAndActiveTrue(election.getOrganization().getId())
                .orElseThrow(() -> new BusinessRuleException("No active identity policy found for organization"));

        if (policy.isLocked()) {
            // For locked policies, require OTP verification
            // This would be checked by the verification service before voting
            logger.debug("Identity policy is locked, OTP verification required");
        }

        // Create vote
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setCandidate(candidate);
        vote.setVoterRegistry(voter);
        vote.setIpAddress(ipAddress);
        vote.setUserAgent(userAgent);
        vote.setAnonymous(request.isAnonymous());
        vote.setVerificationMethod("OTP"); // Default verification method

        // Handle write-in candidates
        if (candidate.isWriteIn() && request.getWriteInCandidateName() != null) {
            vote.setWriteInCandidateName(request.getWriteInCandidateName());
        }

        Vote savedVote = voteRepository.save(vote);

        // Update candidate vote count
        candidate.incrementVoteCount();
        candidateRepository.save(candidate);

        // Mark voter as used
        voter.markAsVoted();
        voterRegistryRepository.save(voter);

        // Update election voter turnout
        updateElectionTurnout(election);

        logger.info("Vote cast successfully for election ID: {}", election.getId());

        return mapToResponse(savedVote);
    }

    @Override
    public List<VoteResponse> getVotesByElection(Long electionId) {
        logger.debug("Getting votes for election ID: {}", electionId);

        if (!electionRepository.existsById(electionId)) {
            throw new ResourceNotFoundException("Election", "id", electionId);
        }

        return voteRepository.findByElectionId(electionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoteResponse> getVotesByVoter(Long voterRegistryId) {
        logger.debug("Getting votes for voter registry ID: {}", voterRegistryId);

        if (!voterRegistryRepository.existsById(voterRegistryId)) {
            throw new ResourceNotFoundException("VoterRegistry", "id", voterRegistryId);
        }

        return voteRepository.findByVoterRegistryId(voterRegistryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasVoted(Long electionId, Long voterRegistryId) {
        return voteRepository.existsByElectionIdAndVoterRegistryId(electionId, voterRegistryId);
    }

    @Override
    public long getElectionVoteCount(Long electionId) {
        return voteRepository.countByElectionId(electionId);
    }

    @Override
    public long getCandidateVoteCount(Long candidateId) {
        return voteRepository.countByCandidateId(candidateId);
    }

    @Override
    public VoteResponse getVoteById(Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote", "id", voteId));

        return mapToResponse(vote);
    }

    // Private helper methods
    private void updateElectionTurnout(Election election) {
        long totalVotes = voteRepository.countByElectionId(election.getId());
        long totalVoters = election.getTotalVoters() != null ? election.getTotalVoters() : 0;

        if (totalVoters > 0) {
            double turnout = (double) totalVotes / totalVoters * 100;
            election.setVoterTurnout((int) Math.round(turnout));
            electionRepository.save(election);
        }
    }

    private VoteResponse mapToResponse(Vote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .electionId(vote.getElection().getId())
                .electionTitle(vote.getElection().getTitle())
                .candidateId(vote.getCandidate().getId())
                .candidateName(vote.getCandidate().getName())
                .voterRegistryId(vote.getVoterRegistry().getId())
                .voterIdentifier(vote.getVoterIdentifier())
                .castAt(vote.getCastAt())
                .ipAddress(vote.getIpAddress())
                .anonymous(vote.isAnonymous())
                .writeInCandidateName(vote.getWriteInCandidateName())
                .verificationMethod(vote.getVerificationMethod())
                .createdAt(vote.getCreatedAt())
                .updatedAt(vote.getUpdatedAt())
                .build();
    }
}