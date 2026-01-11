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
import vote.Request.CandidateRequest;
import vote.Response.ApiResponse;
import vote.Response.CandidateResponse;
import vote.Service.CandidateService;

@RestController
@RequestMapping("/api/admin/elections/{electionId}/candidates")
@Tag(name = "Candidate Management", description = "Candidate management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CandidateController {

    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Add candidate to election", description = "Adds a candidate to an election")
    public ResponseEntity<ApiResponse<CandidateResponse>> addCandidate(
            @PathVariable Long electionId,
            @Valid @RequestBody CandidateRequest request) {

        logger.info("Adding candidate to election ID: {}", electionId);
        CandidateResponse response = candidateService.addCandidate(electionId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Candidate added successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Get election candidates", description = "Gets all candidates for an election")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getElectionCandidates(
            @PathVariable Long electionId,
            @RequestParam(value = "activeOnly", defaultValue = "true") boolean activeOnly) {

        logger.debug("Getting candidates for election ID: {}", electionId);
        List<CandidateResponse> responses = candidateService.getCandidatesByElection(electionId, activeOnly);

        return ResponseEntity.ok(ApiResponse.success(responses, "Candidates retrieved successfully"));
    }

    @GetMapping("/public")
    @Operation(summary = "Get election candidates (public)",
               description = "Gets candidates for election (public)")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getPublicElectionCandidates(
            @PathVariable Long electionId) {

        logger.debug("Getting public candidates for election ID: {}", electionId);
        List<CandidateResponse> responses = candidateService.getActiveCandidatesByElection(electionId);

        return ResponseEntity.ok(ApiResponse.success(responses, "Candidates retrieved successfully"));
    }

    @GetMapping("/{candidateId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Get candidate by ID", description = "Gets candidate details by ID")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidate(
            @PathVariable Long electionId,
            @PathVariable Long candidateId) {

        logger.debug("Getting candidate ID: {} for election ID: {}", candidateId, electionId);
        CandidateResponse response = candidateService.getCandidateById(candidateId, electionId);

        return ResponseEntity.ok(ApiResponse.success(response, "Candidate retrieved successfully"));
    }

    @PutMapping("/{candidateId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Update candidate", description = "Updates candidate details")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateCandidate(
            @PathVariable Long electionId,
            @PathVariable Long candidateId,
            @Valid @RequestBody CandidateRequest request) {

        logger.info("Updating candidate ID: {}", candidateId);
        CandidateResponse response = candidateService.updateCandidate(candidateId, electionId, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Candidate updated successfully"));
    }

    @DeleteMapping("/{candidateId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Delete candidate", description = "Deletes a candidate")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(
            @PathVariable Long electionId,
            @PathVariable Long candidateId) {

        logger.info("Deleting candidate ID: {}", candidateId);
        candidateService.deleteCandidate(candidateId, electionId);

        return ResponseEntity.ok(ApiResponse.success(null, "Candidate deleted successfully"));
    }

    @PostMapping("/{candidateId}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Activate candidate", description = "Activates a candidate")
    public ResponseEntity<ApiResponse<CandidateResponse>> activateCandidate(
            @PathVariable Long electionId,
            @PathVariable Long candidateId) {

        logger.info("Activating candidate ID: {}", candidateId);
        CandidateResponse response = candidateService.activateCandidate(candidateId, electionId);

        return ResponseEntity.ok(ApiResponse.success(response, "Candidate activated successfully"));
    }

    @PostMapping("/{candidateId}/deactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Deactivate candidate", description = "Deactivates a candidate")
    public ResponseEntity<ApiResponse<CandidateResponse>> deactivateCandidate(
            @PathVariable Long electionId,
            @PathVariable Long candidateId) {

        logger.info("Deactivating candidate ID: {}", candidateId);
        CandidateResponse response = candidateService.deactivateCandidate(candidateId, electionId);

        return ResponseEntity.ok(ApiResponse.success(response, "Candidate deactivated successfully"));
    }

    @GetMapping("/{candidateId}/vote-count")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Get candidate vote count", description = "Gets vote count for a candidate")
    public ResponseEntity<ApiResponse<Long>> getCandidateVoteCount(
            @PathVariable Long electionId,
            @PathVariable Long candidateId) {

        logger.debug("Getting vote count for candidate ID: {}", candidateId);
        long voteCount = candidateService.getCandidateVoteCount(candidateId);

        return ResponseEntity.ok(ApiResponse.success(voteCount, "Vote count retrieved"));
    }
}
