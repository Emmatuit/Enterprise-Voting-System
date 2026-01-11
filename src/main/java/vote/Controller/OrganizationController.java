package vote.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vote.Request.OrganizationRequest;
import vote.Response.ApiResponse;
import vote.Response.OrganizationResponse;
import vote.Service.OrganizationService;

@RestController
@RequestMapping("/api/admin/organizations")
@Tag(name = "Organization Management", description = "Organization management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create organization", description = "Creates a new organization")
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody OrganizationRequest request) {

        logger.info("Creating organization: {}", request.getName());
        OrganizationResponse response = organizationService.createOrganization(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Organization created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get organization by ID", description = "Retrieves organization details by ID")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(@PathVariable Long id) {

        logger.debug("Getting organization by ID: {}", id);
        OrganizationResponse response = organizationService.getOrganizationById(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Organization retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get organization by code", description = "Retrieves organization details by code")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganizationByCode(@PathVariable String code) {

        logger.debug("Getting organization by code: {}", code);
        OrganizationResponse response = organizationService.getOrganizationByCode(code);

        return ResponseEntity.ok(ApiResponse.success(response, "Organization retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get all organizations", description = "Retrieves all organizations")
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getAllOrganizations() {

        logger.debug("Getting all organizations");
        List<OrganizationResponse> responses = organizationService.getAllOrganizations();

        return ResponseEntity.ok(ApiResponse.success(responses, "Organizations retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Update organization", description = "Updates organization details")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request) {

        logger.info("Updating organization ID: {}", id);
        OrganizationResponse response = organizationService.updateOrganization(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Organization updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete organization", description = "Deletes an organization (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(@PathVariable Long id) {

        logger.info("Deleting organization ID: {}", id);
        organizationService.deleteOrganization(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Organization deleted successfully"));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Activate organization", description = "Activates a deactivated organization")
    public ResponseEntity<ApiResponse<OrganizationResponse>> activateOrganization(@PathVariable Long id) {

        logger.info("Activating organization ID: {}", id);
        OrganizationResponse response = organizationService.activateOrganization(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Organization activated successfully"));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Deactivate organization", description = "Deactivates an organization")
    public ResponseEntity<ApiResponse<OrganizationResponse>> deactivateOrganization(@PathVariable Long id) {

        logger.info("Deactivating organization ID: {}", id);
        OrganizationResponse response = organizationService.deactivateOrganization(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Organization deactivated successfully"));
    }

    @GetMapping("/exists/name/{name}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Check organization name exists", description = "Checks if organization name exists")
    public ResponseEntity<ApiResponse<Boolean>> checkOrganizationNameExists(@PathVariable String name) {

        logger.debug("Checking if organization name exists: {}", name);
        boolean exists = organizationService.existsByName(name);

        return ResponseEntity.ok(ApiResponse.success(exists, "Check completed"));
    }

    @GetMapping("/exists/code/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Check organization code exists", description = "Checks if organization code exists")
    public ResponseEntity<ApiResponse<Boolean>> checkOrganizationCodeExists(@PathVariable String code) {

        logger.debug("Checking if organization code exists: {}", code);
        boolean exists = organizationService.existsByCode(code);

        return ResponseEntity.ok(ApiResponse.success(exists, "Check completed"));
    }
}