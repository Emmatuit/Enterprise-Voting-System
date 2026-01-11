package vote.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vote.Entity.Organization;
import vote.Exception.BusinessRuleException;
import vote.Exception.ResourceNotFoundException;
import vote.Repository.OrganizationRepository;
import vote.Request.OrganizationRequest;
import vote.Response.OrganizationResponse;
import vote.Service.OrganizationService;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

	private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

	private final OrganizationRepository organizationRepository;

	public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	@Override
	public OrganizationResponse activateOrganization(Long id) {
		logger.info("Activating organization ID: {}", id);

		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

		organization.setActive(true);
		Organization activatedOrganization = organizationRepository.save(organization);

		return mapToResponse(activatedOrganization);
	}

	@Override
	public OrganizationResponse createOrganization(OrganizationRequest request) {
		logger.info("Creating organization: {}", request.getName());

		// Check if organization already exists
		if (organizationRepository.existsByName(request.getName())) {
			throw new BusinessRuleException("Organization with name '" + request.getName() + "' already exists");
		}

		// Create new organization
		Organization organization = new Organization();
		organization.setName(request.getName());
		organization.setDescription(request.getDescription());
		organization.setContactEmail(request.getContactEmail());
		organization.setContactPhone(request.getContactPhone());
		organization.setAddress(request.getAddress());
		organization.setActive(true);

		Organization savedOrganization = organizationRepository.save(organization);
		logger.info("Organization created successfully with ID: {}", savedOrganization.getId());

		return mapToResponse(savedOrganization);
	}

	@Override
	public OrganizationResponse deactivateOrganization(Long id) {
		logger.info("Deactivating organization ID: {}", id);

		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

		organization.setActive(false);
		Organization deactivatedOrganization = organizationRepository.save(organization);

		return mapToResponse(deactivatedOrganization);
	}

	@Override
	public void deleteOrganization(Long id) {
		logger.info("Deleting organization ID: {}", id);

		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

		// Soft delete (deactivate) instead of hard delete
		organization.setActive(false);
		organizationRepository.save(organization);
		logger.info("Organization deactivated ID: {}", id);
	}

	@Override
	public boolean existsByCode(String code) {
		return organizationRepository.existsByCode(code);
	}

	@Override
	public boolean existsByName(String name) {
		return organizationRepository.existsByName(name);
	}

	@Override
	public List<OrganizationResponse> getAllOrganizations() {
		logger.debug("Fetching all organizations");

		return organizationRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public OrganizationResponse getOrganizationByCode(String code) {
		logger.debug("Fetching organization by code: {}", code);

		Organization organization = organizationRepository.findByCode(code)
				.orElseThrow(() -> new ResourceNotFoundException("Organization", "code", code));

		return mapToResponse(organization);
	}

	@Override
	public OrganizationResponse getOrganizationById(Long id) {
		logger.debug("Fetching organization by ID: {}", id);

		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

		return mapToResponse(organization);
	}

	// Helper method to map entity to response
	private OrganizationResponse mapToResponse(Organization organization) {
		return OrganizationResponse.builder().id(organization.getId()).name(organization.getName())
				.code(organization.getCode()).description(organization.getDescription())
				.contactEmail(organization.getContactEmail()).contactPhone(organization.getContactPhone())
				.address(organization.getAddress()).active(organization.isActive())
				.createdAt(organization.getCreatedAt()).updatedAt(organization.getUpdatedAt()).build();
	}

	@Override
	public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
		logger.info("Updating organization ID: {}", id);

		Organization organization = organizationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));

		// Check if new name conflicts with existing organization
		if (!organization.getName().equals(request.getName())
				&& organizationRepository.existsByName(request.getName())) {
			throw new BusinessRuleException("Organization with name '" + request.getName() + "' already exists");
		}

		organization.setName(request.getName());
		organization.setDescription(request.getDescription());
		organization.setContactEmail(request.getContactEmail());
		organization.setContactPhone(request.getContactPhone());
		organization.setAddress(request.getAddress());

		Organization updatedOrganization = organizationRepository.save(organization);
		logger.info("Organization updated successfully ID: {}", id);

		return mapToResponse(updatedOrganization);
	}
}
