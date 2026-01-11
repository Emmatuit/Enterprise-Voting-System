package vote.ServiceImpl;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.Election;
import vote.Entity.Organization;
import vote.Enum.ElectionStatus;
import vote.Exception.BusinessRuleException;
import vote.Exception.ResourceNotFoundException;
import vote.Repository.ElectionRepository;
import vote.Repository.OrganizationRepository;
import vote.Repository.VoteRepository;
import vote.Request.ElectionRequest;
import vote.Response.ElectionResponse;
import vote.Response.ElectionSummaryResponse;
import vote.Service.ElectionService;

@Service
@Transactional
public class ElectionServiceImpl implements ElectionService {

    private static final Logger logger = LoggerFactory.getLogger(ElectionServiceImpl.class);

    private final ElectionRepository electionRepository;
    private final OrganizationRepository organizationRepository;
    private final VoteRepository voteRepository;

    public ElectionServiceImpl(ElectionRepository electionRepository,
                             OrganizationRepository organizationRepository,
                             VoteRepository voteRepository) {
        this.electionRepository = electionRepository;
        this.organizationRepository = organizationRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public ElectionResponse createElection(ElectionRequest request) {
        logger.info("Creating election: {}", request.getTitle());

        // Validate organization
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.getOrganizationId()));

        // Validate dates
        validateElectionDates(request.getStartTime(), request.getEndTime());

        // Create election
        Election election = new Election();
        election.setOrganization(organization);
        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setStartTime(request.getStartTime());
        election.setEndTime(request.getEndTime());
        election.setStatus(ElectionStatus.DRAFT);
        election.setMaxVotesPerVoter(request.getMaxVotesPerVoter());
        election.setAllowWriteIn(request.isAllowWriteIn());
        election.setRequirePhotoId(request.isRequirePhotoId());

        Election savedElection = electionRepository.save(election);
        logger.info("Election created with ID: {}", savedElection.getId());

        return mapToResponse(savedElection);
    }

    @Override
    public ElectionResponse getElectionById(Long id) {
        logger.debug("Getting election by ID: {}", id);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));

        return mapToResponse(election);
    }

    @Override
    public List<ElectionResponse> getElectionsByOrganization(Long organizationId, String status) {
        logger.debug("Getting elections for organization: {}", organizationId);

        // Validate organization exists
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization", "id", organizationId);
        }

        List<Election> elections;
        if (status != null) {
            try {
                ElectionStatus electionStatus = ElectionStatus.valueOf(status.toUpperCase());
                elections = electionRepository.findByOrganizationIdAndStatus(organizationId, electionStatus);
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Invalid election status: " + status);
            }
        } else {
            elections = electionRepository.findByOrganizationId(organizationId);
        }

        return elections.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ElectionResponse> getActiveElections(Long organizationId) {
        logger.debug("Getting active elections for organization: {}", organizationId);

        List<Election> elections = electionRepository.findActiveElectionsByOrganization(
                organizationId, LocalDateTime.now());

        return elections.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ElectionResponse updateElection(Long id, ElectionRequest request) {
        logger.info("Updating election ID: {}", id);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));

        // Check if election can be modified
        if (election.isActive() || election.isCompleted()) {
            throw new BusinessRuleException("Cannot modify an active or completed election");
        }

        // Update organization if changed
        if (!election.getOrganization().getId().equals(request.getOrganizationId())) {
            Organization organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.getOrganizationId()));
            election.setOrganization(organization);
        }

        // Validate dates
        validateElectionDates(request.getStartTime(), request.getEndTime());

        // Update fields
        election.setTitle(request.getTitle());
        election.setDescription(request.getDescription());
        election.setStartTime(request.getStartTime());
        election.setEndTime(request.getEndTime());
        election.setMaxVotesPerVoter(request.getMaxVotesPerVoter());
        election.setAllowWriteIn(request.isAllowWriteIn());
        election.setRequirePhotoId(request.isRequirePhotoId());

        Election updatedElection = electionRepository.save(election);
        logger.info("Election updated ID: {}", id);

        return mapToResponse(updatedElection);
    }

    @Override
    public ElectionResponse activateElection(Long id) {
        logger.info("Activating election ID: {}", id);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));

        if (election.isActive()) {
            throw new BusinessRuleException("Election is already active");
        }

        if (election.isCompleted()) {
            throw new BusinessRuleException("Cannot activate a completed election");
        }

        // Check if start time is in the future
        if (LocalDateTime.now().isBefore(election.getStartTime())) {
            throw new BusinessRuleException("Cannot activate election before its start time");
        }

        // Check if end time is in the past
        if (LocalDateTime.now().isAfter(election.getEndTime())) {
            throw new BusinessRuleException("Election end time has already passed");
        }

        election.setStatus(ElectionStatus.ACTIVE);
        Election activatedElection = electionRepository.save(election);

        logger.info("Election activated ID: {}", id);
        return mapToResponse(activatedElection);
    }

    @Override
    public ElectionResponse pauseElection(Long id) {
        logger.info("Pausing election ID: {}", id);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));

        if (!election.isActive()) {
            throw new BusinessRuleException("Only active elections can be paused");
        }

        election.setStatus(ElectionStatus.PAUSED);
        Election pausedElection = electionRepository.save(election);

        logger.info("Election paused ID: {}", id);
        return mapToResponse(pausedElection);
    }

    @Override
    public ElectionResponse completeElection(Long id) {
        logger.info("Completing election ID: {}", id);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));

        if (election.isCompleted()) {
            throw new BusinessRuleException("Election is already completed");
        }

        // Check if election has ended
        if (LocalDateTime.now().isBefore(election.getEndTime())) {
            throw new BusinessRuleException("Cannot complete election before its end time");
        }

        election.setStatus(ElectionStatus.COMPLETED);
        Election completedElection = electionRepository.save(election);

        // Update voter turnout
        updateVoterTurnout(completedElection);

        logger.info("Election completed ID: {}", id);
        return mapToResponse(completedElection);
    }

    @Override
    public ElectionResponse publishResults(Long id) {
        logger.info("Publishing results for election ID: {}", id);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));

        if (!election.isCompleted()) {
            throw new BusinessRuleException("Only completed elections can have results published");
        }

        if (election.isResultsPublished()) {
            throw new BusinessRuleException("Results are already published");
        }

        election.setResultsPublished(true);
        Election updatedElection = electionRepository.save(election);

        logger.info("Results published for election ID: {}", id);
        return mapToResponse(updatedElection);
    }

    @Override
    public ElectionSummaryResponse getElectionSummary(Long id) {
        logger.debug("Getting summary for election ID: {}", id);

        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));

        long totalCandidates = election.getCandidates().stream()
                .filter(candidate -> candidate.isActive())
                .count();

        long totalVotes = voteRepository.countByElectionId(id);
        long totalVoters = election.getTotalVoters() != null ? election.getTotalVoters() : 0;
        long votedCount = election.getVoterTurnout() != null ?
                (election.getVoterTurnout() * totalVoters / 100) : 0;

        // Find leading candidate
        String leadingCandidate = "None";
        long leadingCandidateVotes = 0;

        for (var candidate : election.getCandidates()) {
            if (candidate.isActive() && candidate.getVoteCount() > leadingCandidateVotes) {
                leadingCandidate = candidate.getName();
                leadingCandidateVotes = candidate.getVoteCount();
            }
        }

        double turnoutPercentage = totalVoters > 0 ?
                ((double) votedCount / totalVoters) * 100 : 0;

        return ElectionSummaryResponse.builder()
                .electionId(election.getId())
                .electionTitle(election.getTitle())
                .organizationName(election.getOrganization().getName())
                .totalCandidates(totalCandidates)
                .totalVotes(totalVotes)
                .totalVoters(totalVoters)
                .votedCount(votedCount)
                .voterTurnoutPercentage(turnoutPercentage)
                .leadingCandidate(leadingCandidate)
                .leadingCandidateVotes(leadingCandidateVotes)
                .resultsPublished(election.isResultsPublished())
                .build();
    }

    @Override
    public boolean isElectionActive(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));

        return election.isOngoing();
    }

    @Override
    public void updateElectionStatuses() {
        logger.info("Updating election statuses");

        LocalDateTime now = LocalDateTime.now();

        // Complete elections that have ended
        List<Election> electionsToComplete = electionRepository.findElectionsToComplete(now);
        for (Election election : electionsToComplete) {
            election.setStatus(ElectionStatus.COMPLETED);
            updateVoterTurnout(election);
            electionRepository.save(election);
            logger.info("Election {} marked as completed", election.getId());
        }

        // Activate elections that have started
        List<Election> activeElections = electionRepository.findAllActiveElections(now);
        for (Election election : activeElections) {
            if (election.isDraft()) {
                election.setStatus(ElectionStatus.ACTIVE);
                electionRepository.save(election);
                logger.info("Election {} activated", election.getId());
            }
        }
    }

    // Private helper methods
    private void validateElectionDates(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw new BusinessRuleException("Start time cannot be in the past");
        }

        if (endTime.isBefore(startTime)) {
            throw new BusinessRuleException("End time cannot be before start time");
        }

        if (endTime.isBefore(now)) {
            throw new BusinessRuleException("End time cannot be in the past");
        }

        // Minimum election duration (1 hour)
        long durationHours = java.time.Duration.between(startTime, endTime).toHours();
        if (durationHours < 1) {
            throw new BusinessRuleException("Election must last at least 1 hour");
        }

        // Maximum election duration (30 days)
        if (durationHours > 720) { // 30 days * 24 hours
            throw new BusinessRuleException("Election cannot last more than 30 days");
        }
    }

    private void updateVoterTurnout(Election election) {
        long totalVoters = election.getTotalVoters() != null ? election.getTotalVoters() : 0;
        long voteCount = voteRepository.countByElectionId(election.getId());

        if (totalVoters > 0) {
            double turnout = (double) voteCount / totalVoters * 100;
            election.setVoterTurnout((int) Math.round(turnout));
        }
    }

    private ElectionResponse mapToResponse(Election election) {
        boolean isOngoing = election.isOngoing();
        long remainingDays = election.getRemainingDays();

        return ElectionResponse.builder()
                .id(election.getId())
                .organizationId(election.getOrganization().getId())
                .organizationName(election.getOrganization().getName())
                .title(election.getTitle())
                .description(election.getDescription())
                .status(election.getStatus())
                .startTime(election.getStartTime())
                .endTime(election.getEndTime())
                .resultsPublished(election.isResultsPublished())
                .voterTurnout(election.getVoterTurnout())
                .totalVoters(election.getTotalVoters())
                .maxVotesPerVoter(election.getMaxVotesPerVoter())
                .allowWriteIn(election.isAllowWriteIn())
                .requirePhotoId(election.isRequirePhotoId())
                .isOngoing(isOngoing)
                .remainingDays(remainingDays)
                .createdAt(election.getCreatedAt())
                .updatedAt(election.getUpdatedAt())
                .build();
    }

	@Override
	public void deleteElection(Long id) {
		// TODO Auto-generated method stub

	}
}