package vote.Service;



import java.util.List;

import vote.Request.IdentityPolicyRequest;
import vote.Response.IdentityPolicyResponse;

public interface IdentityPolicyService {

    IdentityPolicyResponse createPolicy(IdentityPolicyRequest request);

    IdentityPolicyResponse getPolicyById(Long id);

    List<IdentityPolicyResponse> getPoliciesByOrganization(Long organizationId);

    IdentityPolicyResponse getActivePolicy(Long organizationId);

    IdentityPolicyResponse updatePolicy(Long id, IdentityPolicyRequest request);

    IdentityPolicyResponse lockPolicy(Long id);

    IdentityPolicyResponse activatePolicy(Long id);

    IdentityPolicyResponse deactivatePolicy(Long id);

    void deletePolicy(Long id);

    List<String> getVerificationFields(Long organizationId);

    boolean isPolicyLocked(Long organizationId);
}