package vote.Service;



import java.util.List;

import vote.Request.ElectionRequest;
import vote.Response.ElectionResponse;
import vote.Response.ElectionSummaryResponse;

public interface ElectionService {

    ElectionResponse createElection(ElectionRequest request);

    ElectionResponse getElectionById(Long id);

    List<ElectionResponse> getElectionsByOrganization(Long organizationId, String status);

    List<ElectionResponse> getActiveElections(Long organizationId);

    ElectionResponse updateElection(Long id, ElectionRequest request);

    void deleteElection(Long id);

    ElectionResponse activateElection(Long id);

    ElectionResponse pauseElection(Long id);

    ElectionResponse completeElection(Long id);

    ElectionResponse publishResults(Long id);

    ElectionSummaryResponse getElectionSummary(Long id);

    boolean isElectionActive(Long id);

    void updateElectionStatuses(); // For scheduled task
}