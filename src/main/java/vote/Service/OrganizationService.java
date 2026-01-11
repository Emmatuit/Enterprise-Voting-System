package vote.Service;

import java.util.List;

import vote.Request.OrganizationRequest;
import vote.Response.OrganizationResponse;

public interface OrganizationService {

	OrganizationResponse activateOrganization(Long id);

	OrganizationResponse createOrganization(OrganizationRequest request);

	OrganizationResponse deactivateOrganization(Long id);

	void deleteOrganization(Long id);

	boolean existsByCode(String code);

	boolean existsByName(String name);

	List<OrganizationResponse> getAllOrganizations();

	OrganizationResponse getOrganizationByCode(String code);

	OrganizationResponse getOrganizationById(Long id);

	OrganizationResponse updateOrganization(Long id, OrganizationRequest request);
}