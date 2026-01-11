package vote.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.VoterRegistry;

@Repository
public interface VoterRegistryRepository extends JpaRepository<VoterRegistry, Long> {

    List<VoterRegistry> findByOrganizationId(Long organizationId);

    List<VoterRegistry> findByOrganizationIdAndUsedFalse(Long organizationId);

    List<VoterRegistry> findByOrganizationIdAndUsedTrue(Long organizationId);

    Optional<VoterRegistry> findByOrganizationIdAndMatricNumber(Long organizationId, String matricNumber);

    Optional<VoterRegistry> findByOrganizationIdAndEmail(Long organizationId, String email);

    Optional<VoterRegistry> findByOrganizationIdAndPhone(Long organizationId, String phone);

    @Query("SELECT vr FROM VoterRegistry vr WHERE vr.organization.id = :orgId " +
           "AND vr.used = false " +
           "AND (:matricNumber IS NULL OR vr.matricNumber = :matricNumber) " +
           "AND (:email IS NULL OR vr.email = :email) " +
           "AND (:phone IS NULL OR vr.phone = :phone)")
    Optional<VoterRegistry> findEligibleVoter(
            @Param("orgId") Long organizationId,
            @Param("matricNumber") String matricNumber,
            @Param("email") String email,
            @Param("phone") String phone);

    long countByOrganizationId(Long organizationId);

    long countByOrganizationIdAndUsedTrue(Long organizationId);

    long countByOrganizationIdAndUsedFalse(Long organizationId);

    @Query("SELECT vr FROM VoterRegistry vr WHERE vr.organization.id = :orgId " +
           "AND (vr.matricNumber LIKE %:searchTerm% OR vr.email LIKE %:searchTerm% OR " +
           "vr.phone LIKE %:searchTerm% OR vr.fullName LIKE %:searchTerm%)")
    List<VoterRegistry> searchVoters(@Param("orgId") Long organizationId,
                                    @Param("searchTerm") String searchTerm);

    @Query("SELECT vr FROM VoterRegistry vr WHERE vr.organization.id = :orgId " +
           "AND vr.used = false " +
           "AND vr.verificationAttempts >= 5")
    List<VoterRegistry> findLockedVoters(@Param("orgId") Long organizationId);

    @Query("SELECT vr FROM VoterRegistry vr WHERE vr.votedAt BETWEEN :startDate AND :endDate")
    List<VoterRegistry> findVotersByVoteDateRange(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(vr) FROM VoterRegistry vr WHERE vr.organization.id = :orgId " +
           "AND vr.createdAt BETWEEN :startDate AND :endDate")
    long countVotersRegisteredBetween(@Param("orgId") Long organizationId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(vr) FROM VoterRegistry vr WHERE vr.organization.id = :orgId " +
           "AND vr.votedAt BETWEEN :startDate AND :endDate")
    long countVotersVotedBetween(@Param("orgId") Long organizationId,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
}