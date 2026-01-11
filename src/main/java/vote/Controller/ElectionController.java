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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vote.Request.ElectionRequest;
import vote.Response.ApiResponse;
import vote.Response.ElectionResponse;
import vote.Response.ElectionSummaryResponse;
import vote.Service.ElectionService;

@RestController
@RequestMapping("/api/admin/elections")
@Tag(name = "Election Management", description = "Election management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ElectionController {

    private static final Logger logger = LoggerFactory.getLogger(ElectionController.class);

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Create election", description = "Creates a new election")
    public ResponseEntity<ApiResponse<ElectionResponse>> createElection(
            @Valid @RequestBody ElectionRequest request) {

        logger.info("Creating election: {}", request.getTitle());
        ElectionResponse response = electionService.createElection(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Election created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Get election by ID", description = "Retrieves election details by ID")
    public ResponseEntity<ApiResponse<ElectionResponse>> getElection(@PathVariable Long id) {

        logger.debug("Getting election by ID: {}", id);
        ElectionResponse response = electionService.getElectionById(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Election retrieved successfully"));
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Get organization elections", description = "Gets all elections for an organization")
    public ResponseEntity<ApiResponse<List<ElectionResponse>>> getOrganizationElections(
            @PathVariable Long organizationId,
            @RequestParam(value = "status", required = false) String status) {

        logger.debug("Getting elections for organization: {}", organizationId);
        List<ElectionResponse> responses = electionService.getElectionsByOrganization(organizationId, status);

        return ResponseEntity.ok(ApiResponse.success(responses, "Elections retrieved successfully"));
    }

    @GetMapping("/public/organization/{organizationId}/active")
    @Operation(summary = "Get active elections (public)", description = "Gets active elections for organization (public)")
    public ResponseEntity<ApiResponse<List<ElectionResponse>>> getActiveElections(
            @PathVariable Long organizationId) {

        logger.debug("Getting active elections for organization: {}", organizationId);
        List<ElectionResponse> responses = electionService.getActiveElections(organizationId);

        return ResponseEntity.ok(ApiResponse.success(responses, "Active elections retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Update election", description = "Updates election details")
    public ResponseEntity<ApiResponse<ElectionResponse>> updateElection(
            @PathVariable Long id,
            @Valid @RequestBody ElectionRequest request) {

        logger.info("Updating election ID: {}", id);
        ElectionResponse response = electionService.updateElection(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Election updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Delete election", description = "Deletes an election")
    public ResponseEntity<ApiResponse<Void>> deleteElection(@PathVariable Long id) {

        logger.info("Deleting election ID: {}", id);
        electionService.deleteElection(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Election deleted successfully"));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Activate election", description = "Activates an election")
    public ResponseEntity<ApiResponse<ElectionResponse>> activateElection(@PathVariable Long id) {

        logger.info("Activating election ID: {}", id);
        ElectionResponse response = electionService.activateElection(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Election activated successfully"));
    }

    @PostMapping("/{id}/pause")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Pause election", description = "Pauses an active election")
    public ResponseEntity<ApiResponse<ElectionResponse>> pauseElection(@PathVariable Long id) {

        logger.info("Pausing election ID: {}", id);
        ElectionResponse response = electionService.pauseElection(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Election paused successfully"));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Complete election", description = "Completes an election")
    public ResponseEntity<ApiResponse<ElectionResponse>> completeElection(@PathVariable Long id) {

        logger.info("Completing election ID: {}", id);
        ElectionResponse response = electionService.completeElection(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Election completed successfully"));
    }

    @PostMapping("/{id}/publish-results")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Publish results", description = "Publishes election results")
    public ResponseEntity<ApiResponse<ElectionResponse>> publishResults(@PathVariable Long id) {

        logger.info("Publishing results for election ID: {}", id);
        ElectionResponse response = electionService.publishResults(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Results published successfully"));
    }

    @GetMapping("/{id}/summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get election summary", description = "Gets election statistics and summary")
    public ResponseEntity<ApiResponse<ElectionSummaryResponse>> getElectionSummary(@PathVariable Long id) {

        logger.debug("Getting summary for election ID: {}", id);
        ElectionSummaryResponse response = electionService.getElectionSummary(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Election summary retrieved successfully"));
    }

    @GetMapping("/{id}/check-status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Check election status", description = "Checks if election is active and ongoing")
    public ResponseEntity<ApiResponse<Boolean>> checkElectionStatus(@PathVariable Long id) {

        logger.debug("Checking status for election ID: {}", id);
        boolean isActive = electionService.isElectionActive(id);

        return ResponseEntity.ok(ApiResponse.success(isActive, "Election status checked"));
    }
}