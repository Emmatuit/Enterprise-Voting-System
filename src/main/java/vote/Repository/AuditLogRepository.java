package vote.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vote.Entity.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByOrganizationId(Long organizationId);

    List<AuditLog> findByAction(String action);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate")
    List<AuditLog> findByTimestampBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.action = :action " +
           "ORDER BY a.timestamp DESC")
    List<AuditLog> findByUserAndAction(@Param("userId") Long userId,
                                      @Param("action") String action);

    @Query("SELECT a FROM AuditLog a WHERE a.organizationId = :orgId " +
           "AND a.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY a.timestamp DESC")
    List<AuditLog> findOrganizationLogsBetween(@Param("orgId") Long organizationId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a.action, COUNT(a) FROM AuditLog a WHERE a.organizationId = :orgId " +
           "GROUP BY a.action")
    List<Object[]> countActionsByOrganization(@Param("orgId") Long organizationId);

    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ipAddress " +
           "AND a.timestamp > :sinceDate")
    List<AuditLog> findByIpAddressSince(@Param("ipAddress") String ipAddress,
                                       @Param("sinceDate") LocalDateTime sinceDate);
}