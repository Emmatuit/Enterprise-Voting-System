package vote.ServiceImpl;


import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vote.Entity.Organization;
import vote.Entity.VoterRegistry;
import vote.Enum.ElectionStatus;
import vote.Exception.BusinessRuleException;
import vote.Exception.ResourceNotFoundException;
import vote.Repository.ElectionRepository;
import vote.Repository.OrganizationRepository;
import vote.Repository.VoterRegistryRepository;
import vote.Request.VoterRegistryRequest;
import vote.Response.VoterRegistryResponse;
import vote.Response.VoterRegistrySummaryResponse;
import vote.Service.VoterRegistryService;
import vote.Util.CSVProcessor;

@Service
@Transactional
public class VoterRegistryServiceImpl implements VoterRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(VoterRegistryServiceImpl.class);

    private final VoterRegistryRepository voterRegistryRepository;
    private final OrganizationRepository organizationRepository;
    private final ElectionRepository electionRepository;
    private final CSVProcessor csvProcessor;

    public VoterRegistryServiceImpl(VoterRegistryRepository voterRegistryRepository,
                                  OrganizationRepository organizationRepository,
                                  ElectionRepository electionRepository,
                                  CSVProcessor csvProcessor) {
        this.voterRegistryRepository = voterRegistryRepository;
        this.organizationRepository = organizationRepository;
        this.electionRepository = electionRepository;
        this.csvProcessor = csvProcessor;
    }

    @Override
    public VoterRegistrySummaryResponse processRegistryFile(MultipartFile file, Long organizationId, boolean overwrite) {
        logger.info("Processing registry file for organization: {}", organizationId);

        // Validate organization
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        // Process CSV file
        List<VoterRegistry> voters = csvProcessor.processVoterRegistryFile(file, organization);

        if (overwrite) {
            // Delete existing voters
            List<VoterRegistry> existingVoters = voterRegistryRepository.findByOrganizationId(organizationId);
            voterRegistryRepository.deleteAll(existingVoters);
            logger.info("Deleted {} existing voters for organization: {}", existingVoters.size(), organizationId);
        }

        // Save new voters
        List<VoterRegistry> savedVoters = voterRegistryRepository.saveAll(voters);

        // Create summary
        return createSummary(organization, savedVoters.size(), 0);
    }

    @Override
    public VoterRegistryResponse addVoter(VoterRegistryRequest request) {
        logger.info("Adding voter to organization: {}", request.getOrganizationId());

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.getOrganizationId()));

        // Validate at least one identifier is provided
        if (request.getMatricNumber() == null && request.getEmail() == null && request.getPhone() == null) {
            throw new BusinessRuleException("At least one identifier (matric number, email, or phone) is required");
        }

        // Check for duplicates
        checkForDuplicates(organization.getId(), request.getMatricNumber(), request.getEmail(), request.getPhone());

        VoterRegistry voter = new VoterRegistry();
        voter.setOrganization(organization);
        voter.setMatricNumber(request.getMatricNumber());
        voter.setEmail(request.getEmail());
        voter.setPhone(request.getPhone());
        voter.setFullName(request.getFullName());
        voter.setUsed(false);

        VoterRegistry savedVoter = voterRegistryRepository.save(voter);

        return mapToResponse(savedVoter);
    }

    @Override
    public VoterRegistryResponse getVoterById(Long id) {
        VoterRegistry voter = voterRegistryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VoterRegistry", "id", id));

        return mapToResponse(voter);
    }

    @Override
    public List<VoterRegistryResponse> getVotersByOrganization(Long organizationId, Boolean used) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization", "id", organizationId);
        }

        List<VoterRegistry> voters;
        if (used == null) {
            voters = voterRegistryRepository.findByOrganizationId(organizationId);
        } else if (used) {
            voters = voterRegistryRepository.findByOrganizationId(organizationId).stream()
                    .filter(VoterRegistry::isUsed)
                    .collect(Collectors.toList());
        } else {
            voters = voterRegistryRepository.findByOrganizationIdAndUsedFalse(organizationId);
        }

        return voters.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VoterRegistryResponse updateVoter(Long id, VoterRegistryRequest request) {
        logger.info("Updating voter ID: {}", id);

        VoterRegistry voter = voterRegistryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VoterRegistry", "id", id));

        // Validate organization if changed
        if (!voter.getOrganization().getId().equals(request.getOrganizationId())) {
            Organization organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.getOrganizationId()));
            voter.setOrganization(organization);
        }

        // Check for duplicates (excluding current voter)
        checkForDuplicatesExcludingCurrent(voter.getId(), voter.getOrganization().getId(),
                request.getMatricNumber(), request.getEmail(), request.getPhone());

        // Update fields
        voter.setMatricNumber(request.getMatricNumber());
        voter.setEmail(request.getEmail());
        voter.setPhone(request.getPhone());
        voter.setFullName(request.getFullName());

        VoterRegistry updatedVoter = voterRegistryRepository.save(voter);

        return mapToResponse(updatedVoter);
    }

    @Override
    public void deleteVoter(Long id) {
        logger.info("Deleting voter ID: {}", id);

        VoterRegistry voter = voterRegistryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VoterRegistry", "id", id));

        // Check if voter has already voted
        if (voter.isUsed()) {
            throw new BusinessRuleException("Cannot delete a voter who has already voted");
        }

        voterRegistryRepository.delete(voter);
    }

    @Override
    public VoterRegistrySummaryResponse getRegistrySummary(Long organizationId) {
        logger.debug("Getting registry summary for organization: {}", organizationId);

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        long totalVoters = voterRegistryRepository.countByOrganizationId(organizationId);
        long votedCount = voterRegistryRepository.countByOrganizationIdAndUsedTrue(organizationId);
        long remainingVoters = totalVoters - votedCount;
        double turnoutPercentage = totalVoters > 0 ? (double) votedCount / totalVoters * 100 : 0;

        // Get locked voters (exceeded verification attempts)
        int lockedVoters = voterRegistryRepository.findLockedVoters(organizationId).size();

        // Get election statistics
        long totalElections = electionRepository.countByOrganizationId(organizationId);
        int activeElections = electionRepository.findByOrganizationIdAndStatus(organizationId, ElectionStatus.ACTIVE).size();
        int completedElections = electionRepository.findByOrganizationIdAndStatus(organizationId, ElectionStatus.COMPLETED).size();

        return VoterRegistrySummaryResponse.builder()
                .organizationId(organizationId)
                .organizationName(organization.getName())
                .organizationCode(organization.getCode())
                .totalVoters(totalVoters)
                .votedCount(votedCount)
                .remainingVoters(remainingVoters)
                .voterTurnoutPercentage(Math.round(turnoutPercentage * 100.0) / 100.0) // Round to 2 decimal places
                .lockedVoters(lockedVoters)
                .activeElections(activeElections)
                .completedElections(completedElections)
                .totalElections(totalElections)
                .lastUpdated(java.time.LocalDateTime.now().toString())
                .build();
    }

    @Override
    public VoterRegistryResponse markVoterAsVoted(Long id) {
        logger.info("Marking voter as voted ID: {}", id);

        VoterRegistry voter = voterRegistryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VoterRegistry", "id", id));

        if (voter.isUsed()) {
            throw new BusinessRuleException("Voter has already been marked as voted");
        }

        voter.markAsVoted();
        VoterRegistry updatedVoter = voterRegistryRepository.save(voter);

        return mapToResponse(updatedVoter);
    }

    @Override
    public VoterRegistryResponse resetVoterStatus(Long id) {
        logger.info("Resetting voter status ID: {}", id);

        VoterRegistry voter = voterRegistryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VoterRegistry", "id", id));

        voter.setUsed(false);
        voter.setVotedAt(null);
        voter.setVerificationAttempts(0);
        voter.setLastVerificationAttempt(null);

        VoterRegistry updatedVoter = voterRegistryRepository.save(voter);

        return mapToResponse(updatedVoter);
    }

    @Override
    public List<VoterRegistryResponse> searchVoters(Long organizationId, String matricNumber,
                                                   String email, String phone, String fullName) {
        logger.debug("Searching voters in organization: {}", organizationId);

        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization", "id", organizationId);
        }

        List<VoterRegistry> voters = voterRegistryRepository.findByOrganizationId(organizationId);

        // Apply filters
        return voters.stream()
                .filter(voter -> matricNumber == null || matricNumber.isEmpty() ||
                        (voter.getMatricNumber() != null && voter.getMatricNumber().contains(matricNumber)))
                .filter(voter -> email == null || email.isEmpty() ||
                        (voter.getEmail() != null && voter.getEmail().contains(email)))
                .filter(voter -> phone == null || phone.isEmpty() ||
                        (voter.getPhone() != null && voter.getPhone().contains(phone)))
                .filter(voter -> fullName == null || fullName.isEmpty() ||
                        (voter.getFullName() != null && voter.getFullName().contains(fullName)))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isVoterEligible(Long organizationId, String matricNumber, String email, String phone) {
        return voterRegistryRepository.findEligibleVoter(organizationId, matricNumber, email, phone)
                .map(voter -> !voter.isUsed() && voter.getVerificationAttempts() < 5)
                .orElse(false);
    }

    // Private helper methods
    private void checkForDuplicates(Long organizationId, String matricNumber, String email, String phone) {
        if (matricNumber != null && voterRegistryRepository.findByOrganizationIdAndMatricNumber(organizationId, matricNumber).isPresent()) {
            throw new BusinessRuleException("Voter with matric number '" + matricNumber + "' already exists");
        }
        if (email != null && voterRegistryRepository.findByOrganizationIdAndEmail(organizationId, email).isPresent()) {
            throw new BusinessRuleException("Voter with email '" + email + "' already exists");
        }
        if (phone != null && voterRegistryRepository.findByOrganizationIdAndPhone(organizationId, phone).isPresent()) {
            throw new BusinessRuleException("Voter with phone '" + phone + "' already exists");
        }
    }

    private void checkForDuplicatesExcludingCurrent(Long voterId, Long organizationId,
                                                   String matricNumber, String email, String phone) {
        if (matricNumber != null) {
            voterRegistryRepository.findByOrganizationIdAndMatricNumber(organizationId, matricNumber)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(voterId)) {
                            throw new BusinessRuleException("Another voter with matric number '" + matricNumber + "' already exists");
                        }
                    });
        }

        if (email != null) {
            voterRegistryRepository.findByOrganizationIdAndEmail(organizationId, email)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(voterId)) {
                            throw new BusinessRuleException("Another voter with email '" + email + "' already exists");
                        }
                    });
        }

        if (phone != null) {
            voterRegistryRepository.findByOrganizationIdAndPhone(organizationId, phone)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(voterId)) {
                            throw new BusinessRuleException("Another voter with phone '" + phone + "' already exists");
                        }
                    });
        }
    }

    private VoterRegistrySummaryResponse createSummary(Organization organization, int totalVoters, int votedCount) {
        double turnoutPercentage = totalVoters > 0 ? (double) votedCount / totalVoters * 100 : 0;

        return VoterRegistrySummaryResponse.builder()
                .organizationId(organization.getId())
                .organizationName(organization.getName())
                .organizationCode(organization.getCode())
                .totalVoters(totalVoters)
                .votedCount(votedCount)
                .remainingVoters(totalVoters - votedCount)
                .voterTurnoutPercentage(Math.round(turnoutPercentage * 100.0) / 100.0)
                .lastUpdated(java.time.LocalDateTime.now().toString())
                .build();
    }

    private VoterRegistryResponse mapToResponse(VoterRegistry voter) {
        return VoterRegistryResponse.builder()
                .id(voter.getId())
                .organizationId(voter.getOrganization().getId())
                .organizationName(voter.getOrganization().getName())
                .matricNumber(voter.getMatricNumber())
                .email(voter.getEmail())
                .phone(voter.getPhone())
                .fullName(voter.getFullName())
                .used(voter.isUsed())
                .votedAt(voter.getVotedAt())
                .verificationAttempts(voter.getVerificationAttempts())
                .lastVerificationAttempt(voter.getLastVerificationAttempt())
                .createdAt(voter.getCreatedAt())
                .updatedAt(voter.getUpdatedAt())
                .build();
    }
}