package vote.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.OTPCode;

@Repository
public interface OTPCodeRepository extends JpaRepository<OTPCode, Long> {

    Optional<OTPCode> findByIdentifierAndCodeAndUsedFalse(String identifier, String code);

    List<OTPCode> findByIdentifierAndPurposeAndUsedFalse(String identifier, String purpose);

    @Query("SELECT o FROM OTPCode o WHERE o.identifier = :identifier AND o.purpose = :purpose AND o.used = false AND o.expiresAt > :now ORDER BY o.createdAt DESC")
    List<OTPCode> findValidOTPs(@Param("identifier") String identifier,
                               @Param("purpose") String purpose,
                               @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE OTPCode o SET o.used = true WHERE o.identifier = :identifier AND o.purpose = :purpose AND o.used = false")
    void invalidateAllOTPs(@Param("identifier") String identifier,
                          @Param("purpose") String purpose);

    @Modifying
    @Query("DELETE FROM OTPCode o WHERE o.expiresAt < :expiryDate")
    void deleteExpiredOTPs(@Param("expiryDate") LocalDateTime expiryDate);

    long countByIdentifierAndPurpose(String identifier, String purpose);

    @Query("SELECT o FROM OTPCode o WHERE o.organization.id = :orgId")
    List<OTPCode> findByOrganizationId(@Param("orgId") Long organizationId);

    @Query("SELECT o FROM OTPCode o WHERE o.used = true AND o.usedAt BETWEEN :startDate AND :endDate")
    List<OTPCode> findUsedOTPsBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM OTPCode o WHERE o.used = false AND o.expiresAt < :now")
    List<OTPCode> findExpiredUnusedOTPs(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(o) FROM OTPCode o WHERE o.organization.id = :orgId " +
           "AND o.createdAt BETWEEN :startDate AND :endDate")
    long countOTPsGeneratedBetween(@Param("orgId") Long organizationId,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o.purpose, COUNT(o) FROM OTPCode o WHERE o.organization.id = :orgId " +
           "GROUP BY o.purpose")
    List<Object[]> countOTPsByPurpose(@Param("orgId") Long organizationId);

    @Query("SELECT o.channel, COUNT(o) FROM OTPCode o WHERE o.organization.id = :orgId " +
           "GROUP BY o.channel")
    List<Object[]> countOTPsByChannel(@Param("orgId") Long organizationId);
}
