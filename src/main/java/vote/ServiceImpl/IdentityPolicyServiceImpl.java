package vote.ServiceImpl;


import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.IdentityPolicy;
import vote.Entity.Organization;
import vote.Exception.BusinessRuleException;
import vote.Exception.ResourceNotFoundException;
import vote.Repository.IdentityPolicyRepository;
import vote.Repository.OrganizationRepository;
import vote.Request.IdentityPolicyRequest;
import vote.Response.IdentityPolicyResponse;
import vote.Service.IdentityPolicyService;

@Service
@Transactional
public class IdentityPolicyServiceImpl implements IdentityPolicyService {

    private static final Logger logger = LoggerFactory.getLogger(IdentityPolicyServiceImpl.class);

    private final IdentityPolicyRepository identityPolicyRepository;
    private final OrganizationRepository organizationRepository;

    public IdentityPolicyServiceImpl(IdentityPolicyRepository identityPolicyRepository,
                                   OrganizationRepository organizationRepository) {
        this.identityPolicyRepository = identityPolicyRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public IdentityPolicyResponse createPolicy(IdentityPolicyRequest request) {
        logger.info("Creating identity policy for organization: {}", request.getOrganizationId());

        // Validate organization
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.getOrganizationId()));

        // Check if active policy already exists
        identityPolicyRepository.findByOrganizationIdAndActiveTrue(organization.getId())
                .ifPresent(policy -> {
                    if (!policy.isLocked()) {
                        throw new BusinessRuleException("An active policy already exists for this organization. Please deactivate it first.");
                    }
                });

        // Validate identifier fields
        validateIdentifierFields(request.getIdentifierFields());

        // Create new policy
        IdentityPolicy policy = new IdentityPolicy();
        policy.setOrganization(organization);
        policy.setName(request.getName());
        policy.setDescription(request.getDescription());
        policy.setIdentifierFields(request.getIdentifierFields());
        policy.setOtpChannel(request.getOtpChannel());
        policy.setOtpExpiryMinutes(request.getOtpExpiryMinutes());
        policy.setMaxOtpAttempts(request.getMaxOtpAttempts());
        policy.setActive(true);
        policy.setLocked(false);

        IdentityPolicy savedPolicy = identityPolicyRepository.save(policy);
        logger.info("Identity policy created with ID: {}", savedPolicy.getId());

        return mapToResponse(savedPolicy);
    }

    @Override
    public IdentityPolicyResponse getPolicyById(Long id) {
        logger.debug("Getting identity policy by ID: {}", id);

        IdentityPolicy policy = identityPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IdentityPolicy", "id", id));

        return mapToResponse(policy);
    }

    @Override
    public List<IdentityPolicyResponse> getPoliciesByOrganization(Long organizationId) {
        logger.debug("Getting policies for organization: {}", organizationId);

        // Validate organization exists
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization", "id", organizationId);
        }

        return identityPolicyRepository.findByOrganizationId(organizationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IdentityPolicyResponse getActivePolicy(Long organizationId) {
        logger.debug("Getting active policy for organization: {}", organizationId);

        IdentityPolicy policy = identityPolicyRepository.findByOrganizationIdAndActiveTrue(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Active IdentityPolicy", "organizationId", organizationId));

        return mapToResponse(policy);
    }

    @Override
    public IdentityPolicyResponse updatePolicy(Long id, IdentityPolicyRequest request) {
        logger.info("Updating identity policy ID: {}", id);

        IdentityPolicy policy = identityPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IdentityPolicy", "id", id));

        // Check if policy can be modified
        if (policy.isLocked()) {
            throw new BusinessRuleException("Cannot update a locked policy");
        }

        // Validate organization if changed
        if (!policy.getOrganization().getId().equals(request.getOrganizationId())) {
            Organization organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.getOrganizationId()));
            policy.setOrganization(organization);
        }

        // Validate identifier fields
        validateIdentifierFields(request.getIdentifierFields());

        // Update policy
        policy.setName(request.getName());
        policy.setDescription(request.getDescription());
        policy.setIdentifierFields(request.getIdentifierFields());
        policy.setOtpChannel(request.getOtpChannel());
        policy.setOtpExpiryMinutes(request.getOtpExpiryMinutes());
        policy.setMaxOtpAttempts(request.getMaxOtpAttempts());

        IdentityPolicy updatedPolicy = identityPolicyRepository.save(policy);
        logger.info("Identity policy updated ID: {}", id);

        return mapToResponse(updatedPolicy);
    }

    @Override
    public IdentityPolicyResponse lockPolicy(Long id) {
        logger.info("Locking identity policy ID: {}", id);

        IdentityPolicy policy = identityPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IdentityPolicy", "id", id));

        if (policy.isLocked()) {
            throw new BusinessRuleException("Policy is already locked");
        }

        policy.lockPolicy();
        IdentityPolicy lockedPolicy = identityPolicyRepository.save(policy);

        return mapToResponse(lockedPolicy);
    }

    @Override
    public IdentityPolicyResponse activatePolicy(Long id) {
        logger.info("Activating identity policy ID: {}", id);

        IdentityPolicy policy = identityPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IdentityPolicy", "id", id));

        if (policy.isActive()) {
            throw new BusinessRuleException("Policy is already active");
        }

        // Deactivate other active policies for this organization
        identityPolicyRepository.findByOrganizationIdAndActiveTrue(policy.getOrganization().getId())
                .ifPresent(activePolicy -> {
                    activePolicy.setActive(false);
                    identityPolicyRepository.save(activePolicy);
                });

        policy.setActive(true);
        IdentityPolicy activatedPolicy = identityPolicyRepository.save(policy);

        return mapToResponse(activatedPolicy);
    }

    @Override
    public IdentityPolicyResponse deactivatePolicy(Long id) {
        logger.info("Deactivating identity policy ID: {}", id);

        IdentityPolicy policy = identityPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IdentityPolicy", "id", id));

        if (!policy.isActive()) {
            throw new BusinessRuleException("Policy is already inactive");
        }

        policy.setActive(false);
        IdentityPolicy deactivatedPolicy = identityPolicyRepository.save(policy);

        return mapToResponse(deactivatedPolicy);
    }

    @Override
    public void deletePolicy(Long id) {
        logger.info("Deleting identity policy ID: {}", id);

        IdentityPolicy policy = identityPolicyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IdentityPolicy", "id", id));

        if (policy.isLocked()) {
            throw new BusinessRuleException("Cannot delete a locked policy");
        }

        identityPolicyRepository.delete(policy);
    }

    @Override
    public List<String> getVerificationFields(Long organizationId) {
        logger.debug("Getting verification fields for organization: {}", organizationId);

        IdentityPolicy policy = identityPolicyRepository.findByOrganizationIdAndActiveTrue(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Active IdentityPolicy", "organizationId", organizationId));

        return policy.getIdentifierFields().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPolicyLocked(Long organizationId) {
        return identityPolicyRepository.findByOrganizationIdAndActiveTrue(organizationId)
                .map(IdentityPolicy::isLocked)
                .orElse(false);
    }

    // Private helper methods
    private void validateIdentifierFields(java.util.Set<String> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new BusinessRuleException("At least one identifier field is required");
        }

        // Validate allowed fields
        java.util.Set<String> allowedFields = java.util.Set.of("matric_number", "email", "phone");
        for (String field : fields) {
            if (!allowedFields.contains(field)) {
                throw new BusinessRuleException("Invalid identifier field: " + field + ". Allowed fields: matric_number, email, phone");
            }
        }

        // Validate OTP requirements
        if (fields.contains("email") && fields.contains("phone")) {
            throw new BusinessRuleException("Cannot require both email and phone. Choose one or the other.");
        }
    }

    private IdentityPolicyResponse mapToResponse(IdentityPolicy policy) {
        return IdentityPolicyResponse.builder()
                .id(policy.getId())
                .organizationId(policy.getOrganization().getId())
                .organizationName(policy.getOrganization().getName())
                .name(policy.getName())
                .description(policy.getDescription())
                .identifierFields(policy.getIdentifierFields())
                .otpChannel(policy.getOtpChannel())
                .locked(policy.isLocked())
                .active(policy.isActive())
                .otpExpiryMinutes(policy.getOtpExpiryMinutes())
                .maxOtpAttempts(policy.getMaxOtpAttempts())
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}