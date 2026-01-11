package vote.ServiceImpl;


import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.Candidate;
import vote.Entity.Election;
import vote.Exception.BusinessRuleException;
import vote.Exception.ResourceNotFoundException;
import vote.Repository.CandidateRepository;
import vote.Repository.ElectionRepository;
import vote.Request.CandidateRequest;
import vote.Response.CandidateResponse;
import vote.Service.CandidateService;

@Service
@Transactional
public class CandidateServiceImpl implements CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateServiceImpl.class);

    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository,
                              ElectionRepository electionRepository) {
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
    }

    @Override
    public CandidateResponse addCandidate(Long electionId, CandidateRequest request) {
        logger.info("Adding candidate to election ID: {}", electionId);

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", electionId));

        // Check if election allows candidate additions
        if (election.isActive() || election.isCompleted()) {
            throw new BusinessRuleException("Cannot add candidates to an active or completed election");
        }

        // Check for duplicate candidate name in same position
        boolean duplicateExists = candidateRepository.findByElectionId(electionId).stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(request.getName())
                        && c.getPosition().equalsIgnoreCase(request.getPosition()));

        if (duplicateExists) {
            throw new BusinessRuleException("A candidate with this name and position already exists in the election");
        }

        Candidate candidate = new Candidate();
        candidate.setElection(election);
        candidate.setName(request.getName());
        candidate.setPosition(request.getPosition());
        candidate.setBio(request.getBio());
        candidate.setPhotoUrl(request.getPhotoUrl());
        candidate.setPartyAffiliation(request.getPartyAffiliation());
        candidate.setActive(true);
        candidate.setWriteIn(false);
        candidate.setVoteCount(0);

        Candidate savedCandidate = candidateRepository.save(candidate);

        return mapToResponse(savedCandidate);
    }

    @Override
    public List<CandidateResponse> getCandidatesByElection(Long electionId, boolean activeOnly) {
        logger.debug("Getting candidates for election ID: {}", electionId);

        if (!electionRepository.existsById(electionId)) {
            throw new ResourceNotFoundException("Election", "id", electionId);
        }

        List<Candidate> candidates;
        if (activeOnly) {
            candidates = candidateRepository.findByElectionIdAndActiveTrue(electionId);
        } else {
            candidates = candidateRepository.findByElectionId(electionId);
        }

        return candidates.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateResponse> getActiveCandidatesByElection(Long electionId) {
        logger.debug("Getting active candidates for election ID: {}", electionId);

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", electionId));

        // Only return candidates if election is active or results are published
        if (!election.isActive() && !election.isResultsPublished()) {
            throw new BusinessRuleException("Election is not active and results are not published");
        }

        return candidateRepository.findByElectionIdAndActiveTrue(electionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse getCandidateById(Long candidateId, Long electionId) {
        logger.debug("Getting candidate ID: {} for election ID: {}", candidateId, electionId);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));

        // Verify candidate belongs to specified election
        if (!candidate.getElection().getId().equals(electionId)) {
            throw new BusinessRuleException("Candidate does not belong to the specified election");
        }

        return mapToResponse(candidate);
    }

    @Override
    public CandidateResponse updateCandidate(Long candidateId, Long electionId, CandidateRequest request) {
        logger.info("Updating candidate ID: {}", candidateId);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));

        // Verify candidate belongs to specified election
        if (!candidate.getElection().getId().equals(electionId)) {
            throw new BusinessRuleException("Candidate does not belong to the specified election");
        }

        Election election = candidate.getElection();

        // Check if election allows candidate updates
        if (election.isActive() || election.isCompleted()) {
            throw new BusinessRuleException("Cannot update candidates in an active or completed election");
        }

        // Check for duplicate candidate name in same position (excluding current candidate)
        boolean duplicateExists = candidateRepository.findByElectionId(electionId).stream()
                .filter(c -> !c.getId().equals(candidateId))
                .anyMatch(c -> c.getName().equalsIgnoreCase(request.getName())
                        && c.getPosition().equalsIgnoreCase(request.getPosition()));

        if (duplicateExists) {
            throw new BusinessRuleException("Another candidate with this name and position already exists");
        }

        // Update candidate
        candidate.setName(request.getName());
        candidate.setPosition(request.getPosition());
        candidate.setBio(request.getBio());
        candidate.setPhotoUrl(request.getPhotoUrl());
        candidate.setPartyAffiliation(request.getPartyAffiliation());

        Candidate updatedCandidate = candidateRepository.save(candidate);

        return mapToResponse(updatedCandidate);
    }

    @Override
    public CandidateResponse activateCandidate(Long candidateId, Long electionId) {
        logger.info("Activating candidate ID: {}", candidateId);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));

        // Verify candidate belongs to specified election
        if (!candidate.getElection().getId().equals(electionId)) {
            throw new BusinessRuleException("Candidate does not belong to the specified election");
        }

        if (candidate.isActive()) {
            throw new BusinessRuleException("Candidate is already active");
        }

        candidate.setActive(true);
        Candidate activatedCandidate = candidateRepository.save(candidate);

        return mapToResponse(activatedCandidate);
    }

    @Override
    public CandidateResponse deactivateCandidate(Long candidateId, Long electionId) {
        logger.info("Deactivating candidate ID: {}", candidateId);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));

        // Verify candidate belongs to specified election
        if (!candidate.getElection().getId().equals(electionId)) {
            throw new BusinessRuleException("Candidate does not belong to the specified election");
        }

        if (!candidate.isActive()) {
            throw new BusinessRuleException("Candidate is already inactive");
        }

        candidate.setActive(false);
        Candidate deactivatedCandidate = candidateRepository.save(candidate);

        return mapToResponse(deactivatedCandidate);
    }

    @Override
    public long getCandidateVoteCount(Long candidateId) {
        return candidateRepository.findById(candidateId)
                .map(Candidate::getVoteCount)
                .orElse((int) 0L);
    }

    private CandidateResponse mapToResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .electionId(candidate.getElection().getId())
                .electionTitle(candidate.getElection().getTitle())
                .name(candidate.getName())
                .position(candidate.getPosition())
                .bio(candidate.getBio())
                .photoUrl(candidate.getPhotoUrl())
                .partyAffiliation(candidate.getPartyAffiliation())
                .active(candidate.isActive())
                .writeIn(candidate.isWriteIn())
                .voteCount(candidate.getVoteCount())
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .build();
    }

	@Override
	public void deleteCandidate(Long candidateId, Long electionId) {
		// TODO Auto-generated method stub

	}
}
