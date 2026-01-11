package vote.Controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import vote.Request.VoteRequest;
import vote.Response.ApiResponse;
import vote.Response.VoteResponse;
import vote.Service.VoteService;

@RestController
@RequestMapping("/api/votes")
@Tag(name = "Voting", description = "Voting APIs")
@SecurityRequirement(name = "bearerAuth")
public class VotingController {

    private static final Logger logger = LoggerFactory.getLogger(VotingController.class);

    private final VoteService voteService;

    public VotingController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    @PreAuthorize("hasRole('VOTER')")
    @Operation(summary = "Cast vote", description = "Casts a vote in an election")
    public ResponseEntity<ApiResponse<VoteResponse>> castVote(
            @Valid @RequestBody VoteRequest request,
            HttpServletRequest servletRequest) {

        logger.info("Casting vote for election ID: {}", request.getElectionId());

        // Get client IP and user agent
        String ipAddress = getClientIpAddress(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        VoteResponse response = voteService.castVote(request, ipAddress, userAgent);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Vote cast successfully"));
    }

    @GetMapping("/election/{electionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get election votes", description = "Gets all votes for an election")
    public ResponseEntity<ApiResponse<List<VoteResponse>>> getElectionVotes(@PathVariable Long electionId) {

        logger.debug("Getting votes for election ID: {}", electionId);
        List<VoteResponse> responses = voteService.getVotesByElection(electionId);

        return ResponseEntity.ok(ApiResponse.success(responses, "Votes retrieved successfully"));
    }

    @GetMapping("/{voteId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get vote by ID", description = "Gets vote details by ID")
    public ResponseEntity<ApiResponse<VoteResponse>> getVote(@PathVariable Long voteId) {

        logger.debug("Getting vote by ID: {}", voteId);
        VoteResponse response = voteService.getVoteById(voteId);

        return ResponseEntity.ok(ApiResponse.success(response, "Vote retrieved successfully"));
    }

    @GetMapping("/voter/{voterRegistryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN')")
    @Operation(summary = "Get voter's votes", description = "Gets all votes by a voter")
    public ResponseEntity<ApiResponse<List<VoteResponse>>> getVoterVotes(@PathVariable Long voterRegistryId) {

        logger.debug("Getting votes for voter registry ID: {}", voterRegistryId);
        List<VoteResponse> responses = voteService.getVotesByVoter(voterRegistryId);

        return ResponseEntity.ok(ApiResponse.success(responses, "Voter votes retrieved successfully"));
    }

    @GetMapping("/election/{electionId}/has-voted/{voterRegistryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Check if voter has voted", description = "Checks if voter has voted in election")
    public ResponseEntity<ApiResponse<Boolean>> hasVoted(
            @PathVariable Long electionId,
            @PathVariable Long voterRegistryId) {

        logger.debug("Checking if voter {} has voted in election {}", voterRegistryId, electionId);
        boolean hasVoted = voteService.hasVoted(electionId, voterRegistryId);

        return ResponseEntity.ok(ApiResponse.success(hasVoted, "Vote status checked"));
    }

    @GetMapping("/election/{electionId}/count")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Get vote count", description = "Gets total votes count for election")
    public ResponseEntity<ApiResponse<Long>> getElectionVoteCount(@PathVariable Long electionId) {

        logger.debug("Getting vote count for election ID: {}", electionId);
        long voteCount = voteService.getElectionVoteCount(electionId);

        return ResponseEntity.ok(ApiResponse.success(voteCount, "Vote count retrieved"));
    }

    @GetMapping("/candidate/{candidateId}/count")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ORGANIZATION_ADMIN', 'VOTER')")
    @Operation(summary = "Get candidate vote count", description = "Gets vote count for candidate")
    public ResponseEntity<ApiResponse<Long>> getCandidateVoteCount(@PathVariable Long candidateId) {

        logger.debug("Getting vote count for candidate ID: {}", candidateId);
        long voteCount = voteService.getCandidateVoteCount(candidateId);

        return ResponseEntity.ok(ApiResponse.success(voteCount, "Candidate vote count retrieved"));
    }

    // Helper method to get client IP address
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // If multiple IPs, take the first one
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }
}