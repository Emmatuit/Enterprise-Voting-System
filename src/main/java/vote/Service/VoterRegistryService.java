package vote.Service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import vote.Request.VoterRegistryRequest;
import vote.Response.VoterRegistryResponse;
import vote.Response.VoterRegistrySummaryResponse;

public interface VoterRegistryService {

    VoterRegistrySummaryResponse processRegistryFile(MultipartFile file, Long organizationId, boolean overwrite);

    VoterRegistryResponse addVoter(VoterRegistryRequest request);

    VoterRegistryResponse getVoterById(Long id);

    List<VoterRegistryResponse> getVotersByOrganization(Long organizationId, Boolean used);

    VoterRegistryResponse updateVoter(Long id, VoterRegistryRequest request);

    void deleteVoter(Long id);

    VoterRegistrySummaryResponse getRegistrySummary(Long organizationId);

    VoterRegistryResponse markVoterAsVoted(Long id);

    VoterRegistryResponse resetVoterStatus(Long id);

    List<VoterRegistryResponse> searchVoters(Long organizationId, String matricNumber,
                                           String email, String phone, String fullName);

    boolean isVoterEligible(Long organizationId, String matricNumber, String email, String phone);
}