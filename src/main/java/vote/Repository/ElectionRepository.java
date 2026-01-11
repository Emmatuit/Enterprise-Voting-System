package vote.Repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.Election;
import vote.Enum.ElectionStatus;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    List<Election> findByOrganizationId(Long organizationId);

    List<Election> findByOrganizationIdAndStatus(Long organizationId, ElectionStatus status);

    List<Election> findByStatus(ElectionStatus status);

    @Query("SELECT e FROM Election e WHERE e.organization.id = :orgId AND e.startTime <= :now AND e.endTime >= :now")
    List<Election> findActiveElectionsByOrganization(@Param("orgId") Long organizationId,
                                                    @Param("now") LocalDateTime now);

    @Query("SELECT e FROM Election e WHERE e.startTime <= :now AND e.endTime >= :now")
    List<Election> findAllActiveElections(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Election e WHERE e.endTime < :now AND e.status = 'ACTIVE'")
    List<Election> findElectionsToComplete(@Param("now") LocalDateTime now);

    long countByOrganizationId(Long organizationId);

    long countByOrganizationIdAndStatus(Long organizationId, ElectionStatus status);

    @Query("SELECT e FROM Election e WHERE e.organization.id = :orgId " +
           "AND (e.title LIKE %:searchTerm% OR e.description LIKE %:searchTerm%)")
    List<Election> searchElections(@Param("orgId") Long organizationId,
                                  @Param("searchTerm") String searchTerm);

    @Query("SELECT e FROM Election e WHERE e.resultsPublished = true " +
           "AND e.organization.id = :orgId " +
           "ORDER BY e.endTime DESC")
    List<Election> findPublishedResults(@Param("orgId") Long organizationId);

    @Query("SELECT e FROM Election e WHERE e.startTime > :date")
    List<Election> findUpcomingElections(@Param("date") LocalDateTime date);

    @Query("SELECT e FROM Election e WHERE e.endTime < :date AND e.resultsPublished = false")
    List<Election> findCompletedUnpublishedElections(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(e) FROM Election e WHERE e.organization.id = :orgId " +
           "AND e.startTime BETWEEN :startDate AND :endDate")
    long countElectionsStartedBetween(@Param("orgId") Long organizationId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}