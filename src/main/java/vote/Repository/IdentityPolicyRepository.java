package vote.Repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.IdentityPolicy;
import vote.Enum.OTPChannel;

@Repository
public interface IdentityPolicyRepository extends JpaRepository<IdentityPolicy, Long> {

    List<IdentityPolicy> findByOrganizationId(Long organizationId);

    Optional<IdentityPolicy> findByOrganizationIdAndActiveTrue(Long organizationId);

    Optional<IdentityPolicy> findByOrganizationIdAndActiveTrueAndLockedFalse(Long organizationId);

    List<IdentityPolicy> findByOtpChannel(OTPChannel otpChannel);

    @Query("SELECT ip FROM IdentityPolicy ip WHERE ip.organization.id = :orgId AND :fieldName MEMBER OF ip.identifierFields")
    List<IdentityPolicy> findByOrganizationAndField(
            @Param("orgId") Long organizationId,
            @Param("fieldName") String fieldName);

    @Query("SELECT ip FROM IdentityPolicy ip WHERE ip.active = true AND ip.locked = false")
    List<IdentityPolicy> findAllActiveUnlockedPolicies();

    @Query("SELECT ip FROM IdentityPolicy ip WHERE ip.organization.id = :orgId AND ip.active = true ORDER BY ip.createdAt DESC")
    List<IdentityPolicy> findActivePoliciesByOrganization(@Param("orgId") Long organizationId);

    @Query("SELECT COUNT(ip) FROM IdentityPolicy ip WHERE ip.organization.id = :orgId")
    long countByOrganization(@Param("orgId") Long organizationId);

    @Query("SELECT ip FROM IdentityPolicy ip WHERE ip.locked = true")
    List<IdentityPolicy> findAllLockedPolicies();
}