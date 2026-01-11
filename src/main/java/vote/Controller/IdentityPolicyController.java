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
import vote.Request.IdentityPolicyRequest;
import vote.Response.ApiResponse;
import vote.Response.IdentityPolicyResponse;
import vote.Service.IdentityPolicyService;

@RestController
@RequestMapping("/api/admin/identity-policy")
@Tag(name = "Identity Policy", description = "Identity policy configuration APIs")
@SecurityRequirement(name = "bearerAuth")
public class IdentityPolicyController {

    private static final Logger logger = LoggerFactory.getLogger(IdentityPolicyController.class);

    private final IdentityPolicyService identityPolicyService;

    public IdentityPolicyController(IdentityPolicyService identityPolicyService) {
        this.identityPolicyService = identityPolicyService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Create identity policy", description = "Creates identity validation rules for an organization")
    public ResponseEntity<ApiResponse<IdentityPolicyResponse>> createPolicy(
            @Valid @RequestBody IdentityPolicyRequest request) {

        logger.info("Creating identity policy for organization: {}", request.getOrganizationId());
        IdentityPolicyResponse response = identityPolicyService.createPolicy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Identity policy created successfully"));
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get organization policies", description = "Gets all identity policies for an organization")
    public ResponseEntity<ApiResponse<List<IdentityPolicyResponse>>> getOrganizationPolicies(
            @PathVariable Long organizationId) {

        logger.debug("Getting identity policies for organization: {}", organizationId);
        List<IdentityPolicyResponse> responses = identityPolicyService.getPoliciesByOrganization(organizationId);

        return ResponseEntity.ok(ApiResponse.success(responses, "Policies retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get policy by ID", description = "Gets identity policy by ID")
    public ResponseEntity<ApiResponse<IdentityPolicyResponse>> getPolicy(@PathVariable Long id) {

        logger.debug("Getting identity policy by ID: {}", id);
        IdentityPolicyResponse response = identityPolicyService.getPolicyById(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Policy retrieved successfully"));
    }

    @GetMapping("/organization/{organizationId}/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Get active policy", description = "Gets active identity policy for an organization")
    public ResponseEntity<ApiResponse<IdentityPolicyResponse>> getActivePolicy(
            @PathVariable Long organizationId) {

        logger.debug("Getting active policy for organization: {}", organizationId);
        IdentityPolicyResponse response = identityPolicyService.getActivePolicy(organizationId);

        return ResponseEntity.ok(ApiResponse.success(response, "Active policy retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Update policy", description = "Updates identity policy")
    public ResponseEntity<ApiResponse<IdentityPolicyResponse>> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody IdentityPolicyRequest request) {

        logger.info("Updating identity policy ID: {}", id);
        IdentityPolicyResponse response = identityPolicyService.updatePolicy(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Policy updated successfully"));
    }

    @PostMapping("/{id}/lock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Lock policy", description = "Locks identity policy to prevent changes during election")
    public ResponseEntity<ApiResponse<IdentityPolicyResponse>> lockPolicy(@PathVariable Long id) {

        logger.info("Locking identity policy ID: {}", id);
        IdentityPolicyResponse response = identityPolicyService.lockPolicy(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Policy locked successfully"));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Activate policy", description = "Activates an identity policy")
    public ResponseEntity<ApiResponse<IdentityPolicyResponse>> activatePolicy(@PathVariable Long id) {

        logger.info("Activating identity policy ID: {}", id);
        IdentityPolicyResponse response = identityPolicyService.activatePolicy(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Policy activated successfully"));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Deactivate policy", description = "Deactivates an identity policy")
    public ResponseEntity<ApiResponse<IdentityPolicyResponse>> deactivatePolicy(@PathVariable Long id) {

        logger.info("Deactivating identity policy ID: {}", id);
        IdentityPolicyResponse response = identityPolicyService.deactivatePolicy(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Policy deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Delete policy", description = "Deletes an identity policy")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(@PathVariable Long id) {

        logger.info("Deleting identity policy ID: {}", id);
        identityPolicyService.deletePolicy(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Policy deleted successfully"));
    }

    @GetMapping("/organization/{organizationId}/verification-fields")
    @Operation(summary = "Get verification fields", description = "Gets required verification fields for organization (public)")
    public ResponseEntity<ApiResponse<List<String>>> getVerificationFields(@PathVariable Long organizationId) {

        logger.debug("Getting verification fields for organization: {}", organizationId);
        List<String> fields = identityPolicyService.getVerificationFields(organizationId);

        return ResponseEntity.ok(ApiResponse.success(fields, "Verification fields retrieved successfully"));
    }
}