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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vote.Request.VoterRegistryRequest;
import vote.Response.ApiResponse;
import vote.Response.VoterRegistryResponse;
import vote.Response.VoterRegistrySummaryResponse;
import vote.Service.VoterRegistryService;

@RestController
@RequestMapping("/api/admin/voter-registry")
@Tag(name = "Voter Registry", description = "Voter registry management APIs")
@SecurityRequirement(name = "bearerAuth")
public class VoterRegistryController {

    private static final Logger logger = LoggerFactory.getLogger(VoterRegistryController.class);

    private final VoterRegistryService voterRegistryService;

    public VoterRegistryController(VoterRegistryService voterRegistryService) {
        this.voterRegistryService = voterRegistryService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Upload voter registry file",
               description = "Uploads CSV/Excel file containing eligible voters")
    public ResponseEntity<ApiResponse<VoterRegistrySummaryResponse>> uploadRegistryFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("organizationId") Long organizationId,
            @RequestParam(value = "overwrite", defaultValue = "false") boolean overwrite) {

        logger.info("Uploading voter registry file for organization: {}", organizationId);
        VoterRegistrySummaryResponse response = voterRegistryService.processRegistryFile(file, organizationId, overwrite);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Voter registry uploaded successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Add voter to registry", description = "Manually adds a voter to the registry")
    public ResponseEntity<ApiResponse<VoterRegistryResponse>> addVoter(
            @Valid @RequestBody VoterRegistryRequest request) {

        logger.info("Adding voter to registry for organization: {}", request.getOrganizationId());
        VoterRegistryResponse response = voterRegistryService.addVoter(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Voter added to registry successfully"));
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get organization registry", description = "Gets all voters in organization registry")
    public ResponseEntity<ApiResponse<List<VoterRegistryResponse>>> getOrganizationRegistry(
            @PathVariable Long organizationId,
            @RequestParam(value = "used", required = false) Boolean used) {

        logger.debug("Getting voter registry for organization: {}", organizationId);
        List<VoterRegistryResponse> responses = voterRegistryService.getVotersByOrganization(organizationId, used);

        return ResponseEntity.ok(ApiResponse.success(responses, "Voter registry retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get voter by ID", description = "Gets voter registry entry by ID")
    public ResponseEntity<ApiResponse<VoterRegistryResponse>> getVoter(@PathVariable Long id) {

        logger.debug("Getting voter by ID: {}", id);
        VoterRegistryResponse response = voterRegistryService.getVoterById(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Voter retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Update voter", description = "Updates voter registry entry")
    public ResponseEntity<ApiResponse<VoterRegistryResponse>> updateVoter(
            @PathVariable Long id,
            @Valid @RequestBody VoterRegistryRequest request) {

        logger.info("Updating voter ID: {}", id);
        VoterRegistryResponse response = voterRegistryService.updateVoter(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Voter updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Delete voter", description = "Deletes voter from registry")
    public ResponseEntity<ApiResponse<Void>> deleteVoter(@PathVariable Long id) {

        logger.info("Deleting voter ID: {}", id);
        voterRegistryService.deleteVoter(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Voter deleted successfully"));
    }

    @GetMapping("/summary/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get registry summary", description = "Gets registry usage statistics")
    public ResponseEntity<ApiResponse<VoterRegistrySummaryResponse>> getRegistrySummary(
            @PathVariable Long organizationId) {

        logger.debug("Getting registry summary for organization: {}", organizationId);
        VoterRegistrySummaryResponse response = voterRegistryService.getRegistrySummary(organizationId);

        return ResponseEntity.ok(ApiResponse.success(response, "Registry summary retrieved successfully"));
    }

    @PostMapping("/{id}/mark-voted")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Mark voter as voted", description = "Manually marks a voter as having voted")
    public ResponseEntity<ApiResponse<VoterRegistryResponse>> markVoterAsVoted(@PathVariable Long id) {

        logger.info("Marking voter as voted ID: {}", id);
        VoterRegistryResponse response = voterRegistryService.markVoterAsVoted(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Voter marked as voted successfully"));
    }

    @PostMapping("/{id}/reset")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Reset voter status", description = "Resets voter to unused status")
    public ResponseEntity<ApiResponse<VoterRegistryResponse>> resetVoterStatus(@PathVariable Long id) {

        logger.info("Resetting voter status ID: {}", id);
        VoterRegistryResponse response = voterRegistryService.resetVoterStatus(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Voter status reset successfully"));
    }

    @GetMapping("/search/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Search voters", description = "Searches voters in registry")
    public ResponseEntity<ApiResponse<List<VoterRegistryResponse>>> searchVoters(
            @PathVariable Long organizationId,
            @RequestParam(value = "matricNumber", required = false) String matricNumber,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(value = "fullName", required = false) String fullName) {

        logger.debug("Searching voters in organization: {}", organizationId);
        List<VoterRegistryResponse> responses = voterRegistryService.searchVoters(
                organizationId, matricNumber, email, phone, fullName);

        return ResponseEntity.ok(ApiResponse.success(responses, "Voters retrieved successfully"));
    }
}